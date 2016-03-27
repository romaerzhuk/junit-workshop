package ru.iteco.test.utils.rules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import ru.iteco.test.utils.TestUtil;
import ru.iteco.test.utils.annotations.BeforeMock;

/**
 * Вызывает методы с аннотацией {@link BeforeMock}
 * 
 * @author Роман Ержуков I-Teco 28 мая 2014 г.
 */
public class BeforeMockRule implements TestRule {
	private final Object targetTest;
	private final List<Method> methods;

	/**
	 * Вызывает методы с аннотацией {@link BeforeMock}
	 * 
	 * @param targetTest
	 *            тест
	 */
	public BeforeMockRule(Object targetTest) {
		this.targetTest = targetTest;
		this.methods = TestUtil.findMethods(targetTest.getClass(), BeforeMock.class);
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		if (methods.isEmpty()) {
			return base;
		}
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				for (Method m : methods) {
					try {
						m.invoke(targetTest);
					} catch (InvocationTargetException e) {
						throw e.getTargetException();
					}
				}
				base.evaluate();
			}
		};
	}
}
