package ru.iteco.test.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Параметризированный тест
 * 
 * @author Роман Ержуков I-Teco 30 июня 2014 г.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Parameterized {
	/**
	 * Имя метода, который параметризирует тест
	 * 
	 * @return имя метода. По умолчанию совпадает с именем теста
	 */
	String[] value() default "";
}
