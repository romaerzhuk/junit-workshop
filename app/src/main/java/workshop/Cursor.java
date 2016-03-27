package workshop;

import java.sql.SQLException;
import java.util.Iterator;

public interface Cursor<T> extends Iterator<T> {
  void close() throws SQLException;
}
