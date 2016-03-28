package workshop;

import java.sql.SQLException;
import java.util.Iterator;

public interface CursorCallback<E, R> {
  R doWithCursor(Iterator<E> cursor) throws SQLException;
}
