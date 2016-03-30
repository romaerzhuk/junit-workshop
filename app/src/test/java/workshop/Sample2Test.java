package workshop;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.uid;
import static ru.iteco.test.utils.TestUtil.uidBool;
import static ru.iteco.test.utils.TestUtil.uidS;

import java.io.File;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import ru.iteco.test.utils.LoggerMock;
import ru.iteco.test.utils.TestUtil;
import ru.iteco.test.utils.annotations.BeforeMock;
import ru.iteco.test.utils.annotations.LogMock;
import ru.iteco.test.utils.annotations.NoInOrder;

public class Sample2Test {
  @Rule
  public TestRule rule = AppRule.ruleChain(this);

  @InjectMocks
  private Sample2 subj;

  @Mock
  private Sample2 self;
  @Mock
  private Dao dao;
  @Mock
  private Predicate predicate;
  @Mock
  private CursorCreator<Account> cursorCreator;
  @Mock
  private Formatter formatter;
  @Mock
  private CursorTemplate cursorTemplate;
  @Mock
  private FileTemplate fileTemplate;
  @Mock
  private Writer writer;

  @Mock
  @NoInOrder
  private LazyWriter lazyWriter;

  @Captor
  private ArgumentCaptor<CursorCallback<Account, Boolean>> cursorCallback;
  @Captor
  private ArgumentCaptor<WriterCallback<Object>> writerCallback;

  @LogMock
  private LoggerMock log;

  @BeforeMock
  public void beforeMock() {
    subj = new Sample2() {
      @Override Sample2 self() {
        return self;
      }
    };
  }

  @Before
  public void setUp() throws  Exception{
    subj.dir = TestUtil.getTempFolder(getClass());
    when(lazyWriter.get()).thenReturn(writer);
  }

  @Test
  public void testSaveToFile() throws Exception {
    String name = uidS();
    when(dao.cursorCreatorByName(name)).thenReturn(cursorCreator);
    boolean result = uidBool();
    when(cursorTemplate.execute(eq(cursorCreator), anyCursorCallback())).thenReturn(result);

    assertThat(subj.saveToFile(name, predicate), is(result));

    verifyInOrder(dao).cursorCreatorByName(name);
    verifyInOrder(cursorTemplate).execute(eq(cursorCreator), cursorCallback.capture());

    // шаг2: doWithCursor
    Iterator<Account> cursor = asList(newAccount()).iterator(); // любой список
    result = uidBool();
    when(self.saveToFile(cursor, name, predicate)).thenReturn(result);

    assertThat(cursorCallback.getValue().doWithCursor(cursor), is(result));

    verifyInOrder(self).saveToFile(cursor, name, predicate);
  }

  @Test
  public void testSaveToFile_cursor_is_empty() throws Exception {
    // шаг1: fileTemplate.execute
    String name = uidS();
    Iterator<Account> cursor = Collections.<Account>emptyList().iterator();
    boolean result = uidBool();
    when(fileTemplate.execute(any(File.class), anyWriterCallback())).thenReturn(result);

    assertThat(subj.saveToFile(cursor, name, predicate), is(result));

    verifyInOrder(fileTemplate).execute(eq(new File(subj.dir, name + ".txt")), writerCallback.capture());

    // шаг2: doWithWriter
    assertThat(writerCallback.getValue().doWithWriter(lazyWriter), is(false));
  }

  @Test
  public void testSaveToFile_cursor() throws Exception {
    // шаг1: fileTemplate.execute
    String name = uidS();
    boolean result = uidBool();
    when(fileTemplate.execute(any(File.class), anyWriterCallback())).thenReturn(result);
    List<Account> accounts = asList(newAccount(), newAccount(), newAccount());

    assertThat(subj.saveToFile(accounts.iterator(), name, predicate), is(result));

    verifyInOrder(fileTemplate).execute(eq(new File(subj.dir, name + ".txt")), writerCallback.capture());

    // шаг2: doWithWriter
    when(predicate.apply(any(Account.class))).thenReturn(true, false, true);

    assertThat(writerCallback.getValue().doWithWriter(lazyWriter), is(true));

    verifyInOrder(predicate).apply(accounts.get(0));
    verifyInOrder(formatter).write(writer, accounts.get(0));
    verifyInOrder(predicate).apply(accounts.get(1));
    verifyInOrder(predicate).apply(accounts.get(2));
    verifyInOrder(formatter).write(writer, accounts.get(2));
  }

  @SuppressWarnings("unchecked")
  private WriterCallback<Boolean> anyWriterCallback() {
    return any(WriterCallback.class);
  }

  @SuppressWarnings("unchecked")
  private CursorCallback<Account, Boolean> anyCursorCallback() {
    return any(CursorCallback.class);
  }

  private Account newAccount() {
    Account a = new Account();
    a.setName("Имя" + uid());
    return a;
  }
}