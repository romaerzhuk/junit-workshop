package workshop;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.uidS;

import java.io.File;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.iteco.test.utils.TestUtil;

public class FileTemplateTest {
  @Rule public TestRule rule = AppRule.ruleChain(this);

  private FileTemplate subj;

  @Mock
  private WriterCallback<Object> callback;

  private File dir;

  @Before
  public void setUp() {
    subj = new FileTemplate();
    dir = TestUtil.getTempFolder(getClass());
  }

  @Test
  public void testExecute_write() throws Exception {
    File file = new File(dir, uidS());
    Object result = new Object();
    String message = "Привет, мир!" + uidS();
    when(callback.doWithWriter(any(LazyWriter.class))).thenAnswer(new Answer<Object>() {
      @Override public Object answer(InvocationOnMock inv) throws Throwable {
        LazyWriter lazy = inv.getArgumentAt(0, LazyWriter.class);
        lazy.get().write(message);
        return result;
      }
    });

    assertThat(subj.execute(file, callback), is(result));

    assertThat(readFileToString(file, "UTF-8"), is(message));

    verifyInOrder(callback).doWithWriter(isA(LazyWriter.class));
  }

  @Test
  public void testExecute_no_write() throws Exception {
    File file = new File(dir, uidS());
    Object result = new Object();
    when(callback.doWithWriter(any(LazyWriter.class))).thenReturn(result);

    assertThat(subj.execute(file, callback), is(result));

    assertThat(file.exists(), is(false));
    verifyInOrder(callback).doWithWriter(isA(LazyWriter.class));
  }
}
