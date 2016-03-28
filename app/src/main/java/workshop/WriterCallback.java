package workshop;

import java.io.IOException;

public interface WriterCallback<T> {
  T doWithWriter(LazyWriter lazyWriter) throws IOException;
}
