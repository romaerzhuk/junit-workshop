package workshop;

import java.io.IOException;
import java.io.Writer;

public interface Formatter {
  void write(Writer out, Object obj) throws IOException;
}
