package workshop;

import java.util.Calendar;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.iteco.test.utils.TestUtil;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

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
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(0);
    c.set(2016, 03, 27, 10, 33, 12);

    assertEquals(c.getTime(), subj.parse("2016-03-27T10:33:12"));
  }
}