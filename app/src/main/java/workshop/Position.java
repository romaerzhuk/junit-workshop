package workshop;

import java.math.BigDecimal;

public class Position {
  private String name;
  private State state;
  private BigDecimal value;
  private int count;
  private boolean closed;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }
}
