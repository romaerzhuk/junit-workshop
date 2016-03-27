package ru.iteco.test.utils.hamcrest;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Сравнивает объект по его свойствам
 * 
 * @param <T>
 *            тип объекта
 * @author Роман Ержуков I-Teco 15 апр. 2014 г.
 */
public abstract class PropertiesMatcher<T> extends BaseMatcher<T> {
	private final String name;
	private List<String> list;
	private StringBuilder sb;

	/**
	 * Сравнивает объект по его свойствам
	 * 
	 * @param <T>
	 *            тип объекта
	 * @author Роман Ержуков I-Teco 02 сент. 2014 г.
	 */
	public interface Checker<T> {
		/**
		 * Сравнивает объект по его свойствам
		 * 
		 * @param a
		 *            актуальный объект
		 * @param m
		 *            создаёт результат сраврения
		 */
		void check(T a, PropertiesMatcher<T> m);
	}

	/**
	 * Создаёт эталонный объект для сравнения
	 * 
	 * @param name
	 *            имя эталонного объекта
	 * @param checker
	 *            метод сравнения объектов
	 * @return {@link Matcher}
	 */
	public static <T> Matcher<T> of(String name, final Checker<T> checker) {
		return new PropertiesMatcher<T>(name) {
			@Override
			protected void check(T item) {
				checker.check(item, this);
			}
		};
	}

	public PropertiesMatcher(String name) {
		this.name = name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final boolean matches(Object item) {
		list = null;
		try {
			check((T) item);
		} catch (Throwable t) {
			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			}
			if (t instanceof Error) {
				throw (Error) t;
			}
			throw new RuntimeException(t);
		}
		return list == null;
	}

	@Override
	public void describeTo(Description d) {
		d.appendText(name + (list != null ? list : ""));
	}

	/**
	 * Проверяет актуальный объект на совпадение с ожидаемым
	 * 
	 * @param it
	 *            актуальный объект
	 * @throws Throwable
	 */
	protected abstract void check(T it) throws Throwable;

	/**
	 * Проверяет свойства объекта на совпадение
	 * 
	 * @param name
	 *            имя свойства
	 * @param actual
	 *            актуальное значение
	 * @param expected
	 *            ожидаемое значение
	 * @return true, если объект равны, иначе false
	 */
	public boolean add(String name, Object actual, Object expected) {
		if (!equal(actual, expected)) {
			if (list == null) {
				list = new ArrayList<String>();
				sb = new StringBuilder();
			}
			sb.append('{').append(name).append(": ").append(actual).append(" != ").append(expected)
					.append('}');
			list.add(sb.toString());
			sb.setLength(0);
		}
		return list == null;
	}

	/**
	 * Возвращает признак равенства объектов
	 * 
	 * @return true, если объекты равны
	 */
	public boolean isEqual() {
		return list == null;
	}

	private static boolean equal(Object actual, Object expected) {
		if (actual == expected) {
			return true;
		}
		if (actual == null || expected == null) {
			return false;
		}
		return actual.equals(expected);
	}
}
