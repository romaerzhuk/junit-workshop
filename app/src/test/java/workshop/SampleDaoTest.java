package workshop;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.dec;
import static ru.iteco.test.utils.TestUtil.readFileToString;
import static ru.iteco.test.utils.TestUtil.uid;
import static ru.iteco.test.utils.TestUtil.uidBool;
import static ru.iteco.test.utils.TestUtil.uidS;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.hamcrest.Matchers;
import org.junit.rules.TestRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.iteco.test.utils.TestUtil;
import workshop.dao.SqlQuery;

public class SampleDaoTest {
  private class SampleDaoSpy extends SampleDao {
    @Override
    SampleDao self() {
      return self;
    }

    @Override
    protected <T> List<T> executeQuery(Class<T> valueClass, SqlQuery query) {
      throw new UnsupportedOperationException("унаследован для mock-тестирования");
    }
  }

  @Rule
  public TestRule rule = AppRule.ruleChain(this);

  private SampleDaoSpy subj;
  @Mock
  private SampleDaoSpy self;
  @Captor
  private ArgumentCaptor<SqlQuery> query;

  @Before
  public void setUp() {
    subj = new SampleDaoSpy();
  }

  @Test
  public void testFindByCriteria() throws Exception {
    Account criteria = newAccount();
    List<Account> result = asList(newAccount(), newAccount());
    when(self.executeQuery(eq(Account.class), any(SqlQuery.class))).thenReturn(result);

    assertThat(subj.findByCriteria(criteria), is(result));

    verifyInOrder(self).executeQuery(eq(Account.class), query.capture());
    SqlQuery q = query.getValue();
    assertThat(q.sql(), is(readFileToString("findByCriteria.sql")));
    assertThat(q.params(), contains(criteria.getName(), criteria.getAmount()));
  }

  @Test
  public void testFindByCriteria_empty() throws Exception {
    Account criteria = new Account();
    List<Account> result = asList(newAccount(), newAccount());
    when(self.executeQuery(eq(Account.class), any(SqlQuery.class))).thenReturn(result);

    assertThat(subj.findByCriteria(criteria), is(result));

    verifyInOrder(self).executeQuery(eq(Account.class), query.capture());
    SqlQuery q = query.getValue();
    assertThat(q.sql(), is(readFileToString("findByCriteria_empty.sql")));
    assertThat(q.params(), empty());
  }

  private String readFileToString(String name) throws Exception {
    return TestUtil.readFileToString(getClass(), name, "UTF-8").replaceAll("\r\n", "\n");
  }

  private Account newAccount() {
    Account a = new Account();
    a.setId(uid());
    a.setClosed(uidBool());
    a.setName(uidS());
    a.setAmount(dec(uid()));
    return a;
  }
}