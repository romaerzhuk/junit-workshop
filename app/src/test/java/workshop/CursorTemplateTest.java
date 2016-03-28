package workshop;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.uid;
import static ru.iteco.test.utils.TestUtil.uidS;

import java.sql.SQLException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import ru.iteco.test.utils.LoggerMock;
import ru.iteco.test.utils.annotations.LogMock;

public class CursorTemplateTest {
  @Rule
  public TestRule rule = AppRule.ruleChain(this);

  private CursorTemplate subj;

  @Mock
  private CursorCreator<Object> creator;
  @Mock
  private CursorCallback<Object,Object> callback;
  @Mock
  private Cursor<Object> cursor;

  @LogMock
  private LoggerMock log;

  @Before
  public void setUp() throws Exception {
    subj = new CursorTemplate();
    when(creator.open()).thenReturn(cursor);
  }

  @Test
  public void testExecute() throws Exception {
    Object result = new Object();
    when(callback.doWithCursor(cursor)).thenReturn(result);

    assertThat(subj.execute(creator, callback), is(result));

    verifyInOrder(creator).open();
    verifyInOrder(callback).doWithCursor(cursor);
    verifyInOrder(cursor).close();
  }

  @Test
  public void testExecute_Exceptions() throws Throwable {
    execute_Exceptions(new RuntimeException(uidS()), new RuntimeException(uidS()));
    execute_Exceptions(new RuntimeException(uidS()), new SQLException(uidS()));
    execute_Exceptions(new SQLException(uidS()), new RuntimeException(uidS()));
    execute_Exceptions(new SQLException(uidS()), new SQLException(uidS()));
    execute_Exceptions(new RuntimeException(uidS()), new Error(uidS()));
    execute_Exceptions(new Error(uidS()), new Error(uidS()));
  }

  private void execute_Exceptions(Throwable thrown1, Throwable thrown2) throws Throwable {
    doThrow(thrown1).when(callback).doWithCursor(cursor);
    doThrow(thrown2).when(cursor).close();

    try {
      subj.execute(creator, callback);
      fail();
    } catch (Throwable t) {
      if (t != thrown1)
        throw t;
      verifyInOrder(creator).open();
      verifyInOrder(callback).doWithCursor(cursor);
      verifyInOrder(cursor).close();

      log.warn("Невозможно закрыть курсор", thrown2);
    }
  }
}