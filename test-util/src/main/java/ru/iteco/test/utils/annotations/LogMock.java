package ru.iteco.test.utils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.iteco.test.utils.LoggerMock;

/**
 * В тестах внедряет {@link LoggerMock}.
 *
 * @author Роман Ержуков I-Teco 12.09.2011
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LogMock {
    /**
     * Уровень логгирования Log4j, регистрируемый в {@link LoggerMock}
     * 
     * @return level
     */
    String level() default "INFO";
}
