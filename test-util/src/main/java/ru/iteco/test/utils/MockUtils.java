package ru.iteco.test.utils;

import static org.mockito.Matchers.isNull;

import java.math.BigDecimal;
import java.util.Date;

import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import ru.iteco.test.utils.annotations.LogMock;

/**
 * Утилиты для работы с моками
 * 
 * @author Роман Ержуков, I-Teco 22.06.2012
 */
public final class MockUtils {
	private static InOrder inOrder;
	private static LoggerMock loggerMock;

	private MockUtils() {
	}

	/**
	 * Вызывает {@link InOrder#verify(Object)} для теста.
	 * <p>
	 * Verifies interaction in order. E.g:
	 * 
	 * <pre class="code">
	 * <code class="java">
	 * InOrder inOrder = inOrder(firstMock, secondMock);
	 * 
	 * inOrder.verify(firstMock, times(2)).someMethod("was called first two times");
	 * inOrder.verify(secondMock, atLeastOnce()).someMethod("was called second at least once");
	 * </code>
	 * </pre>
	 * 
	 * </p>
	 * See examples in javadoc for {@link Mockito} class
	 * 
	 * @param mock
	 *            to be verified
	 * @return mock object itself
	 */
	public static <T> T verifyInOrder(T mock) {
		return getInOrder().verify(mock);
	}

	/**
	 * Вызывает метод {@link InOrder#verify(Object)} для теста.
	 * <p>
	 * Verifies that no more interactions happened <b>in order</b>. Different
	 * from {@link Mockito#verifyNoMoreInteractions(Object...)} because the
	 * order of verification matters.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre class="code">
	 * <code class="java">
	 * mock.foo(); //1st
	 * mock.bar(); //2nd
	 * mock.baz(); //3rd
	 * 
	 * InOrder inOrder = inOrder(mock);
	 * 
	 * inOrder.verify(mock).bar(); //2n
	 * inOrder.verify(mock).baz(); //3rd (last method)
	 * 
	 * //passes because there are no more interactions after last method:
	 * inOrder.verifyNoMoreInteractions();
	 * 
	 * //however this fails because 1st method was not verified:
	 * Mockito.verifyNoMoreInteractions(mock);
	 * </code>
	 * </pre>
	 * 
	 * </p>
	 */
	public static <T> T verifyInOrder(T mock, VerificationMode mode) {
		return getInOrder().verify(mock, mode);
	}

	/**
	 * Вызывает {@link InOrder#verifyNoMoreInteractions()} для теста.
	 * <p>
	 * Вызывает {@link LoggerMock#verify()} для поля теста с аннотацией @
	 * {@link LogMock}.
	 * </p>
	 */
	public static void verifyInOrderNoMoreInteractions() {
		getInOrder().verifyNoMoreInteractions();
		getLoggerMock().verify();
	}

	/**
	 * Вызывает {@link Matchers#isNull()} и приводит результат к строке.
	 */
	public static String isNullString() {
		return (String) isNull();
	}

	/**
	 * Вызывает {@link Matchers#isNull()} и приводит результат к {@link Date}.
	 */
	public static Date isNullDate() {
		return (Date) isNull();
	}

	/**
	 * Вызывает {@link Matchers#isNull()} и приводит результат к
	 * {@link BigDecimal}.
	 */
	public static BigDecimal isNullBigDecimal() {
		return (BigDecimal) isNull();
	}

	/**
	 * Устанавливает макет логгера
	 * 
	 * @param log
	 *            макет логгера
	 */
	public static void setLoggerMock(LoggerMock log) {
		loggerMock = log;
	}

	/**
	 * Устанавливает верификатор порядка вызовов методов макетов
	 * 
	 * @param order
	 *            верификатор порядка вызовов методов макетов
	 */
	public static void setInOrder(InOrder order) {
		inOrder = order;
	}

	private static InOrder getInOrder() {
		return checkNotNull(inOrder);
	}

	private static LoggerMock getLoggerMock() {
		return checkNotNull(loggerMock);
	}

	private static <T> T checkNotNull(T object) {
		if (object != null) {
			return object;
		}
		throw new IllegalStateException("Должен быть использован соответсвующий @Rule");
	}
}
