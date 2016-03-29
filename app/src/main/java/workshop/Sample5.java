package workshop;

public class Sample5 {
  private Dao dao;

  public void save(Position position) {
    if (position.getState() == null || position.getState() == State.INIT) {
      dao.insert(position);
    } else {
      dao.update(position);
    }
  }
}
