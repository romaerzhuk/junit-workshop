package workshop;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Account implements Cloneable {
  private long id;
  private String name;
  private BigDecimal amount;
  private boolean closed;
  private Timestamp created;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  @Override
  public Account clone()  {
    try {
      return (Account) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException();
    }
  }
}
