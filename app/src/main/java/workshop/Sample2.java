package workshop;

import javax.annotation.Resource;

public class Sample2 {
  @Resource
  private Dao dao;

  public long createAccount(String name) {
    Account account = new Account();
    account.setId(dao.nextId());
    account.setName(name);
    dao.save(account);
    return account.getId();
  }
}
