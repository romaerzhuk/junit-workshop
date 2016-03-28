package workshop;

import java.sql.Timestamp;
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
}
