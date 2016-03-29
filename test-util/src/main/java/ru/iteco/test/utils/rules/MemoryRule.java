package ru.iteco.test.utils.rules;

import static java.lang.System.err;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import ru.iteco.test.utils.Predicate;
import ru.iteco.test.utils.TestUtil;

/**
 * Освобождает память после прогона тестов
 * 
 * @author Роман Ержуков I-Teco 21 июня 2014 г.
 */
public final class MemoryRule {
	private static final Predicate<Field> object = new Predicate<Field>() {
		@Override
		public boolean apply(Field f) {
			return isObject(f);
		}
	};
	private static final Predicate<Field> noStaticObject = new Predicate<Field>() {
		@Override
		public boolean apply(Field f) {
			return !isStatic(f.getModifiers()) && isObject(f);
		}
	};
	private static final TestRule rule = new TestRule() {
		@Override
		public Statement apply(Statement base, Description description) {
			return base;
		}
	};
	private static Object test;

	/**
	 * Освобождает память после прогона тестов
	 * 
	 * @param targetTest
	 *            целевой тест
	 */
	public static TestRule of(Object targetTest) {
		if (test != null) {
			Predicate<Field> pred = test.getClass() != targetTest.getClass() ? object
					: noStaticObject;
			List<Field> fields = TestUtil.findFields(test.getClass(), pred);
			for (Field f : fields) {
				try {
					f.set(test, null);
				} catch (Throwable t) {
					err.printf("Невозможно записать null в поле %s\n", f);
					t.printStackTrace();
				}
			}
		}
		test = targetTest;
		return rule;
	}

	private static boolean isObject(Field f) {
		if (f.getType().isPrimitive()) {
			return false;
		}
		int mod = f.getModifiers();
		return !isFinal(mod) || !isStatic(mod);
	}
}
