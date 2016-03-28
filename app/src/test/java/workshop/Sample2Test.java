package workshop;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.newTimestamp;
import static ru.iteco.test.utils.TestUtil.uid;
import static ru.iteco.test.utils.TestUtil.uidS;

import java.sql.Timestamp;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.iteco.test.utils.annotations.BeforeMock;

public class Sample2Test {
  @Rule
  public TestRule rule = AppRule.ruleChain(this);

  @InjectMocks
  private Sample2 subj;

  @Mock
  private Dao dao;
  @Captor
  private ArgumentCaptor<Account> accountCaptor;

  private Timestamp now;

  @BeforeMock
  public void beforeMock() {
    subj = new Sample2();
  }

  @Before
  public void setUp() {
    now = newTimestamp();
  }

  @Test
  public void testCreateAccount() {
    long id = uid();
    when(dao.nextId()).thenReturn(id);
    String name = uidS();

    assertThat(subj.createAccount(name), is(id));

    verifyInOrder(dao).save(accountCaptor.capture());
    assertThat(account().getId(), is(id));
    assertThat(account().getName(), is(name));
    //assertThat(account().getCreated(), is(now));
  }

  private Account account() {
    return accountCaptor.getValue();
  }
}