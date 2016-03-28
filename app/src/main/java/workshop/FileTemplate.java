package workshop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.commons.io.IOUtils;

public class FileTemplate {
  private static class LazyFileWriter implements LazyWriter {
    private File file;
    private Writer out;

    public LazyFileWriter(File file) {
      this.file = file;
    }

    @Override
    public Writer get() throws IOException {
      if (out == null) {
        out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
      }
      return out;
    }

    public void close() {
      IOUtils.closeQuietly(out);
    }
  }

  public <R> R execute(File file, WriterCallback<R> callback) throws IOException {
    LazyFileWriter writer = new LazyFileWriter(file);
    try {
      return callback.doWithWriter(writer);
    } finally {
      writer.close();
    }
  }
}
