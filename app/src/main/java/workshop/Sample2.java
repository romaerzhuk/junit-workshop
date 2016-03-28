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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sample2 {
  @Resource
  private Dao dao;
  @Resource
  File dir;
  @Resource
  private Formatter formatter;
  @Resource
  private CursorTemplate cursorTemplate;

  /**
   * Сохраняет в файл name.txt список счетов удовлетворяющих условию
   * @param name имя
   * @param predicate условие
   * @return true, если хотя бы одна запись найдена, файл был создан, иначе false
   * @throws IOException
   * @throws SQLException
   */
  public boolean saveToFile(String name, Predicate predicate) throws IOException, SQLException {
    return cursorTemplate.execute(dao.cursorCreatorByName(name), new CursorCallback<Account, Boolean>() {
        @Override
        public Boolean doWithCursor(Iterator<Account> cursor) throws SQLException {
          Writer out = null;
          try {
            while (cursor.hasNext()) {
              Account account = cursor.next();
              if (predicate.apply(account)) {
                if (out == null) {
                  out = new OutputStreamWriter(new FileOutputStream(new File(dir, name + ".txt")), "UTF-8");
                }
                formatter.write(out, account);
              }
            }
            return out != null;
          } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e); // TODO поправить хак
          } finally {
            IOUtils.closeQuietly(out);
          }
        }
      });
  }
}
