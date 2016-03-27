package ru.iteco.test.utils;

/**
 * Вычисляет условие
 * 
 * @param <T>
 *            тип входного параметра
 * @author Роман Ержуков I-Teco 28 мая 2014 г.
 */
public interface Predicate<T> {
	/**
	 * Вычисляет условие
	 * 
	 * @param input
	 *            входной параметр
	 * @return результат условия
	 */
	boolean apply(T input);
}
