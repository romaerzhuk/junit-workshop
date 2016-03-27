package workshop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Sample1 {
  public Date parse(String dateTime) throws IllegalArgumentException {
    try {
      return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(dateTime);
    } catch (ParseException e) {
      throw new UnsupportedOperationException();
    }
  }
}
