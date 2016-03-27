package workshop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

public class Sample1 {
  public Date parse(String dateTime) throws IllegalArgumentException {
    try {
      return DateUtils.parseDate(dateTime, "yyyy-MM-dd'T'hh:mm:ss");
    } catch (ParseException e) {
      throw new IllegalArgumentException("Некорректно: dateTime=[" + dateTime + "]");
    }
  }
}
