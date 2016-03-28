package workshop;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.newTimestamp;

import java.sql.Timestamp;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ System.class })
public class ClockTest {
  @Rule
  public TestRule rule = AppRule.ruleChain(this);

  private Clock subj;

  private Timestamp now;

  @Before
  public void setUp() {
    subj = new Clock();
    now = newTimestamp();
    mockStatic(System.class);
    when(System.currentTimeMillis()).thenReturn(now.getTime());
  }

  @Test
  public void testNewTimestamp() {
    assertThat(subj.newTimestamp(), is(now));

    verifyStatic();
    System.currentTimeMillis();
  }
}
