package ru.iteco.test.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Вызывает метод теста перед инициализацией Mock-ов.
 * 
 * @author Роман Ержуков I-Teco 28 мая 2014 г.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeMock {
}
