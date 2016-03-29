package workshop;

import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.uid;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.iteco.test.utils.annotations.BeforeMock;

public class Sample5Test {
  @Rule
  public TestRule rule = AppRule.ruleChain(this);

  @InjectMocks
  private Sample5 subj;

  @Mock
  private Dao dao;

  @BeforeMock
  public void init() {
    subj = new Sample5();
  }

  @Test
  public void testSave_position_is_null() throws Exception {
    Position pos = new Position();

    subj.save(pos);

    verifyInOrder(dao).insert(pos);
  }

  @Test
  public void testSave_position_is_INIT() throws Exception {
    Position pos = new Position();
    pos.setState(State.INIT);

    subj.save(pos);

    verifyInOrder(dao).insert(pos);
  }

  @Test
  public void testSave_position_is_other() throws Exception {
    Position pos = new Position();
    pos.setState(uid(State.class, State.INIT));

    subj.save(pos);

    verifyInOrder(dao).update(pos);
  }
}