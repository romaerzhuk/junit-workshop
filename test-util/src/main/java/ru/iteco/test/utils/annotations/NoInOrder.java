package ru.iteco.test.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.iteco.test.utils.MockUtils;

/**
 * <p>
 * Аннотация для тестируемого класса, или метода. Отключает требование
 * использовать проверки порядка вызова методов
 * {@link MockUtils#verifyInOrder(Object)},
 * {@link MockUtils#verifyInOrderNoMoreInteractions()}.
 * </p>
 * <p>
 * Допускается испльзовать в крайних случаях, например, для старых тестов.
 * </p>
 * 
 * @author Роман Ержуков, I-Teco 25.06.2012
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface NoInOrder {
}
