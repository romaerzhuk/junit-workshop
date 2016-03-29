package ru.iteco.test.utils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Assert;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Трассировщик сообщений на консоль. Используется в тестах.
 * <p>
 * Чтобы отключить все другие Appender-ы, установите в параметрах JVM
 * <strong>-DTestTraceEnabled=true</strong>
 * </p>
 * <p>
 * Пример использования в JUnit-тесте:
 * 
 * <pre>
 * void setUp() {
 * 	trace = new LoggerMock();
 * }
 * 
 * void tearDown() {
 * 	trace.close(); // необходимо обязательно закрыть, во избежание утечек памяти
 * }
 * 
 * void test() {
 * 	trace.error(&quot;message&quot;, new Excepiton(&quot;fail!&quot;));
 * 	// тут какие-то действия, которые пишут в логгер
 * 	trace.verify();
 * }
 * </pre>
 * 
 * </p>
 * 
 * @author Роман Ержуков I-Teco 25.06.2009
 */
public class LoggerMock {
	private static class Data {
		final Level level;
		final String message;
		final Throwable cause;
		final boolean regExp;

		public Data(Level level, String message, Throwable cause, boolean regExp) {
			this.level = level;
			this.message = message;
			this.cause = cause;
			this.regExp = regExp;
		}

		private boolean equals(LoggingEvent actual) {
			if (actual == null) {
				return false;
			}
			EqualsBuilder eq = new EqualsBuilder();
			eq.append(level, actual.getLevel());
			Throwable ft = cause;
			Throwable et = cause(actual);
			if (ft != et) {
				eq.append(true, ft != null && et != null);
				if (eq.isEquals()) {
					Class<?> expectedClass = ft.getClass();
					if (expectedClass.isAnonymousClass()) {
						expectedClass = expectedClass.getSuperclass();
					}
					eq.append(expectedClass, et.getClass());
					append(eq, ft.getMessage(), et.getMessage());
				}
			}
			append(eq, message, actual.getMessage());
			return eq.isEquals();
		}

		private void append(EqualsBuilder eq, String expected, Object actual) {
			if (!regExp || expected == null) {
				eq.append(expected, actual);
			} else {
				String str = actual != null ? actual.toString() : "";
				eq.append(true, str.matches(expected));
			}
		}

		private static Throwable cause(LoggingEvent e) {
			ThrowableInformation ti = e.getThrowableInformation();
			return ti != null ? ti.getThrowable() : null;
		}
	}

	private static final Logger log = Logger.getRootLogger();
	private static final PatternLayout layout = new PatternLayout("%d{ABSOLUTE} %-5p [%c{1}] %m%n");
	private static final ConsoleAppender console;
	private static TestAppender oldAppender;

	private final LinkedList<LoggingEvent> infoList = new LinkedList<LoggingEvent>();
	private final LinkedList<Data> futureList = new LinkedList<Data>();
	private final List<LoggingEvent> allList = new ArrayList<LoggingEvent>();
	private final String testCase;
	private TestAppender appender;
	private Level level = Level.INFO;

	private final class TestAppender extends AppenderSkeleton {
		private final Level old;
		private final String testCase;

		public TestAppender(String testCase) {
			this.testCase = testCase;
			synchronized (TestAppender.class) {
				if (oldAppender != null) {
					Logger.getLogger(LoggerMock.class).warn(
							"Не был вызван LoggerMock.close(): " + oldAppender.testCase);
					oldAppender.close();
					oldAppender = null;
				}
				oldAppender = this;
			}
			setName(LoggerMock.class.getName());
			log.addAppender(this);
			old = log.getLevel();
			log.setLevel(Level.ALL);
		}

		@Override
		protected void append(LoggingEvent event) {
			if (event.getLevel().isGreaterOrEqual(level)) {
				Data expected = futureList.isEmpty() ? null : (Data) futureList.getFirst();
				if (expected != null && expected.equals(event)) {
					futureList.removeFirst();
				} else {
					infoList.add(event);
				}
			}
			allList.add(event);
		}

		@Override
		public void close() {
			if (closed) {
				return;
			}
			synchronized (TestAppender.class) {
				closed = true;
				if (oldAppender == this) {
					oldAppender = null;
				}
			}
			log.removeAppender(this);
			log.setLevel(old);
			if (console != null) {
				for (int i = 0; i < allList.size(); ++i) {
					console.doAppend(allList.get(i));
				}
			}
			erase();
		}

