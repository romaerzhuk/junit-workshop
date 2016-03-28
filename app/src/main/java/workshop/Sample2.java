package workshop;

import java.sql.Timestamp;
import java.util.List;
import javax.annotation.Resource;

public class Sample2 {
  @Resource
  private Dao dao;
  @Resource
  private Clock clock;

  public long createAccount(String name) {
    Account account = new Account();
    account.setId(dao.nextId());
    account.setName(name);
    account.setCreated(clock.newTimestamp());
    dao.save(account);
    return account.getId();
  }

  public void createAccounts(List<String> names) {
    for (String name: names) {
      self().createAccount(name);
    }
  }

  public void copy(Account account) {
//    Account clone = account.clone();
//    clone.setId(dao.nextId());
//    clone.setCreated(clock.newTimestamp());
//    dao.save(clone);
  }

  // нужен для mock-тестирования
  Sample2 self() {
    return this;
  }
}
