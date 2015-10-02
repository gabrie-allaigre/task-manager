package com.synaptix.taskmanager.antlr;

import com.synaptix.taskmanager.antlr.GraphCalcParser.*;

import java.util.List;
import java.util.stream.Collectors;

public class EvalGraphCalcVisitor extends GraphCalcBaseVisitor<AbstractGraphNode> {

	@Override
	public AbstractGraphNode visitCompile(CompileContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public AbstractGraphNode visitExpr(ExprContext ctx) {
		return visit(ctx.exprAnd());
	}

	@Override
	public AbstractGraphNode visitExprAnd(ExprAndContext ctx) {
		if (ctx.exprNext().size() > 1) {
			List<AbstractGraphNode> nodes = ctx.exprNext().stream().map(this::visit).collect(Collectors.toList());
			return new ParallelGraphNode(nodes);
		}
		return visit(ctx.exprNext(0));
	}

	@Override
	public AbstractGraphNode visitExprNext(ExprNextContext ctx) {
		AbstractGraphNode firstNode = visit(ctx.first);
		if (ctx.factor().size() > 1) {
			for (int i = 1; i < ctx.factor().size(); i++) {
				AbstractGraphNode nextNode = visit(ctx.factor(i));
				firstNode = new NextGraphNode(firstNode, nextNode);
			}
		}
		return firstNode;
	}

	@Override
	public AbstractGraphNode visitParens(ParensContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public AbstractGraphNode visitId(IdContext ctx) {
		return new IdGraphNode(ctx.getText());
	}
}