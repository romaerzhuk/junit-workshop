package workshop;

import java.util.List;
import workshop.dao.AbstractDao;
import workshop.dao.SqlQuery;

public class SampleDao extends AbstractDao {

  public List<Account> findByCriteria(Account criteria) {
    SqlQuery query = new SqlQuery("SELECT ID, NAME, AMOUNT, CLOSED"
        + "\nFROM ACCOUNT");
    if (criteria.getName() != null) {
      query.add("NAME", criteria.getName());
    }
    if (criteria.getAmount() != null) {
      query.add("AMOUNT", criteria.getAmount());
    }
    return self().executeQuery(Account.class, query);
  }

  SampleDao self() {
    return this;
  }
}
