package ru.iteco.test.utils.rules;

import static javax.persistence.Persistence.createEntityManagerFactory;
import static ru.iteco.test.utils.TestUtil.findFields;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.IOUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Внедряет {@link EntityManager} в поля помеченные аннотацией
 * {@link PersistenceContext}.
 * 
 * <p>
 * Каждая аннотация {@link PersistenceContext} должна содержать свойство
 * unitName, в противном случае необходимо в конструктор {@link JpaRule}
 * передать unitName по умолчанию, иначе будет ошибка. Для каждого unitName
 * создает свой {@link EntityManager}. Правило обходит все поля отмеченные
 * аннотацией {@link PersistenceContext} подставляет {@link EntityManager} в
 * поле класса.
 * </p>
 * 
 * <p>
 * В случае, внедряет {@link EntityManager} так же в тестируемые объекты,
 * помеченные аннотацией {@link InjectMocks}.
 * </p>
 * 
 * @author Виктор Ержуков I-Teco 02 апр. 2014 г.
 * @author Роман Ержуков I-Teco 07 апр. 2014 г.
 */
public class JpaRule implements TestRule {
	private static final Logger log = LoggerFactory.getLogger(JpaRule.class);
	private static final Map<String, EntityManager> entityManager = new HashMap<String, EntityManager>();
	private static final Properties properties = new Properties();
	static {
		InputStream stream = null;
		try {
			stream = JpaRule.class.getResourceAsStream("/jpa.properties");
			properties.load(stream);
			sysProperty("hibernate.connection.url");
			sysProperty("hibernate.connection.username");
			sysProperty("hibernate.connection.password");
		} catch (IOException e) {
			throw new RuntimeException("Ошибка чтения файла настроек jpa.properties", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	private final Object targetTest;
	private boolean transaction;
	private final String defaultUnitName;
	private final List<Field> targetFields;
	private final List<Field> injectMockFields;

	/**
	 * Создание правила
	 * 
	 * @param targetTest
	 *            целевой тест, в который следует внедрить {@link EntityManager}
	 * @param transaction
	 *            начинает и завершает транзакцию
	 */
	public JpaRule(Object targetTest, boolean transaction) {
		this(targetTest, transaction, "");
	}

	/**
	 * Создание правила
	 * 
	 * @param targetTest
	 *            целевой тест, в который следует внедрить {@link EntityManager}
	 * @param transaction
	 *            начинает и завершает транзакцию
	 * @param defaultUnitName
	 *            имя модуля JPA по умолчанию
	 */
	public JpaRule(Object targetTest, boolean transaction, String defaultUnitName) {
		this.targetTest = targetTest;
		this.transaction = transaction;
		this.defaultUnitName = defaultUnitName;
		Class<?> testClass = targetTest.getClass();
		this.targetFields = findFields(testClass, PersistenceContext.class);
		this.injectMockFields = findFields(testClass, InjectMocks.class);
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		if (targetFields.isEmpty()) {
			return base;
		}
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					setEntityManagers();
					if (transaction) {
						for (EntityManager em : entityManager.values()) {
							em.getTransaction().begin();
						}
					}
					base.evaluate();
				} finally {
					if (transaction) {
						for (EntityManager em : entityManager.values()) {
							try {
								em.getTransaction().rollback();
							} catch (Throwable t) {
								log.warn("Unable rollback: {}", t.getMessage(), t);
							}
						}
					}
				}
			}
		};
	}

	private void setEntityManagers() {
		setEntityManagers(targetTest, targetFields, defaultUnitName);
		for (Field field : injectMockFields) {
			Object target = readField(targetTest, field);
			if (target != null) {
				List<Field> fieldList = findFields(target.getClass(), PersistenceContext.class);
				setEntityManagers(target, fieldList, defaultUnitName);
			}
		}
	}

	private static void setEntityManagers(Object target, List<Field> targetFields,
			String defaultUnitName) {
		for (Field field : targetFields) {
			PersistenceContext annotation = field.getAnnotation(PersistenceContext.class);
			String unitName = annotation.unitName();
			if (unitName.isEmpty()) {
				unitName = defaultUnitName;
			}
			checkUnitName(field, unitName);
			writeField(target, field, getEntityManager(unitName));
		}
	}

	private static void checkUnitName(Field field, final String unitName) {
		if (unitName.isEmpty()) {
			throw new RuntimeException("Класс [" + field.getDeclaringClass() + "], поле ["
					+ field.getName()
					+ "] с аннотацией @PersistenceContext должно содержать свойство unitName");
		}
	}

	private static EntityManager getEntityManager(String unitName) {
		EntityManager em = entityManager.get(unitName);
		if (em == null) {
			em = createEntityManagerFactory(unitName, properties).createEntityManager();
			entityManager.put(unitName, em);
		}
		return em;
	}

	private static Object readField(Object target, Field field) {
		try {
			return field.get(target);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeField(Object target, Field field, Object value) {
		try {
			field.set(target, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static void sysProperty(String name) {
		String value = System.getProperty(name);
		if (value != null) {
			properties.setProperty(name, value);
		}
	}
}
