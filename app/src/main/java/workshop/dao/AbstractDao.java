package workshop.dao;

import java.util.List;

public abstract class AbstractDao {
  protected <T> List<T> executeQuery(Class<T> valueClass, SqlQuery query) {
    // TODO заглушка реализации
    return null;
  }
}
