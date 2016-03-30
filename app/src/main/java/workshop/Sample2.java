package workshop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Iterator;
import javax.annotation.Resource;
import org.apache.commons.io.IOUtils;

public class Sample2 {
  @Resource
  private Dao dao;
  @Resource
  File dir;
  @Resource
  private Formatter formatter;
  @Resource
  private CursorTemplate cursorTemplate;
  @Resource
  private FileTemplate fileTemplate;

  /**
   * Сохраняет в файл name.txt список счетов удовлетворяющих условию
   * @param name имя
   * @param predicate условие
   * @return true, если хотя бы одна запись найдена, файл был создан, иначе false
   * @throws IOException
   * @throws SQLException
   */
  public boolean saveToFile(final String name, final Predicate predicate) throws IOException, SQLException {
    return cursorTemplate.execute(dao.cursorCreatorByName(name), new CursorCallback<Account, Boolean>() {
        @Override
        public Boolean doWithCursor(Iterator<Account> cursor) throws SQLException {
          return self().saveToFile(cursor, name, predicate);
        }
      });
  }

  Sample2 self() {
    return this;
  }

  boolean saveToFile(Iterator<Account> cursor, String name, Predicate predicate) {
    try {
      return fileTemplate.execute(new File(dir, name + ".txt"), new WriterCallback<Boolean>() {
        @Override
        public Boolean doWithWriter(LazyWriter lazyWriter) throws IOException {
          Writer out = null;
            while (cursor.hasNext()) {
              Account account = cursor.next();
              if (predicate.apply(account)) {
                out = lazyWriter.get();
                formatter.write(out, account);
              }
            }
            return out != null;
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e); // TODO поправить хак
    }
  }
}
