package workshop;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import workshop.dao.AbstractDao;
import workshop.dao.SqlQuery;

public class SampleDao extends AbstractDao {

  public List<Account> findByName(Account criteria) {
    SqlQuery query = new SqlQuery("SELECT ID, NAME, AMOUNT, CLOSED"
        + "\nFROM ACCOUNT");
    if (criteria.getName() != null) {
      query.addWhere("NAME", criteria.getName());
    }
    if (criteria.getName() != null) {
      query.addWhere("NAME", criteria.getName());
    }


  }
}
