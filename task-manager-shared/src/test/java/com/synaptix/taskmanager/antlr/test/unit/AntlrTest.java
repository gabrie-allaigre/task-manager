package com.synaptix.taskmanager.antlr.test.unit;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.taskmanager.antlr.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Assert;
import org.junit.Test;

public class AntlrTest {

	@Test
	public void test1() {
		try {
			GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream("A"));
			lex.removeErrorListeners();
			lex.addErrorListener(ThrowingErrorListener.INSTANCE);
			CommonTokenStream input = new CommonTokenStream(lex);
			GraphCalcParser parser = new GraphCalcParser(input);
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
			AbstractGraphNode graphNode = new EvalGraphCalcVisitor().visit(parser.compile());

			Assert.assertEquals("A",toString(graphNode));
		} catch (ParseCancellationException t) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void test2() {
		try {
			GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream("A->B"));
			lex.removeErrorListeners();
			lex.addErrorListener(ThrowingErrorListener.INSTANCE);
			CommonTokenStream input = new CommonTokenStream(lex);
			GraphCalcParser parser = new GraphCalcParser(input);
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
			parser.compile();

			Assert.assertTrue(false);
		} catch (ParseCancellationException t) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void test3() {
		try {
			GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream("A=>B"));
			lex.removeErrorListeners();
			lex.addErrorListener(ThrowingErrorListener.INSTANCE);
			CommonTokenStream input = new CommonTokenStream(lex);
			GraphCalcParser parser = new GraphCalcParser(input);
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
			AbstractGraphNode graphNode = new EvalGraphCalcVisitor().visit(parser.compile());

			Assert.assertEquals("(A=>B)", toString(graphNode));
		} catch (Throwable t) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void test4() {
		try {
			GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream("A>B"));
			lex.removeErrorListeners();
			lex.addErrorListener(ThrowingErrorListener.INSTANCE);
			CommonTokenStream input = new CommonTokenStream(lex);
			GraphCalcParser parser = new GraphCalcParser(input);
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
			parser.compile();

			Assert.assertTrue(false);
		} catch (Throwable t) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void test5() {
		try {
			GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream("A=>(B,C=>D)"));
			lex.removeErrorListeners();
			lex.addErrorListener(ThrowingErrorListener.INSTANCE);
			CommonTokenStream input = new CommonTokenStream(lex);
			GraphCalcParser parser = new GraphCalcParser(input);
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
			AbstractGraphNode graphNode = new EvalGraphCalcVisitor().visit(parser.compile());

			Assert.assertEquals("(A=>(B,(C=>D)))", toString(graphNode));
		} catch (Throwable t) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void test6() {
		try {
			GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream("(A,(E),F)=>(B,C=>D)"));
			lex.removeErrorListeners();
			lex.addErrorListener(ThrowingErrorListener.INSTANCE);
			CommonTokenStream input = new CommonTokenStream(lex);
			GraphCalcParser parser = new GraphCalcParser(input);
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
			AbstractGraphNode graphNode = new EvalGraphCalcVisitor().visit(parser.compile());

			Assert.assertEquals("((A,E,F)=>(B,(C=>D)))", toString(graphNode));
		} catch (Throwable t) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void test7() {
		try {
			GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream("A123_123-az"));
			lex.removeErrorListeners();
			lex.addErrorListener(ThrowingErrorListener.INSTANCE);
			CommonTokenStream input = new CommonTokenStream(lex);
			GraphCalcParser parser = new GraphCalcParser(input);
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
			AbstractGraphNode graphNode = new EvalGraphCalcVisitor().visit(parser.compile());

			Assert.assertEquals("A123_123-az", toString(graphNode));
		} catch (Throwable t) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void test8() {
		try {
			GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream("((A))"));
			lex.removeErrorListeners();
			lex.addErrorListener(ThrowingErrorListener.INSTANCE);
			CommonTokenStream input = new CommonTokenStream(lex);
			GraphCalcParser parser = new GraphCalcParser(input);
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
			AbstractGraphNode graphNode = new EvalGraphCalcVisitor().visit(parser.compile());

			Assert.assertEquals("A", toString(graphNode));
		} catch (Throwable t) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void test9() {
		try {
			GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream("(A&é"));
			lex.removeErrorListeners();
			lex.addErrorListener(ThrowingErrorListener.INSTANCE);
			CommonTokenStream input = new CommonTokenStream(lex);
			GraphCalcParser parser = new GraphCalcParser(input);
			parser.removeErrorListeners();
			parser.addErrorListener(ThrowingErrorListener.INSTANCE);
			parser.compile();

			Assert.assertTrue(false);
		} catch (Throwable t) {
			Assert.assertTrue(true);
		}
	}

	private static String toString(AbstractGraphNode node) {
		if (node instanceof IdGraphNode) {
			return ((IdGraphNode) node).getId();
		} else if (node instanceof ParallelGraphNode) {
			ParallelGraphNode pgn = (ParallelGraphNode) node;

			StringBuilder sb = new StringBuilder("(");
			boolean first = true;
			for (AbstractGraphNode child : pgn.getNodes()) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(toString(child));
			}
			sb.append(")");
			return sb.toString();
		} else if (node instanceof NextGraphNode) {
			NextGraphNode ngn = (NextGraphNode) node;

			String firstCr = toString(ngn.getFirstNode());
			String nextCr = toString(ngn.getNextNode());

			return "(" + firstCr + "=>" + nextCr + ")";
		}
		return "";
	}
}
