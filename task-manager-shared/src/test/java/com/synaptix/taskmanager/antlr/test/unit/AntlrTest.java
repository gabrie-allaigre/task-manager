package com.synaptix.taskmanager.antlr.test.unit;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Assert;
import org.junit.Test;

import com.synaptix.taskmanager.antlr.AbstractGraphNode;
import com.synaptix.taskmanager.antlr.EvalGraphCalcVisitor;
import com.synaptix.taskmanager.antlr.GraphCalcLexer;
import com.synaptix.taskmanager.antlr.GraphCalcParser;
import com.synaptix.taskmanager.antlr.IdGraphNode;
import com.synaptix.taskmanager.antlr.ThrowingErrorListener;

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

			Assert.assertTrue(true);
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

			Assert.assertTrue(true);
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

			Assert.assertTrue(true);
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

			Assert.assertTrue(true);
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

			Assert.assertTrue(true);
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

			Assert.assertTrue(true);
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

	private static List<Class<?>> extract(AbstractGraphNode node) {
		List<Class<?>> res = new ArrayList<Class<?>>();
		if (node instanceof IdGraphNode) {
		}
		return res;
	}
}
