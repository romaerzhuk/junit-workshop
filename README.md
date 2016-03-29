h2. Unit

# Unit-тесты:
#* быстрый запуск и устранение ошибок;
#* уверенность: проверка всех деталей алгоритмов;
#* рефакторинг: удаление избыточного и грязного кода.
# Фреймворки
## Базовые фреймворки Unit-тестирования:
##* JUnit;
##* TestNG.
## Hamcrest
## Mock-тестирование:
##* Mockito;
##* PowerMock;
##* Easymock;
##* JMockit.
## Собственный набор утилит: test-util
##* TestUtil:
##** uid(), newDate(), уникальные значения для тестов;
##** toDate(Date), format(Date, format), parse(str, parsePatterns), toCalendar(Date), преобразование даты/времени;
##** dec(String), dec(double), преобразования в BigDecimal.
##** readFileToString(testClass, suffix, encoding), getFile;
##* Rules:
##** MockitoRule, verifyInOrder
##** BeforeMockRule, инициализация до Mockito;
##** LoggerRule, тестирование логов;
##** ParameterizedRule, параметризация тестов;
##** TempDirRule, временные файлы теста;
##** JpaRule, тестирование JPA 2.0.
# Примеры:
#* test-00, пустой проект
#* test-01, SimpleDateFormat.parse, JUnit, Assert.assertEquals, TestUtil.toDate;
#* test-02, assertThat, Mockito, verifyInOrder, Captor, self, рефакторинг, PowerMock;
#* test-04, наследование, TestUtil.readFileToString;
#* test-03, полное тестирование legacy-кода, answer, рефакторинг, безопасное закрытие ресурсов.
