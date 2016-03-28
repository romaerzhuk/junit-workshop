package workshop;

import java.sql.SQLException;

public interface CursorCreator<T> {
  Cursor<T> open() throws SQLException;
}