		@Override
		public boolean requiresLayout() {
			return false;
		}
	}

	static {
		java.util.logging.Logger jul = java.util.logging.Logger.getLogger("");
		jul.setLevel(java.util.logging.Level.ALL);
		SLF4JBridgeHandler.install();
		if (!"true".equals(System.getProperty("TestTraceEnabled"))) {
			console = null;
		} else {
			console = new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT);
			console.setName(LoggerMock.class.getName() + ".console");
			console.setThreshold(Level.ALL);
			Enumeration<?> e = log.getAllAppenders();
			while (e.hasMoreElements()) {
				AppenderSkeleton a = (AppenderSkeleton) e.nextElement();
				a.setThreshold(Level.OFF);
			}
			Handler[] handlers = jul.getHandlers();
			for (int i = 0; i < handlers.length; ++i) {
				handlers[i].setLevel(java.util.logging.Level.OFF);
			}
		}
	}

	/**
	 * Конструирует объект и добавляет себя в список обработчиков протокола
	 * <p>
	 * <b>Необходимо обязательно закрыть, во избежание утечек памяти!</b>
	 * </p>
	 */
	public LoggerMock() {
		testCase = "unknown";
		appender = new TestAppender(testCase);
	}

	/**
	 * Конструирует объект и добавляет себя в список обработчиков протокола
	 * <p>
	 * <b>Необходимо обязательно закрыть, во избежание утечек памяти!</b>
	 * </p>
	 */
	public LoggerMock(Class<?> testCase) {
		this.testCase = testCase.getName();
		appender = new TestAppender(this.testCase);
	}

	/**
	 * Устанавливает уровень критичности событий
	 * 
	 * @param level
	 *            уровень критичности событий
	 */
	public void setLevel(Level level) {
		this.level = level;
	}

	/**
	 * Переинициализирует объект
	 */
	public void init() {
		erase();
		close();
		appender = new TestAppender(testCase);
	}

	/**
	 * Удаляет себя из списка обработчиков протокола. Обязательно вызывайте этот
	 * метод, иначе возникнут утечки памяти.
	 */
	public void close() {
		if (appender != null) {
			appender.close();
		}
		appender = null;
	}

	/**
	 * Игнорируемое сообщение
	 */
	private static final String IGNORED_MESSAGE = "System time changed:";

	/**
	 * Проверяет, соответствие сообщений
	 */
	public void verify() {
		if (!futureList.isEmpty() || !infoList.isEmpty()) {
			PatternLayout pattern = new PatternLayout("[%-5p] %m%n"); //$NON-NLS-1$
			String actual = toString(pattern, infoList);
			String expected = toString(pattern, futureList);
			Assert.assertEquals(expected, actual);
			Assert.fail("expected: [\n" + expected + "\n], actual=[\n" + actual + "\n]");
		}
		erase();
	}

	/**
	 * Проверяет отладочное сообщение
	 * 
	 * @param message
	 *            отладочное сообщение
	 */
	public void debug(String message) {
		debug(message, null);
	}

	/**
	 * Проверяет отладочное сообщение по регулярному выражению
	 * 
	 * @param regExp
	 *            регулярное выражение
	 */
	public void debugRe(String regExp) {
		debugRe(regExp, null);
	}

	/**
	 * Проверяет отладочное сообщение
	 * 
	 * @param message
	 *            отладочное сообщение
	 * @param cause
	 *            ошибка
	 */
	public void debug(String message, Throwable cause) {
		log(Level.DEBUG, message, cause, false);
	}

	/**
	 * Проверяет отладочное сообщение по регулярному выражению
	 * 
	 * @param regExp
	 *            регулярное выражение
	 * @param cause
	 *            ошибка
	 */
	public void debugRe(String regExp, Throwable cause) {
		log(Level.DEBUG, regExp, cause, true);
	}

	/**
	 * Проверяет сообщение
	 * 
	 * @param message
	 *            сообщение
	 */
	public void info(String message) {
		info(message, null);
	}

	/**
	 * Проверяет сообщение по регулярному выражению
	 * 
	 * @param regExp
	 *            регулярное выражение
	 */
	public void infoRe(String regExp) {
		infoRe(regExp, null);
	}

	/**
	 * Проверяет сообщение
	 * 
	 * @param message
	 *            сообщение
	 * @param cause
	 *            ошибка
	 */
	public void info(String message, Throwable cause) {
		log(Level.INFO, message, cause, false);
	}

	/**
	 * Проверяет сообщение по регулярному выражению
	 * 
	 * @param regExp
	 *            регулярное выражение
	 * @param cause
	 *            ошибка
	 */
	public void infoRe(String regExp, Throwable cause) {
		log(Level.INFO, regExp, cause, true);
	}

	/**
	 * Проверяет сообщение
	 * 
	 * @param message
	 *            предупреждение
	 */
	public void warn(String message) {
		warn(message, null);
	}

	/**
	 * Проверяет предупреждение по регулярному выражению
	 * 
	 * @param regExp
	 *            регулярное выражение
	 */
	public void warnRe(String regExp) {
		warnRe(regExp, null);
	}

	/**
	 * Проверяет предупреждение
	 * 
	 * @param message
	 *            предупреждение
	 * @param cause
	 *            ошибка
	 */
	public void warn(String message, Throwable cause) {
		log(Level.WARN, message, cause, false);
	}

	/**
	 * Проверяет предупреждение по регулярному выражению
	 * 
	 * @param regExp
	 *            регулярное выражение
	 * @param cause
	 *            ошибка
	 */
	public void warnRe(String regExp, Throwable cause) {
		log(Level.WARN, regExp, cause, true);
	}

	/**
	 * Проверяет ошибку
	 * 
	 * @param message
	 *            ошибка
	 */
	public void error(String message) {
		error(message, null);
	}

	/**
	 * Проверяет ошибку по регулярному выражению
	 * 
	 * @param regExp
	 *            регулярное выражение
	 */
	public void errorRe(String regExp) {
		errorRe(regExp, null);
	}

	/**
	 * Проверяет сообщение об ошибке
	 * 
	 * @param message
	 *            соошбщение об ошибке
	 * @param cause
	 *            ошибка
	 */
	public void error(String message, Throwable cause) {
		log(Level.ERROR, message, cause, false);
	}

	/**
	 * Проверяет ошибку по регулярному выражению
	 * 
	 * @param regExp
	 *            регулярное выражение
	 * @param cause
	 *            ошибка
	 */
	public void errorRe(String regExp, Throwable cause) {
		log(Level.ERROR, regExp, cause, true);
	}

	/**
	 * Возвращает число записей в протоколе
	 * 
	 * @return число записей в протоколе
	 */
	public int size() {
		return infoList.size();
	}

	/**
	 * Очищает массив записей. Если массив не очистить, при закрытии распечатает
	 * ошибки на стандартный вывод
	 * 
	 * @return количество info-записей
	 */
	public int erase() {
		int n = infoList.size();
		infoList.clear();
		allList.clear();
		return n;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		WriterAppender appender = new WriterAppender(layout, writer);
		for (int i = 0; i < infoList.size(); ++i) {
			appender.append(infoList.get(i));
		}
		return writer.toString();
	}

	private void log(Level level, String message, Throwable cause, boolean regExp) {
		Data expected = new Data(level, message, cause, regExp);
		LoggingEvent actual = infoList.isEmpty() ? null : (LoggingEvent) infoList.getFirst();
		if (expected.equals(actual)) {
			infoList.removeFirst();
		} else {
			futureList.add(expected);
		}
	}

	private static String toString(PatternLayout pattern, List<?> list) {
		StrBuilder sb = new StrBuilder();
		Iterator<?> iter = list.iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			LoggingEvent e;
			if (obj instanceof LoggingEvent) {
				e = (LoggingEvent) obj;
			} else {
				Data d = (Data) obj;
				e = new LoggingEvent(null, log, d.level, d.message, d.cause);
			}
			if(!((String) e.getMessage()).contains(IGNORED_MESSAGE)){
				sb.append(pattern.format(e));
			}
			ThrowableInformation t = e.getThrowableInformation();
			if (t != null) {
				sb.append('\t').append(t.getThrowable()).append('\n');
			}
		}
		return sb.toString();
	}
}
