package com.synaptix.taskmanager.engine.configuration.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import com.synaptix.taskmanager.antlr.AbstractGraphNode;
import com.synaptix.taskmanager.antlr.EvalGraphCalcVisitor;
import com.synaptix.taskmanager.antlr.GraphCalcLexer;
import com.synaptix.taskmanager.antlr.GraphCalcParser;
import com.synaptix.taskmanager.antlr.GraphCalcParser.CompileContext;
import com.synaptix.taskmanager.antlr.IdGraphNode;
import com.synaptix.taskmanager.antlr.NextGraphNode;
import com.synaptix.taskmanager.antlr.ParallelGraphNode;
import com.synaptix.taskmanager.antlr.ThrowingErrorListener;
import com.synaptix.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.synaptix.taskmanager.engine.task.NormalTask;
import com.synaptix.taskmanager.engine.taskdefinition.INormalTaskDefinition;

public class DefaultTaskChainCriteriaTransform extends AbstractTaskChainCriteriaTransform {

	@Override
	public List<NormalTask> transformeToTasks(ITaskManagerConfiguration taskManagerConfiguration, String taskChainCriteria) {
		if (taskChainCriteria != null && !taskChainCriteria.isEmpty()) {
			try {
				AbstractGraphNode graphNode = new EvalGraphCalcVisitor().visit(compile(taskChainCriteria));
				return _createTasks(taskManagerConfiguration, graphNode);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
		return null;
	}

	private CompileContext compile(String rule) throws ParseCancellationException {
		GraphCalcLexer lex = new GraphCalcLexer(new ANTLRInputStream(rule));
		lex.removeErrorListeners();
		lex.addErrorListener(ThrowingErrorListener.INSTANCE);
		CommonTokenStream input = new CommonTokenStream(lex);
		GraphCalcParser parser = new GraphCalcParser(input);
		parser.removeErrorListeners();
		parser.addErrorListener(ThrowingErrorListener.INSTANCE);
		return parser.compile();

	}

	private List<NormalTask> _createTasks(ITaskManagerConfiguration taskManagerConfiguration, AbstractGraphNode node) {
		if (node instanceof IdGraphNode) {
			IdGraphNode ign = (IdGraphNode) node;
			INormalTaskDefinition normalTaskDefinition = taskManagerConfiguration.getTaskDefinitionRegistry().getNormalTaskDefinition(ign.getId());
			NormalTask task = taskManagerConfiguration.getTaskFactory().newNormalTask(normalTaskDefinition);
			return Arrays.asList(task);
		} else if (node instanceof ParallelGraphNode) {
			ParallelGraphNode pgn = (ParallelGraphNode) node;

			List<NormalTask> taskNodes = new ArrayList<NormalTask>();
			for (AbstractGraphNode subNode : pgn.getNodes()) {
				taskNodes.addAll(_createTasks(taskManagerConfiguration, subNode));
			}

			return taskNodes;
		} else if (node instanceof NextGraphNode) {
			NextGraphNode ngn = (NextGraphNode) node;

			List<NormalTask> firstCr = _createTasks(taskManagerConfiguration, ngn.getFirstNode());
			List<NormalTask> nextCr = _createTasks(taskManagerConfiguration, ngn.getNextNode());

			if (firstCr != null && nextCr != null) {
				for (NormalTask firstTask : firstCr) {
					for (NormalTask nextTask : nextCr) {
						firstTask.getNextTasks().add(nextTask);
					}
				}
			}

			return firstCr;
		}
		return null;
	}
}
