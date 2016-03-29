package ru.iteco.test.utils.rules;

import static ru.iteco.test.utils.TestUtil.findFields;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import ru.iteco.test.utils.LoggerMock;
import ru.iteco.test.utils.annotations.LogMock;

/**
 * Создаёт перед вызовом теста {@link LoggerMock}, внедряет, при наличии, в поле
 * теста с аннотацией @{@link LogMock}. По завершению теста проверяет отсутствие
 * непроверенных сообщений в логе, уровня INFO или выше.
 * 
 * @author Роман Ержуков I-Teco 12.09.2011
 */
public class LoggerRule implements TestRule {
	private static class Data {
		LoggerMock log;
		List<Field> fields;
		Level level;

		public Data(LoggerMock log, List<Field> fields) {
			this.log = log;
			this.fields = fields;
			this.level = findLevel(fields);
		}

		private Level findLevel(List<Field> fields) {
			Level level = null;
			Field field = null;
			for (Field f : fields) {
				LogMock annotation = f.getAnnotation(LogMock.class);
				Level l = Level.toLevel(annotation.level());
				if (field != null && !l.equals(level)) {
					throw new IllegalArgumentException(
							"Уровни логгирования не должны различаться для полей: "
									+ field.getName() + "=" + level + " и " + f.getName() + "=" + l);
				}
				level = l;
				field = f;
			}
			return level;
		}
	}

	private static final ThreadLocal<Data> data = new ThreadLocal<Data>();

	private final Object targetTest;
	private final boolean external;

	/**
	 * Создаёт перед вызовом теста {@link LoggerMock}, внедряет, при наличии, в
	 * поле теста с аннотацией @{@link LogMock}. По завершению теста проверяет
	 * отсутствие непроверенных сообщений в логе, уровня INFO или выше.
	 * 
	 * @param targetTest
	 *            целевой тест
	 * @param external
	 *            признак внешнего правила
	 */
	public LoggerRule(Object targetTest, boolean external) {
		this.targetTest = targetTest;
		this.external = external;
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				LoggerMock log = null;
				try {
					if (external) {
						log = new LoggerMock();
						data.set(new Data(log, findFields(targetTest.getClass(), LogMock.class,
								LoggerMock.class)));
					} else {
						Data d = data.get();
						log = d.log;
						log.erase();
						Level level = d.level;
						if (level != null) {
							log.setLevel(level);
						}
						for (Field field : d.fields) {
							field.set(targetTest, log);
						}
					}
					base.evaluate();
					if (!external) {
						log.verify();
					}
				} finally {
					if (external) {
						log.erase();
						log.close();
						data.remove();
					}
				}
			}
		};
	}
}
