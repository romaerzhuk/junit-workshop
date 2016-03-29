package ru.iteco.test.utils.rules;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

/**
 * Инициализирует UIManager перед запуском теста
 *
 * @author Roman Erzhukov I-Teco 2015-07-20
 */
public class UIManagerRule implements TestRule {
  private static final TestRule instance = new UIManagerRule();
  private UIManagerRule() {
  }

  /**
   * Возвращает экземпляр {@link UIManagerRule}
   *
   * @return {@link #instance}
   */
  public static TestRule of() {
    return instance;
  }

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {

      @Override
      public void evaluate() throws Throwable {
        try {
          AppContext.getAppContext().remove(SwingUtilities2.LAF_STATE_KEY);
          UIManager.setLookAndFeel(new FakeLookAndFill());
          UIManager.put("Table.defaultCellRendererMargin", 0);
          UIManager.put("Table.focusEditableCellHighlightBorder", new EmptyBorder(0, 0, 0, 0));
          UIManager.put("Table.cellNoFocusBorder", new EmptyBorder(0, 0, 0, 0));
          UIManager.put("Table.focusCellHighlightBorder", new EmptyBorder(0, 0, 0, 0));
          UIManager.put("RootPane.ancestorInputMap", new InputMap());
          UIManager.put("GkLabelField.field.foreground", new Color(256));
          UIManager.put("GkLabelField.underline.border", new EmptyBorder(0, 0, 0, 0));
          UIManager.put("GkLabel.opaque", true);
          UIManager.put("GkCompoundComponent.notnull.icon", new ImageIcon());
          UIManager.put("GkCompoundComponent.notnull.underline.border", new EmptyBorder(0, 0, 0, 0));
          UIManager.put("GkCompoundComponent.notnull.foreground", new Color(256));
          UIManager.put("GkCompoundComponent.underline.border", new EmptyBorder(0, 0, 0, 0));
          UIManager.put("GkCompoundComponent.underlined.label", false);
          UIManager.put("TableHeader.descending.image", new BufferedImage(1, 1, 1));
          UIManager.put("TableHeader.border.color", new Color(256));
          UIManager.put("TableHeader.ascending.image", new BufferedImage(1, 1, 1));
          UIManager.put("TableHeader.image", new BufferedImage(1, 1, 1));
          UIManager.put("ActionButtonFactory.default.actions.keystrokes.visible", false);
          UIManager.put("ActionDescriptor.icon.standard.disabled", new ImageIcon());
          UIManager.put("ActionDescriptor.icon.standard.mouse.pressed", new ImageIcon());
          UIManager.put("ActionDescriptor.icon.standard.mouse.over", new ImageIcon());
          UIManager.put("ActionDescriptor.icon.standard", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.ok.disabled", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.ok.mouse.pressed", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.ok.mouse.over", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.ok", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.cancel.disabled", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.cancel.mouse.pressed", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.cancel.mouse.over", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.cancel", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.close.disabled", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.close.mouse.pressed", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.close.mouse.over", new ImageIcon());
          UIManager.put("ActionButtonFactory.icon.close", new ImageIcon());
          UIManager.put("CalendarControl.calendar.icon", new ImageIcon());
          UIManager.put("CalendarControl.calendar.pressed.icon", new ImageIcon());
          UIManager.put("CalendarControl.calendar.rollover.icon", new ImageIcon());
          UIManager.put("CalendarControl.calendar.disabled.icon", new ImageIcon());
          UIManager.put("CalendarControl.calendar.icon", new ImageIcon());
          UIManager.put("LookupComponent.dropdown.button.pressed.icon", new ImageIcon());
          UIManager.put("LookupComponent.dropdown.button.rollover.icon", new ImageIcon());
          UIManager.put("LookupComponent.dropdown.button.disabled.icon", new ImageIcon());
          UIManager.put("LookupComponent.dropdown.button.icon", new ImageIcon());
          base.evaluate();
        } finally {
          safePut("Table.defaultCellRendererMargin", null);
          safePut("Table.focusEditableCellHighlightBorder", null);
          safePut("Table.cellNoFocusBorder", null);
          safePut("Table.focusCellHighlightBorder", null);
          safePut("RootPane.ancestorInputMap", null);
          safePut("GkLabelField.field.foreground", null);
          safePut("GkLabelField.underline.border", null);
          safePut("GkLabel.opaque", false);
          safePut("GkCompoundComponent.notnull.icon", null);
          safePut("GkCompoundComponent.notnull.underline.border", null);
          safePut("GkCompoundComponent.notnull.foreground", null);
          safePut("GkCompoundComponent.underline.border", null);
          safePut("GkCompoundComponent.underlined.label", false);
          safePut("TableHeader.descending.image", null);
          safePut("TableHeader.border.color", null);
          safePut("TableHeader.ascending.image", null);
          safePut("TableHeader.image", null);
          safePut("ActionButtonFactory.default.actions.keystrokes.visible", false);
          safePut("ActionDescriptor.icon.standard.disabled", null);
          safePut("ActionDescriptor.icon.standard.mouse.pressed", null);
          safePut("ActionDescriptor.icon.standard.mouse.over", null);
          safePut("ActionDescriptor.icon.standard", null);
          safePut("ActionButtonFactory.icon.ok.disabled", null);
          safePut("ActionButtonFactory.icon.ok.mouse.pressed", null);
          safePut("ActionButtonFactory.icon.ok.mouse.over", null);
          safePut("ActionButtonFactory.icon.ok", null);
          safePut("ActionButtonFactory.icon.cancel.disabled", null);
          safePut("ActionButtonFactory.icon.cancel.mouse.pressed", null);
          safePut("ActionButtonFactory.icon.cancel.mouse.over", null);
          safePut("ActionButtonFactory.icon.cancel", null);
          safePut("ActionButtonFactory.icon.close.disabled", null);
          safePut("ActionButtonFactory.icon.close.mouse.pressed", null);
          safePut("ActionButtonFactory.icon.close.mouse.over", null);
          safePut("ActionButtonFactory.icon.close", null);
          safePut("CalendarControl.calendar.icon", null);
          safePut("CalendarControl.calendar.pressed.icon", null);
          safePut("CalendarControl.calendar.rollover.icon", null);
          safePut("CalendarControl.calendar.disabled.icon", null);
          safePut("CalendarControl.calendar.icon", null);
          safePut("LookupComponent.dropdown.button.pressed.icon", null);
          safePut("LookupComponent.dropdown.button.rollover.icon", null);
          safePut("LookupComponent.dropdown.button.disabled.icon", null);
          safePut("LookupComponent.dropdown.button.icon", null);
          AppContext.getAppContext().remove(SwingUtilities2.LAF_STATE_KEY);
        }
      }
    };
  }

  private static void safePut(String key, Object value) {
    try {
      UIManager.put(key, value);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
  }
}
