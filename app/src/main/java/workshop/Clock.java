package workshop;

import java.sql.Timestamp;

public class Clock {
  public Timestamp newTimestamp() {
    return new Timestamp(System.currentTimeMillis());
  }
}

