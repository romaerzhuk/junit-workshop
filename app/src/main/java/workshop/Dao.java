package workshop;

import java.util.List;

public interface Dao {
  void save(Account account);

  long nextId();
}
