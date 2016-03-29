package ru.iteco.test.utils.rules;

import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;
import static ru.iteco.test.utils.TestUtil.findMethods;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import ru.iteco.test.utils.Predicate;
import ru.iteco.test.utils.annotations.Parameterized;
import ru.iteco.test.utils.annotations.ParameterizedField;
import ru.iteco.test.utils.base.Defaults;

/**
 * Позволяет параметризировать тесты с помощью аннотации {@link Parameterized}
 * 
 * @author Роман Ержуков I-Teco 30 июня 2014 г.
 */
public class ParameterizedRule implements TestRule {
	private static class Data {
		List<Field> fields;
		Method method;

		void clean(Object targetTest) {
			if (fields != null) {
				for (Field f : fields) {
					try {
						f.set(targetTest, Defaults.defaultValue(f.getType()));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	private static final Predicate<Method> param = new Predicate<Method>() {
		@Override
		public boolean apply(Method method) {
			Class<?>[] types = method.getParameterTypes();
			return types.length == 1 && types[0] == ParameterizedTest.class;
		}
	};

	private final Object targetTest;
	private Map<String, Data> datas;

	/**
	 * Позволяет параметризировать тесты с помощью аннотации
	 * {@link Parameterized}
	 * 
	 * @param targetTest
	 *            целевой тест
	 */
	public ParameterizedRule(Object targetTest) {
		this.targetTest = targetTest;
	}

	@Override
	public Statement apply(Statement base, Description description) {
		Parameterized p = description.getAnnotation(Parameterized.class);
		if (p != null) {
			for (String name : p.value()) {
				base = statement(base, name, description);
			}
		}
		return base;
	}

	private Statement statement(final Statement base, String name, Description d) {
		final String methodName = !"".equals(name) ? name : d.getMethodName();
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				Data data = data(methodName);
				try {
					data.method.invoke(targetTest, test(base));
				} catch (InvocationTargetException e) {
					throw e.getTargetException();
				} finally {
					data.clean(targetTest);
				}
			}
		};
	}

	private Data data(String methodName) {
		if (datas == null) {
			datas = new HashMap<String, Data>();
			Class<?> klass = targetTest.getClass();
			for (Method m : findMethods(klass, param)) {
				Data data = new Data();
				data.method = m;
				ParameterizedField paramFields = m.getAnnotation(ParameterizedField.class);
				if (paramFields != null) {
					data.fields = fields(klass, paramFields.value());
				}
				datas.put(m.getName(), data);
			}
		}
		Data data = datas.get(methodName);
		if (data == null) {
			throw new RuntimeException("Не найден подходящий метод " + methodName);
		}
		return data;
	}

	private static List<Field> fields(Class<?> klass, String[] values) {
		List<Field> fields = new ArrayList<Field>(values.length);
		for (String v : values) {
			fields.add(getDeclaredField(klass, v, true));
		}
		return fields;
	}

	private static ParameterizedTest test(final Statement base) {
		return new ParameterizedTest() {
			@Override
			public void run(Object name) {
				try {
					base.evaluate();
				} catch (Throwable t) {
					String message = String.valueOf(name);
					if (t.getMessage() != null) {
						message += ": " + t.getMessage();
					}
					AssertionError e = new AssertionError(message);
					e.initCause(t);
					e.setStackTrace(t.getStackTrace());
					throw e;
				}
			}
		};
	}
}
