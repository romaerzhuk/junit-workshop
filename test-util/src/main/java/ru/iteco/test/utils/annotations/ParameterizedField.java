package ru.iteco.test.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Параметры теста, которые обнуляются после окончания теста
 * 
 * @author Роман Ержуков I-Teco 30 июня 2014 г.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParameterizedField {
	/**
	 * Параметры теста
	 * 
	 * @return параметры теста
	 */
	String[] value() default "";
}
