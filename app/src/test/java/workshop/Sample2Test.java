package workshop;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.iteco.test.utils.MockUtils;
import ru.iteco.test.utils.annotations.BeforeMock;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static ru.iteco.test.utils.MockUtils.*;
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
  private Cursor<workshop.Account> cursor;

  @BeforeMock
  public void beforeMock() {
    subj = new Sample2();
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
}