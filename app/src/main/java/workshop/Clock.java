package workshop;

import java.sql.Timestamp;

public class Clock {
  public Timestamp newTimestamp() {
    return new Timestamp(self().currentTimeMillis());
  }

  // нужен для mock-тестирования
  Clock self() {
    return this;
  }

  // не тестируется: static System.currentTimeMillis()
  long currentTimeMillis() {
    return System.currentTimeMillis();
  }
}

