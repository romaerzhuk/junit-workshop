package workshop;

import java.sql.Timestamp;
import javax.annotation.Resource;

public class Sample2 {
  @Resource
  private Dao dao;

  public long createAccount(String name) {
    Account account = new Account();
    account.setId(dao.nextId());
    account.setName(name);
    account.setCreated(new Timestamp(System.currentTimeMillis()));
    dao.save(account);
    return account.getId();
  }
}
