package workshop;

import org.junit.rules.RuleChain;
import ru.iteco.test.utils.rules.BeforeMockRule;
import ru.iteco.test.utils.rules.LoggerRule;
import ru.iteco.test.utils.rules.MemoryRule;
import ru.iteco.test.utils.rules.MockitoRule;
import ru.iteco.test.utils.rules.ParameterizedRule;
import ru.iteco.test.utils.rules.TempDirRule;

public class AppRule {
  private AppRule() {}
  /**
   * Creates test rules for app module.
   *
   * @param test
   *          object containg test methods
   * @return test rules
   */
  public static RuleChain ruleChain(Object test) {
    return RuleChain.outerRule(new LoggerRule(test, true))
        .around(new ParameterizedRule(test))
        .around(new BeforeMockRule(test))
        .around(new MockitoRule(test))
        .around(MemoryRule.of(test))
        .around(new LoggerRule(test, false))
        .around(new TempDirRule(test.getClass()));
  }

}