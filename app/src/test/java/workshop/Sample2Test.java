package workshop;

import java.io.Writer;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.iteco.test.utils.LoggerMock;
import ru.iteco.test.utils.TestUtil;
import ru.iteco.test.utils.annotations.BeforeMock;
import ru.iteco.test.utils.annotations.LogMock;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static ru.iteco.test.utils.MockUtils.*;
import static ru.iteco.test.utils.TestUtil.uid;
import static ru.iteco.test.utils.TestUtil.uidS;

public class Sample2Test {
  @Rule
  public TestRule rule = AppRule.ruleChain(this);

  @InjectMocks
  private Sample2 subj;

  @Mock
  private Dao dao;
  @Mock
  private Predicate predicate;
  @Mock
  private Cursor<Account> cursor;
  @Mock
  private Formatter formatter;

  @LogMock
  private LoggerMock log;

  @BeforeMock
  public void beforeMock() {
    subj = new Sample2();
  }

  @Before
  public void setUp() {
    subj.dir = TestUtil.getTempFolder(getClass());
  }

  @Test
  public void testSaveToFile_cursor_is_empty() throws Exception {
    String name = uidS();
    when(dao.openByName(anyString())).thenReturn(cursor);
    when(cursor.hasNext()).thenReturn(false);

    assertThat(subj.saveToFile(name, predicate), is(false));

    verifyInOrder(dao).openByName(name);
    verifyInOrder(cursor).hasNext();
    verifyInOrder(cursor).close();
  }

  @Test
  public void testSaveToFile() throws Exception {
    String name = uidS();
    when(dao.openByName(anyString())).thenReturn(cursor);
    when(cursor.hasNext()).thenReturn(true, true, true, false);
    Account account1 = newAccount();
    Account account2 = newAccount();
    Account account3 = newAccount();
    when(cursor.next()).thenReturn(account1, account2, account3);
    when(predicate.apply(any(Account.class))).thenReturn(true, false, true);

    assertThat(subj.saveToFile(name, predicate), is(true));

    verifyInOrder(dao).openByName(name);
    verifyInOrder(cursor).hasNext();
    verifyInOrder(cursor).next();
    verifyInOrder(predicate).apply(account1);
    verifyInOrder(formatter).write(isA(Writer.class), eq(account1));
    verifyInOrder(cursor).hasNext();
    verifyInOrder(cursor).next();
    verifyInOrder(predicate).apply(account2);
    verifyInOrder(cursor).hasNext();
    verifyInOrder(cursor).next();
    verifyInOrder(predicate).apply(account3);
    verifyInOrder(formatter).write(isA(Writer.class), eq(account3));
    verifyInOrder(cursor).hasNext();
    verifyInOrder(cursor).close();
  }

  @Test
  public void testSaveToFile_Exceptions() throws Throwable {
    saveToFile_Exceptions(new RuntimeException(uidS()), new SQLException(uidS()));
    saveToFile_Exceptions(new RuntimeException(uidS()), new RuntimeException(uidS()));
    saveToFile_Exceptions(new RuntimeException(uidS()), new Error(uidS()));
    saveToFile_Exceptions(new Error(uidS()), new Error(uidS()));
  }

  private void saveToFile_Exceptions(Throwable thrown1, Throwable thrown2) throws Throwable {
    doThrow(thrown1).when(cursor).hasNext();
    doThrow(thrown2).when(cursor).close();

    String name = uidS();
    when(dao.openByName(anyString())).thenReturn(cursor);

    try {
      subj.saveToFile(name, predicate);
      fail();
    } catch (Throwable t) {
      if (t != thrown1)
        throw t;
      verifyInOrder(dao).openByName(name);
      verifyInOrder(cursor).hasNext();
      verifyInOrder(cursor).close();

      log.warn("Невозможно закрыть курсор", thrown2);
    }
  }

  private Account newAccount() {
    Account a = new Account();
    a.setId(uid());
    return a;
  }
}