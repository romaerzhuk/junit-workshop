package workshop;

public interface Dao {
  Cursor<Account> openByName(String name);
}
