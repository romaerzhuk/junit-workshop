package workshop;

import java.io.IOException;
import java.io.Writer;

public interface LazyWriter {
  Writer get() throws IOException;
}
