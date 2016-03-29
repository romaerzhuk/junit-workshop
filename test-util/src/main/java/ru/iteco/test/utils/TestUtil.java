package ru.iteco.test.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.shuffle;
import static org.apache.commons.lang3.time.DateUtils.MILLIS_PER_DAY;
import static org.apache.commons.lang3.time.DateUtils.MILLIS_PER_HOUR;
import static ru.iteco.test.utils.rules.TempDirRule.BASE_ROOT;
import static ru.iteco.test.utils.rules.TempDirRule.SUBFODLER;

/**
 * Утилиты для тестирования
 *
 * @author Роман Ержуков I-Teco 15.03.2010
 */
public class TestUtil {
	private static abstract class ClassMember<T> {
		abstract void setAccessible(T value);

		abstract T[] getValues(Class<?> klass);
	}

	private static final ClassMember<Field> fields = new ClassMember<Field>() {
		@Override
		void setAccessible(Field value) {
			value.setAccessible(true);
		}

		@Override
		Field[] getValues(Class<?> klass) {
			return klass.getDeclaredFields();
		}
	};
	private static final ClassMember<Method> methods = new ClassMember<Method>() {
		@Override
		void setAccessible(Method value) {
			value.setAccessible(true);
		}

		@Override
		Method[] getValues(Class<?> klass) {
			return klass.getDeclaredMethods();
		}
	};
	private static final Logger log = LoggerFactory.getLogger(TestUtil.class);
	/**
	 * Корневая директория для временных файлов теста
	 */
	static final String TMP_DIR = "target/test-tmp/";
	private static int uid;

	/**
	 * Преобразует число BigDecimal; избегает создания бесконечных десятичных
	 * дробей
	 *
	 * @param num
	 *            число
	 * @return BigDecimal
	 */
	public static BigDecimal dec(double num) {
		return dec(Double.toString(num));
	}

	/**
	 * Преобразует в число BigDecimal
	 *
	 * @param num
	 *            число
	 * @return BigDecimal
	 */
	public static BigDecimal dec(String num) {
		return new BigDecimal(num);
	}

	/**
	 * Открывает двоичный поток на чтение
	 *
	 * @param klass
	 *            класс
	 * @param name
	 *            имя файла, относительно класса
	 * @return inputStream
	 */
	public static InputStream newInputStream(Class<?> klass, String name) {
		name = fileName(klass, name, true);
		InputStream is = klass.getResourceAsStream(name);
		if (is == null) {
			throw new IllegalArgumentException(name);
		}
		return is;
	}

	/**
	 * Открывает двоичный поток на чтение
	 *
	 * @param klass
	 *            класс
	 * @param name
	 *            имя файла, относительно класса
	 * @param prefix
	 *            признак добавления префикса класса теста к имени файла
	 * @return inputStream
	 */
	public static InputStream newInputStream(Class<?> klass, String name, boolean prefix) {
		name = fileName(klass, name, prefix);
		InputStream is = klass.getResourceAsStream(name);
		if (is == null) {
			throw new IllegalArgumentException(name);
		}
		return is;
	}

	/**
	 * Открывает текстовый поток на чтение
	 *
	 * @param klass
	 *            класс
	 * @param name
	 *            имя файла, относительно класса
	 * @return inputStream
	 * @throws IOException
	 */
	public static BufferedReader newReader(Class<?> klass, String name, String encoding)
			throws IOException {
		return new BufferedReader(new InputStreamReader(newInputStream(klass, name), encoding));
	}

	/**
	 * Читает содержимое файла относительно класса
	 *
	 * @param klass
	 *            класс
	 * @param name
	 *            имя файла
	 * @param encoding
	 *            кодировка
	 * @throws IOException
	 */
	public static String readFileToString(Class<?> klass, String name, String encoding)
			throws IOException {
		return readFileToString(klass, name, encoding, true);
	}

	/**
	 * Читает содержимое файла относительно класса
	 *
	 * @param klass
	 *            класс
	 * @param name
	 *            имя файла
	 * @param encoding
	 *            кодировка
	 * @param prefix
	 *            признак добавления префикса класса теста к имени файла
	 * @throws IOException
	 */
	public static String readFileToString(Class<?> klass, String name, String encoding,
			boolean prefix) throws IOException {
		return new String(readFileToByteArray(klass, name, prefix), encoding);
	}

