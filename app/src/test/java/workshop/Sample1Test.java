package workshop;

import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static ru.iteco.test.utils.TestUtil.toDate;

/**
 * Created by roman on 27.03.16.
 */
public class Sample1Test {
  private Sample1 subj;

  @Before
  public void setUp() {
    subj = new Sample1();
  }

  @Test
  public void testParse() throws Exception {
    Date expected = toDate("27.03.2016 10:33:12");

    assertEquals(expected, subj.parse("2016-03-27T10:33:12"));
  }

  @Test
  public void testParse_Exception() throws Exception {
    try {
      subj.parse("2016-03-27T10:33:1?");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Некорректно: dateTime=[2016-03-27T10:33:1?]", e.getMessage());
    }
  }
}