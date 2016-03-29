package workshop.dao;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SqlQuery {
  private String sql;
  private Map<String, Object> values = new LinkedHashMap<>();

  public SqlQuery(String sql) {
    this.sql = sql;
  }

  public void addWhere(String name, Object value) {
    values.put(name, value);
  }

  public String getSql() {
    if (values.isEmpty()) {
      return sql;
    }
    StringBuilder sb = new StringBuilder();
    for (Entry<String, Object> entry: values.entrySet()) {

    }
  }
}
