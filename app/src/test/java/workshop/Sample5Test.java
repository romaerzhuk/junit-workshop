package workshop;

import static org.mockito.Mockito.times;
import static ru.iteco.test.utils.MockUtils.verifyInOrder;
import static ru.iteco.test.utils.TestUtil.uid;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.iteco.test.utils.annotations.BeforeMock;
import ru.iteco.test.utils.annotations.Parameterized;
import ru.iteco.test.utils.annotations.ParameterizedField;
import ru.iteco.test.utils.rules.ParameterizedTest;

public class Sample5Test {
  @Rule
  public TestRule rule = AppRule.ruleChain(this);

  @InjectMocks
  private Sample5 subj;

  @Mock
  private Dao dao;
  private State state;

  @BeforeMock
  public void init() {
    subj = new Sample5();
  }

  @Test
  @Parameterized("setState") // указывает на метод(ы), которыми параметризируется тест
  public void testSave() {
    Position pos = new Position();
    pos.setState(state);

    boolean init = state == null || state == State.INIT;

    subj.save(pos);

    verifyInOrder(dao, times(init ? 1: 0)).insert(pos);
    verifyInOrder(dao, times(init ? 0: 1)).update(pos);
  }

  @ParameterizedField("state") // сбрасывает state=null после теста
  private void setState(ParameterizedTest test) {
    for (State s: State.values()) {
      state = s;
      test.run(state);
    }
    state = null;
    test.run(state);
  }

}