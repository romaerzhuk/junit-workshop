package workshop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import javax.annotation.Resource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sample2 {
  private Logger log = LoggerFactory.getLogger(getClass());

  @Resource
  private Dao dao;
  @Resource
  File dir;
  @Resource
  private Formatter formatter;

  /**
   * Сохраняет в файл name.txt список счетов удовлетворяющих условию
   * @param name имя
   * @param predicate условие
   * @return true, если хотя бы одна запись найдена, файл был создан, иначе false
   * @throws IOException
   * @throws SQLException
   */
  public boolean saveToFile(String name, Predicate predicate) throws IOException, SQLException {
    Writer out = null;
    Cursor<Account> cursor = dao.openByName(name);
    try {
      while (cursor.hasNext()) {
        Account account = cursor.next();
        if (predicate.apply(account)) {
          if (out == null) {
            out = new OutputStreamWriter(new FileOutputStream(new File(dir, name)), "UTF-8");
          }
          formatter.write(out, account);
        }
      }
      return out != null;
    } finally {
      close(cursor);
      IOUtils.closeQuietly(out);
    }
  }

  private void close(Cursor<Account> cursor) {
    try {
      cursor.close();
    } catch (Throwable t) {
      log.warn("Невозможно закрыть курсор", t);
    }
  }
}
