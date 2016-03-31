package workshop;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyIterator;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.same;
import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.uid;
import static ru.iteco.test.utils.TestUtil.uidBool;
import static ru.iteco.test.utils.TestUtil.uidS;

import com.sun.org.apache.xpath.internal.operations.Bool;
import java.io.File;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hamcrest.Matchers;
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
import workshop.Sample2.SaveToFile;

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
  private LazyWriter lazyWriter;

  @Captor
  private ArgumentCaptor<CursorCallback<Account, Boolean>> cursorCallback;
  @Captor
  private ArgumentCaptor<WriterCallback<Object>> writerCallback;

  @LogMock
  private LoggerMock log;

  @BeforeMock
  public void beforeMock() {
    subj = new Sample2();
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

    SaveToFile saveToFile = (SaveToFile) cursorCallback.getValue();
    assertThat(saveToFile.name, is(name));
    assertThat(saveToFile.predicate, is(predicate));
    assertThat(saveToFile.cursor, nullValue());
  }

  @Test
  public void testSaveToFile_doWithCursor() throws Exception {
    String name = uidS();
    SaveToFile saveToFile = subj.new SaveToFile(name, predicate);
    Iterator<Account> cursor = asList(newAccount(), newAccount(), newAccount()).iterator();
    boolean result = uidBool();
    when(fileTemplate.execute(any(File.class), anyWriterCallback())).thenReturn(result);

    assertThat(saveToFile.doWithCursor(cursor), is(result));

    verifyInOrder(fileTemplate).execute(eq(new File(subj.dir, name + ".txt")), same(saveToFile));
    assertThat(saveToFile.cursor, is(cursor));
  }

  @Test
  public void testSaveToFile_doWithWriter() throws Exception {
    String name = uidS();
    when(predicate.apply(any(Account.class))).thenReturn(true, false, true);
    List<Account> accounts = asList(newAccount(), newAccount(), newAccount());
    SaveToFile saveToFile = subj.new SaveToFile(name, predicate);
    saveToFile.cursor = accounts.iterator();

    assertThat(saveToFile.doWithWriter(lazyWriter), is(true));

    verifyInOrder(predicate).apply(accounts.get(0));
    verifyInOrder(lazyWriter).get();
    verifyInOrder(formatter).write(writer, accounts.get(0));
    verifyInOrder(predicate).apply(accounts.get(1));
    verifyInOrder(predicate).apply(accounts.get(2));
    verifyInOrder(lazyWriter).get();
    verifyInOrder(formatter).write(writer, accounts.get(2));
  }

  @Test
  public void testSaveToFile_doWithWriter_cursor_is_empty() throws Exception {
    String name = uidS();
    when(predicate.apply(any(Account.class))).thenReturn(true, false, true);
    SaveToFile saveToFile = subj.new SaveToFile(name, predicate);
    Iterator<Account> cursor = emptyIterator();
    saveToFile.cursor = cursor;
    when(predicate.apply(any(Account.class))).thenReturn(true, false, true);

    assertThat(saveToFile.doWithWriter(lazyWriter), is(false));
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