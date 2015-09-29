package com.synaptix.taskmanager.engine.configuration.transform;

import java.util.*;

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
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.engine.taskdefinition.ISubTaskDefinition;

public class DefaultTaskChainCriteriaTransform extends AbstractTaskChainCriteriaTransform {

	@Override
	public IResult transformeToTasks(ITaskManagerConfiguration taskManagerConfiguration, String taskChainCriteria) {
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

	private IResult _createTasks(ITaskManagerConfiguration taskManagerConfiguration, AbstractGraphNode node) {
		if (node instanceof IdGraphNode) {
			IdGraphNode ign = (IdGraphNode) node;
			ISubTaskDefinition subTaskDefinition = taskManagerConfiguration.getTaskDefinitionRegistry().getSubTaskDefinition(ign.getId());
			ISubTask task = taskManagerConfiguration.getTaskFactory().newSubTask(subTaskDefinition);

			MyResult result = new MyResult();
			result.newSubTasks.add(task);
			result.nextSubTasks.add(task);
			return result;
		} else if (node instanceof ParallelGraphNode) {
			ParallelGraphNode pgn = (ParallelGraphNode) node;


			MyResult result = new MyResult();
			for (AbstractGraphNode subNode : pgn.getNodes()) {
				IResult subResult = _createTasks(taskManagerConfiguration, subNode);
				result.newSubTasks.addAll(subResult.getNewSubTasks());
				result.nextSubTasks.addAll(subResult.getNextSubTasks());
				result.linkNextTasksMap.putAll(subResult.getLinkNextTasksMap());
			}

			return result;
		} else if (node instanceof NextGraphNode) {
			NextGraphNode ngn = (NextGraphNode) node;

			IResult firstCr = _createTasks(taskManagerConfiguration, ngn.getFirstNode());
			IResult nextCr = _createTasks(taskManagerConfiguration, ngn.getNextNode());

			MyResult result = new MyResult();
			result.newSubTasks.addAll(firstCr.getNewSubTasks());
			result.nextSubTasks.addAll(firstCr.getNextSubTasks());
			result.linkNextTasksMap.putAll(firstCr.getLinkNextTasksMap());
			result.newSubTasks.addAll(nextCr.getNewSubTasks());
			result.linkNextTasksMap.putAll(nextCr.getLinkNextTasksMap());

				for (ISubTask firstTask : firstCr.getNewSubTasks()) {
					if (!firstCr.getLinkNextTasksMap().containsKey(firstTask)) {
						result.linkNextTasksMap.put(firstTask,nextCr.getNewSubTasks());
					}
				}

			return result;
		}
		return null;
	}

	private class MyResult implements IResult {

		List<ISubTask> newSubTasks = new ArrayList<ISubTask>();

		List<ISubTask> nextSubTasks = new ArrayList<ISubTask>();

		Map<ISubTask, List<ISubTask>> linkNextTasksMap = new HashMap<ISubTask, List<ISubTask>>();

		@Override
		public List<ISubTask> getNewSubTasks() {
			return newSubTasks;
		}

		@Override
		public List<ISubTask> getNextSubTasks() {
			return nextSubTasks;
		}

		@Override
		public Map<ISubTask, List<ISubTask>> getLinkNextTasksMap() {
			return linkNextTasksMap;
		}
	}
}
