package ru.iteco.test.utils.rules;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import ru.iteco.test.utils.MockUtils;
import ru.iteco.test.utils.Predicate;
import ru.iteco.test.utils.TestUtil;
import ru.iteco.test.utils.annotations.NoInOrder;
import ru.iteco.test.utils.annotations.NoVerifyZeroInteractions;

/**
 * Внедряет моки в тестовые поля, помеченные аннотациями {@link Mock}, аналогично {@link MockitoJUnitRunner}.
 * Требуется использовать вызовы {@link MockUtils#verifyInOrder(Object)},
 * {@link MockUtils#verifyInOrder(Object, VerificationMode)},
 * {@link MockUtils#verifyInOrderNoMoreInteractions()}, которые позволяют протестировать порядок вызовов всех
 * моков тестов.
 *
 * <ul>
 * <li>
 * Initializes mocks annotated with {@link Mock}, so that explicit usage of
 * {@link MockitoAnnotations#initMocks(Object)} is not necessary. Mocks are initialized before each test
 * method.
 * <li>
 * validates framework usage after each test method. See javadoc for {@link Mockito#validateMockitoUsage()}.
 * </ul>
 *
 * Runner is completely optional - there are other ways you can get &#064;Mock working, for example by writing
 * a base class. Explicitly validating framework usage is also optional because it is triggered automatically
 * by Mockito every time you use the framework. See javadoc for {@link Mockito#validateMockitoUsage()}.
 * <p>
 * Read more about &#064;Mock annotation in javadoc for {@link MockitoAnnotations}
 *
 * <pre>
 * <b>&#064;RunWith(PaymentClassRunner.class)</b>
 * public class ExampleTest {
 * 
 *     &#064;Mock
 *     private List list;
 * 
 *     &#064;Test
 *     public void shouldDoSomething() {
 *         list.add(100);
 *     }
 * }
 * </pre>
 *
 * </p>
 *
 * @author Роман Ержуков I-Teco 14.09.2011
 */
public class MockitoRule implements TestRule {
	private static class Data implements InOrder {
		private final List<Field> mocks;
		private final Map<Object, Object> zeroInvocations = new IdentityHashMap<Object, Object>();
		private final List<Object> mockList = new ArrayList<Object>();

		private boolean verifyZeroInteractions;
		private InOrder inOrder;

		public Data(List<Field> mocks) {
			this.mocks = mocks;
		}

		public void before(Object test, Description description) {
			zeroInvocations.clear();
			mockList.clear();
			verifyZeroInteractions = !isAnnotationPresent(description,
					NoVerifyZeroInteractions.class);
			for (Field mock : mocks) {
				try {
					addMock(mock.get(test));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			if (mockList.isEmpty()) {
				return;
			}
			if (isAnnotationPresent(description, NoInOrder.class)) {
				inOrder = null;
				MockUtils.setInOrder(null);
			} else {
				inOrder = Mockito.inOrder(mockList.toArray());
				MockUtils.setInOrder(this);
			}
		}

		@Override
		public <T> T verify(T mock) {
			zeroInvocations.remove(mock);
			return inOrder.verify(mock);
		}

		@Override
		public <T> T verify(T mock, VerificationMode mode) {
			zeroInvocations.remove(mock);
			return inOrder.verify(mock, mode);
		}

		@Override
		public void verifyNoMoreInteractions() {
			if (inOrder != null) {
				inOrder.verifyNoMoreInteractions();
			} else if (!mockList.isEmpty()) {
				Mockito.verifyNoMoreInteractions(mockList.toArray());
			}
			if (!zeroInvocations.isEmpty()) {
				Mockito.verifyZeroInteractions(zeroInvocations.keySet().toArray());
			}
		}

		private void addMock(Object mock) {
			if (verifyZeroInteractions) {
				zeroInvocations.put(mock, null);
			}
			mockList.add(mock);
		}

		private boolean isAnnotationPresent(Description description,
				Class<? extends Annotation> klass) {
			return description.getAnnotation(klass) != null;
		}
	}

	private final Object targetTest;

	private Data data;

  /**
   * Внедряет моки в тестовые поля, помеченные аннотациями {@link Mock}, аналогично {@link MockitoJUnitRunner}
   *
   * @param targetTest
   *          тест
   */
  public MockitoRule(Object targetTest) {
    this.targetTest = targetTest;
    List<Field> mocks = TestUtil.findFields(targetTest.getClass(), new Predicate<Field>() {
      @Override
      public boolean apply(Field field) {
        return field.isAnnotationPresent(Mock.class) && !field.isAnnotationPresent(NoInOrder.class);
      }
    });
    if (!mocks.isEmpty()) {
      data = new Data(mocks);
    }
  }

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				MockitoAnnotations.initMocks(targetTest);
				try {
					if (data != null) {
						data.before(targetTest, description);
					}
					base.evaluate();
					try {
						if (data != null) {
							data.verifyNoMoreInteractions();
						}
						Mockito.validateMockitoUsage();
					} catch (Error e) {
						MockitoException ex = new MockitoException(e.getMessage());
						ex.setStackTrace(e.getStackTrace());
						throw ex;
					}
				} finally {
					MockUtils.setInOrder(null);
				}
			}
		};
	}
}
