package workshop;

import java.sql.SQLException;

public interface Dao {
  Cursor<Account> openByName(String name) throws SQLException;

  CursorCreator<Account> cursorCreatorByName(String name);
}
