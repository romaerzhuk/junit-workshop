package workshop;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CursorTemplate {
  private final Logger log = LoggerFactory.getLogger(getClass());

  public <E, R> R execute(CursorCreator<E> creator, CursorCallback<E, R> callback) throws SQLException {
    Cursor<E> cursor = creator.open();
    try {
      return callback.doWithCursor(cursor);
    } finally {
      close(cursor);
    }
  }

  private void close(Cursor<?> cursor) {
    try {
      cursor.close();
    } catch (Throwable t) {
      log.warn("Невозможно закрыть курсор", t);
    }
  }
}
