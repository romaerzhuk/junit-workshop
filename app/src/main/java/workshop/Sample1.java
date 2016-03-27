package workshop;

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

public class Sample1 {
  public Date parse(String dateTime) throws IllegalArgumentException {
    try {
      return DateUtils.parseDate(dateTime, "yyyy-MM-dd'T'hh:mm:ss");
    } catch (Exception e) {
      throw new IllegalArgumentException("Некорректно: dateTime=[" + dateTime + "]");
    }
  }
}