	/**
	 * Читает содержимое файла относительно класса
	 *
	 * @param klass
	 *            класс
	 * @param name
	 *            имя файла
	 * @throws IOException
	 */
	public static byte[] readFileToByteArray(Class<?> klass, String name) throws IOException {
		return readFileToByteArray(klass, name, true);
	}

	/**
	 * Читает содержимое файла относительно класса
	 *
	 * @param klass
	 *            класс
	 * @param name
	 *            имя файла
	 * @param prefix
	 *            признак добавления префикса класса теста к имени файла
	 * @throws IOException
	 */
	public static byte[] readFileToByteArray(Class<?> klass, String name, boolean prefix)
			throws IOException {
		InputStream is = null;
		try {
			is = newInputStream(klass, name, prefix);
			return IOUtils.toByteArray(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Преобразует текст в дату
	 *
	 * @param date
	 *            дата, время, или дата/время
	 * @return date
	 * @throws IllegalArgumentException
	 */
	public static Date toDate(String date) {
		return toDate(date, TimeZone.getDefault());
	}

	/**
	 * Преобразует текст в календарь
	 *
	 * @param date
	 *            дата, время, или дата/время
	 * @return календарь
	 */
	public static Calendar toCalendar(String date) {
		return toCalendar(toDate(date));
	}

	/**
	 * Преобразует дату в календарь
	 *
	 * @param date
	 *            дата/время
	 * @return календарь
	 */
	public static Calendar toCalendar(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	/**
	 * Преобразует текст в дату
	 *
	 * @param date
	 *            дата, время, или дата/время
	 * @param timezone
	 *            временная зона
	 * @return дата/время
	 */
	public static Date toDate(String date, TimeZone timezone) {
		return parseDate(date, timezone, "dd.MM.yyyy HH:mm:ss", "dd.MM.yyyy",
				"dd.MM.yyyy HH:mm:ss.SSS", "yyyy-MM-dd", "HH:mm:ss", "HH:mm:ss.SSS");
	}

	/**
	 * Преобразует текст в календарь
	 *
	 * @param date
	 *            дата, время, или дата/время
	 * @param timezone
	 *            временная зона
	 * @return календарь
	 */
	public static Calendar toCalendar(String date, TimeZone timezone) {
		Calendar c = Calendar.getInstance(timezone);
		c.setTime(toDate(date, timezone));
		return c;
	}

	/**
	 * Преобразует текст в дату
	 *
	 * @param str
	 *            строка даты/времени
	 * @param parsePattern
	 *            формат даты/времени
	 * @return дата/время
	 * @throws ParseException
	 *             если невозможно преобразовать строку
	 */
	public static Date parse(String str, String parsePattern) {
		return parseDate(str, parsePattern);
	}

	/**
	 * Проверяет приблизительное совпадение текущего времени
	 *
	 * @param time
	 *            текущее время
	 */
	public static void assertNow(Date time) {
		Assert.assertNotNull(time);
		long now = System.currentTimeMillis();
		long tim = time.getTime();
		Assert.assertTrue(
				"delta=" + (now - tim) / 1000.0 + " sec; now=" + format(new Date(now), "HH:mm:ss")
						+ ", time=" + format(new Date(tim), "HH:mm:ss"), now + 500 >= tim
						&& now <= tim + 5000);
	}

	/**
	 * Закрывает ресурс, или коллекцию ресурсов
	 *
	 * @param obj
	 *            ресурс, или коллекцию ресурсов
	 */
	public static void close(Object obj) {
		if (obj == null) {
			return;
		}
		if (obj instanceof Collection) {
			Iterator<?> itr = ((Collection<?>) obj).iterator();
			while (itr.hasNext()) {
				close(itr.next());
			}
		} else if (obj instanceof Closeable) {
			closeQuietly((Closeable) obj);
		} else {
			throw new IllegalArgumentException("Неизвестный класс: " + obj.getClass());
		}
	}

	/**
	 * Возвращает файл из classpath-а
	 *
	 * @param klass
	 *            класс
	 * @param name
	 *            имя файла
	 * @return file
	 */
	public static File getFile(Class<?> klass, String name) {
		try {
			name = fileName(klass, name, true);
			URL resource = klass.getResource(name);
			String path = klass.getPackage().getName().replace('.', '/') + '/' + name;
			if (resource == null) {
				throw new IllegalArgumentException("Файл не найден: classpath:" + path);
			}
			return new File(URLDecoder.decode(resource.getFile(), CharEncoding.UTF_8));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Возвращает имя новой тестовой директории. На диске не создаёт
	 *
	 * @param klass
	 *            тестовый класс
	 * @return tmpDir
	 */
	public static File newTmpDir(Class<?> klass) {
		return new File(TMP_DIR + klass.getSimpleName());
	}

	/**
	 * Форматирует Date в String
	 *
	 * @param date
	 *            дата/время
	 * @param format
	 *            формат даты/времени
	 * @return форматированная дата
	 */
	public static String format(Date date, String format) {
		return FastDateFormat.getInstance(format).format(toCalendar(date));
	}

	/**
	 * Удаляет временную директорию
	 */
	public static void deleteTmpDir(Class<?> klass) {
		File tmpDir = newTmpDir(klass);
		FileUtils.deleteQuietly(tmpDir); // target/test/ClassName
	}

	/**
	 * Преобразует текст в дату с использованием нескольких форматов
	 *
	 * @param str
	 *            строка даты/времени
	 * @param parsePatterns
	 *            форматы даты/времени
	 * @return дата/время
	 * @throws IllegalArgumentException
	 *             если невозможно преобразовать строку
	 */
	private static Date parseDate(String str, String... parsePatterns) {
		return parseDate(str, TimeZone.getDefault(), parsePatterns);
	}

	/**
	 * Преобразует текст в дату с использованием нескольких форматов
	 *
	 * @param str
	 *            строка даты/времени
	 * @param timezone
	 *            временная зона
	 * @param parsePatterns
	 *            форматы даты/времени
	 * @return дата/время
	 * @throws IllegalArgumentException
	 *             если невозможно преобразовать строку
	 * @see {@link DateUtils#parseDate(String, String[])}
	 */
	private static Date parseDate(String str, TimeZone timezone, String... parsePatterns) {
		if (str == null || parsePatterns == null) {
			throw new IllegalArgumentException("Date and Patterns must not be null");
		}
		SimpleDateFormat parser = null;
		ParsePosition pos = new ParsePosition(0);
		for (int i = 0; i < parsePatterns.length; i++) {
			if (i == 0) {
				parser = new SimpleDateFormat(parsePatterns[0]);
				parser.getCalendar().setLenient(true);
				parser.setTimeZone(timezone);
			} else {
				parser.applyPattern(parsePatterns[i]);
			}
			pos.setIndex(0);
			Date date = parser.parse(str, pos);
			if (date != null && pos.getIndex() == str.length()) {
				return date;
			}
		}
		throw new IllegalArgumentException("Unable to parse the date: " + str);
	}

	/**
	 * Возвращает короткий уникальный идентификатор в рамках запуска тестов
	 *
	 * @return {@link #uid}
	 */
	public static int uid() {
		return ++uid;
	}

	/**
	 * Возвращает короткий уникальный идентификатор в рамках запуска тестов,
	 * [0..n-1]
	 *
	 * @return {@link #uid}
	 */
	public static int uid(int n) {
		return uid() % n;
	}

	/**
	 * Возвращает короткий уникальный отрицательный идентификатор в рамках
	 * запуска тестов
	 *
	 * @return {@link #uid}
	 */
	public static int negUid() {
		return ++uid * (-1);
	}

	/**
	 * Возвращает случайное значение перечисления
	 *
	 * @param klass
	 *            класс перечисления
	 * @param excludes
	 *            исключаемые значения
	 * @return любое значение перечисления
	 */
	public static <T extends Enum<?>> T uid(Class<T> klass, T... excludes) {
		T[] values = klass.getEnumConstants();
		List<T> excludeList = Arrays.asList(excludes);
		for (int i = 0; i < values.length; i++) {
			T result = values[uid(values.length)];
			if (!excludeList.contains(result)) {
				return result;
			}
		}
		throw new IllegalArgumentException(excludeList.toString());
	}

	/**
	 * Возвращает короткий уникальный идентификатор в рамках запуска тестов
	 *
	 * @return {@link #uid}
	 */
	public static long uidL() {
		return ++uid;
	}

	/**
	 * Возвращает короткий уникальный идентификатор в рамках запуска тестов,
	 * [0..n-1]
	 *
	 * @return {@link #uid}
	 */
	public static long uidL(int n) {
		return uid(n);
	}

	/**
	 * Возвращает короткий уникальный идентификатор в рамках запуска тестов
	 *
	 * @return {@link #uid}
	 */
	public static double uidD() {
		return ++uid;
	}

	/**
	 * Возвращает короткий уникальный идентификатор в рамках запуска тестов,
	 * [0..n-1]
	 *
	 * @return {@link #uid}
	 */
	public static double uidD(int n) {
		return uid(n);
	}

	/**
	 * Возвращает короткий уникальный идентификатор в рамках запуска тестов в
	 * виде строки
	 *
	 * @return {@link #uid} в виде строки
	 */
	public static String uidS() {
		return String.valueOf(uid());
	}

	/**
	 * Возвращает короткий уникальный идентификатор в рамках запуска тестов, в
	 * виде строки [0..n-1]. Дополняет слева нулями
	 *
	 * @return {@link #uid}
	 */
	public static String uidS(int n) {
		return StringUtils.leftPad(String.valueOf(uid(n)), (int) Math.log10(n), '0');
	}

	/**
	 * Возвращает идентификатор в виде булевого значения.
	 *
	 * @return идентификатор в виде булевого значения
	 */
	public static boolean uidBool() {
		return uid() % 2 == 0;
	}

	/**
	 * Возвращает уникальную дату/время
	 *
	 * @return date
	 */
	public static Date newDate() {
		return newDate(MILLIS_PER_HOUR);
	}

	/**
	 * Возвращает уникальную дату
	 *
	 * @return date
	 */
	public static java.sql.Date newSqlDate() {
		return new java.sql.Date(DateUtils.truncate(newDate(MILLIS_PER_DAY), Calendar.DATE).getTime());
	}

	private static Date newDate(long offset) {
		return new Date(currentTimeMillis() - uid() * offset);
	}

	/**
	 * Возвращает уникальную дату/время, округлённую до секунд
	 *
	 * @return timestamp
	 */
	public static Timestamp newTimestamp() {
		return new Timestamp(currentTimeMillis() / 1000 * 1000 - uid() * MILLIS_PER_HOUR);
	}

  /**
   * Возвращает уникальную дату/время, округлённую до секунд в виде long
   *
   * @return timestamp
   */
  public static long newTime() {
    return currentTimeMillis() / 1000 * 1000 - uid() * MILLIS_PER_HOUR;
  }

	private static void closeQuietly(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Throwable t) {
				log.warn("Невозможно закрыть [{}]: {}", c.getClass().getSimpleName(), t.toString());
			}
		}
	}

	/**
	 * Возвращает поля в тестовом классе, помеченное аннотацией
	 *
	 * @param testClass
	 *            класс теста
	 * @param annotationClass
	 *            класс аннотации
	 * @param expectedClass
	 *            ожидаемый класс поля
	 * @return list
	 */
	public static <T extends Annotation> List<Field> findFields(Class<?> testClass,
			final Class<T> annotationClass, final Class<?> expectedClass) {
		return findFields(testClass, new Predicate<Field>() {
			@Override
			public boolean apply(Field field) {
				boolean result = field.getAnnotation(annotationClass) != null;
				if (result && field.getType() != expectedClass) {
					throw new IllegalArgumentException(
							"Поле " //$NON-NLS-1$
									+ field.getName()
									+ " с аннотацией @" + annotationClass.getSimpleName() + " должно быть класса " //$NON-NLS-1$
									+ expectedClass.getSimpleName());
				}
				return result;
			}
		});
	}

	/**
	 * Возвращает поля в тестовом классе, помеченное аннотацией
	 *
	 * @param testClass
	 *            класс теста
	 * @param annotationClass
	 *            класс аннотации
	 * @return list
	 */
	public static <T extends Annotation> List<Field> findFields(Class<?> testClass,
			final Class<T> annotationClass) {
		return findFields(testClass, new Predicate<Field>() {
			@Override
			public boolean apply(Field field) {
				return field.getAnnotation(annotationClass) != null;
			}
		});
	}

	/**
	 * Возвращает поля в тестовом классе по заданому условию
	 *
	 * @param testClass
	 *            класс теста
	 * @param predicate
	 *            условие поиска
	 * @return list
	 */
	public static <T extends Annotation> List<Field> findFields(Class<?> testClass,
			Predicate<Field> predicate) {
		return findFor(testClass, predicate, fields);
	}

	/**
	 * Возвращает void-методы в тестовом классе без параметров, помеченные
	 * аннотацией
	 *
	 * @param testClass
	 *            класс теста
	 * @param annotationClass
	 *            класс аннотации
	 * @return list
	 */
	public static <T extends Annotation> List<Method> findMethods(Class<?> testClass,
			final Class<T> annotationClass) {
		return findMethods(testClass, new Predicate<Method>() {
            @Override
            public boolean apply(Method method) {
                if (method.getAnnotation(annotationClass) == null) {
                    return false;
                }
                if (method.getReturnType() != Void.TYPE) {
                    throw new IllegalStateException(method + " не должен возвращать значений");
                }
                if (method.getParameterTypes().length > 0) {
                    throw new IllegalStateException(method + " не должен принимать параметров");
                }
                method.setAccessible(true);
                return true;
            }
        });
	}

	/**
	 * Возвращает void-методы в тестовом классе без параметров, помеченные
	 * аннотацией
	 *
	 * @param testClass
	 *            класс теста
	 * @param predicate
	 *            условие поиска
	 * @return list
	 */
	public static List<Method> findMethods(Class<?> testClass, Predicate<Method> predicate) {
		return findFor(testClass, predicate, methods);
	}

	/**
	 * Возвращает системное свойство, или, если отсутствует, из props
	 *
	 * @param props
	 *            свойства
	 * @param key
	 *            имя свойства
	 * @return значение свойства
	 */
	public static String getProperty(Properties props, String key) {
		String value = System.getProperty(key);
		if (value == null) {
			value = props.getProperty(key);
		}
		return value;
	}

	public static <T> List<T> findFor(Class<?> testClass, Predicate<T> predicate,
			ClassMember<T> member) {
		List<T> list = new ArrayList<T>();
		Class<? extends Object> klass = testClass;
		while (klass != Object.class) {
			for (T value : member.getValues(klass)) {
				if (predicate.apply(value)) {
					member.setAccessible(value);
					list.add(value);
				}
			}
			klass = klass.getSuperclass();
		}
		return list;
	}

	/**
	 * Возвращает несортированный итератор по уникальным числам в диапазоне
	 * 0..n-1.
	 *
	 * @return итератор
	 */
	public static Iterator<Integer> randomIterator(int n) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) {
			list.add(i);
		}
		shuffle(list);
		return list.iterator();
	}

	/**
	 * Возвращает список пустых строк.
	 *
	 * @return список
	 */
	public static List<String> blankStrings() {
		List<String> list = new ArrayList<String>();
		list.add(null);
		list.add("");
		list.add("    ");
		list.add("\t");
		list.add("\n");
		list.add("\t \n  ");
		return list;
	}

	/**
	 * Создает массив одинаковых строк
	 *
	 * @param message
	 *            строка
	 * @param times
	 *            кол-во повторов
	 * @return массив одинаковых строк
	 */
	public static String[] messageTimes(String message, int times) {
		String[] arr = new String[times];
		for (int i = 0; i < times; i++) {
			arr[i] = message;
		}
		return arr;
	}

	private static String fileName(Class<?> klass, String name, boolean prefix) {
		return prefix ? klass.getSimpleName() + '-' + name : name;
	}

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }


  /**
   * Returns temporary root folder for test class
   * used in ru.iteco.test.utils.rules.TempDirRule#create()
   *
   * @return {@link File}
   */
  public static File getTempFolder(Class clazz) {
    return new File(new StringBuilder().
        append(new File(clazz.getClassLoader().getResource(BASE_ROOT).getFile()).getParent()).
        append(separator).
        append(SUBFODLER).
        append(separator).
        append(clazz.getName()).toString());
  }

}
