package com.synaptix.taskmanager.engine.configuration.transform;

import com.synaptix.taskmanager.antlr.*;
import com.synaptix.taskmanager.antlr.GraphCalcParser.CompileContext;
import com.synaptix.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.synaptix.taskmanager.engine.task.ISubTask;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private MyResult _createTasks(ITaskManagerConfiguration taskManagerConfiguration, AbstractGraphNode node) {
		if (node instanceof IdGraphNode) {
			IdGraphNode ign = (IdGraphNode) node;
			ISubTask task = taskManagerConfiguration.getTaskFactory().newSubTask(ign.getId());

			MyResult result = new MyResult();
			result.newSubTasks.add(task);
			result.nextSubTasks.add(task);
			return result;
		} else if (node instanceof ParallelGraphNode) {
			ParallelGraphNode pgn = (ParallelGraphNode) node;


			MyResult result = new MyResult();
			for (AbstractGraphNode subNode : pgn.getNodes()) {
				MyResult subResult = _createTasks(taskManagerConfiguration, subNode);
				result.newSubTasks.addAll(subResult.newSubTasks);
				result.nextSubTasks.addAll(subResult.nextSubTasks);
				result.linkNextTasksMap.putAll(subResult.linkNextTasksMap);
			}

			return result;
		} else if (node instanceof NextGraphNode) {
			NextGraphNode ngn = (NextGraphNode) node;

			MyResult firstCr = _createTasks(taskManagerConfiguration, ngn.getFirstNode());
			MyResult nextCr = _createTasks(taskManagerConfiguration, ngn.getNextNode());

			MyResult result = new MyResult();
			result.newSubTasks.addAll(firstCr.newSubTasks);
			result.nextSubTasks.addAll(firstCr.nextSubTasks);
			result.linkNextTasksMap.putAll(firstCr.linkNextTasksMap);
			result.newSubTasks.addAll(nextCr.newSubTasks);
			result.linkNextTasksMap.putAll(nextCr.linkNextTasksMap);

				for (ISubTask firstTask : firstCr.getNewSubTasks()) {
					if (!firstCr.getLinkNextTasksMap().containsKey(firstTask)) {
						result.linkNextTasksMap.put(firstTask,nextCr.newSubTasks);
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
