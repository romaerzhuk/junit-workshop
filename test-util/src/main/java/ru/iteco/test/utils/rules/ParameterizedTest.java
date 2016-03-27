package ru.iteco.test.utils.rules;

/**
 * Выполняет параметризированный тест
 * 
 * @author Роман Ержуков I-Teco 30 июня 2014 г.
 */
public interface ParameterizedTest {
	/**
	 * Выполняет тест
	 * 
	 * @param value
	 *            значение параметра
	 */
	void run(Object value);
}
