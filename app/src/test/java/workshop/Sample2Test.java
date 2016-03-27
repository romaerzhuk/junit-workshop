package workshop;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static ru.iteco.test.utils.TestUtil.uidS;

public class Sample2Test {
  private Sample2 subj;

  private Predicate predicate;

  @Before
  public void setUp() {
    subj = new Sample2();
  }

  @Test
  public void testSaveToFile() throws Exception {
    String name = uidS();

    assertThat(subj.saveToFile(name, predicate), is(true));
  }
}