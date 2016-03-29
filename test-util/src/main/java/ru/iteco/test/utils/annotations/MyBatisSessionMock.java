package ru.iteco.test.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Класс, описывающий
 * 
 * @author Шлегер Андрей I-Teco 02 Июнь 2014 г.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MyBatisSessionMock {

	/**
	 * Список xml мепперов
	 * 
	 * @return список
	 */
	String[] mappers() default {};
}
