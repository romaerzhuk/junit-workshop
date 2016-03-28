package workshop;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Iterator;
import javax.annotation.Resource;

public class Sample2 {
  private class SaveToFile implements CursorCallback<Account, Boolean>, WriterCallback<Boolean> {
    private String name;
    private Predicate predicate;
    private Iterator<Account> cursor;

    public SaveToFile(String name, Predicate predicate) {
      this.name = name;
      this.predicate = predicate;
    }

    @Override
    public Boolean doWithCursor(Iterator<Account> cursor) throws SQLException {
      this.cursor = cursor;
      try {
        return fileTemplate.execute(new File(dir, name + ".txt"), this);
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e); // TODO поправить хак
      }
    }

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
  }
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
  public boolean saveToFile(String name, Predicate predicate) throws IOException, SQLException {
    return cursorTemplate.execute(dao.cursorCreatorByName(name), new SaveToFile(name, predicate));
  }
}
