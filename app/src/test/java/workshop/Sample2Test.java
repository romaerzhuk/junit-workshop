package workshop;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.newTimestamp;
import static ru.iteco.test.utils.TestUtil.uid;
import static ru.iteco.test.utils.TestUtil.uidBool;
import static ru.iteco.test.utils.TestUtil.uidS;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.iteco.test.utils.annotations.BeforeMock;
import ru.iteco.test.utils.hamcrest.PropertiesMatcher;

public class Sample2Test {
  @Rule
  public TestRule rule = AppRule.ruleChain(this);

  @InjectMocks
  private Sample2 subj;
  @Mock
  private Sample2 self;
  @Mock
  private Clock clock;
  @Mock
  private Dao dao;
  @Captor
  private ArgumentCaptor<Account> accountCaptor;

  private Timestamp now;

  @BeforeMock
  public void beforeMock() {
    subj = new Sample2() {
      @Override Sample2 self() {
        return self;
      }
    };
  }

  @Before
  public void setUp() {
    now = newTimestamp();
    when(clock.newTimestamp()).thenReturn(now);
  }

  @Test
  public void testCreateAccount() {
    long id = uid();
    when(dao.nextId()).thenReturn(id);
    String name = uidS();

    assertThat(subj.createAccount(name), is(id));

    verifyInOrder(dao).nextId();
    verifyInOrder(clock).newTimestamp();
    verifyInOrder(dao).save(accountCaptor.capture());
    assertThat(account(), account(id, name, new Account()));
  }

  @Test
  public void testCreateAccounts() {
    String name1 = uidS();
    String name2 = uidS();

    subj.createAccounts(asList(name1, name2));

    verifyInOrder(self).createAccount(name1);
    verifyInOrder(self).createAccount(name2);
  }

  @Test
  public void testCopy() {
    Account account = newAccount();
    long id = uid();
    when(dao.nextId()).thenReturn(id);

    subj.copy(account);
    verifyInOrder(dao).nextId();
    verifyInOrder(clock).newTimestamp();
    verifyInOrder(dao).save(accountCaptor.capture());
    assertThat(account(), account(id, account.getName(), account));
  }

  private Matcher<Account> account(final long id, final String name, final Account copy) {
    return new PropertiesMatcher<Account>("account") {
      @Override protected void check(Account it) throws Throwable {
        add("id", it.getId(), id);
        add("name", it.getName(), name);
        add("created", it.getCreated(), now);
        add("closed", it.isClosed(), copy.isClosed());
        add("amount", it.getAmount(), copy.getAmount());
      }
    };
  }

  private Account newAccount() {
    Account a = new Account();
    a.setCreated(newTimestamp());
    a.setId(uid());
    a.setName(uidS());
    a.setAmount(BigDecimal.valueOf(uid()));
    a.setClosed(uidBool());
    return a;
  }

  private Account account() {
    return accountCaptor.getValue();
  }
}