package workshop;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.newTimestamp;

import java.sql.Timestamp;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

public class ClockTest {
  @Rule
  public TestRule rule = AppRule.ruleChain(this);

  private Clock subj;
  @Mock
  private Clock self;

  private Timestamp now;

  @Before
  public void setUp() {
    subj = new Clock() {
      @Override Clock self() {
        return self;
      }
    };
    now = newTimestamp();
    when(self.currentTimeMillis()).thenReturn(now.getTime());
  }

  @Test
  public void testNewTimestamp() {
    assertThat(subj.newTimestamp(), is(now));

    verifyInOrder(self).currentTimeMillis();
  }
}
