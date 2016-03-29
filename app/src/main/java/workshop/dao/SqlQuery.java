package workshop.dao;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SqlQuery {
  private String sql;
  private Map<String, Object> params = new LinkedHashMap<>();

  public SqlQuery(String sql) {
    this.sql = sql;
  }

  public void add(String name, Object value) {
    params.put(name, value);
  }

  public String sql() {
    if (params.isEmpty()) {
      return sql;
    }
    String s = "\nWHERE ";
    StringBuilder sb = new StringBuilder(sql);
    for (Entry<String, Object> entry: params.entrySet()) {
      sb.append(s).append(entry.getKey()).append("=?");
      s = "\n  AND ";
    }
    return sb.toString();
  }

  public Collection<Object> params() {
    return params.values();
  }
}
