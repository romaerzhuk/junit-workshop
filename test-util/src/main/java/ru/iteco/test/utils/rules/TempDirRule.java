package ru.iteco.test.utils.rules;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.rules.ExternalResource;

import java.io.File;
import java.io.IOException;

/**
 * Created by shchipalkin on 03.09.2015.
 */
public class TempDirRule extends ExternalResource {

  private final File parentFolder;

  private File folder;

  private File subFolder;

  private Class clazz;

  public static final String BASE_ROOT = ".";

  public static final String SUBFODLER = "test";

  public TempDirRule(Class clazz) {
    this(clazz, BASE_ROOT);
  }

  public TempDirRule(Class clazz, String base) {
    this.parentFolder =
        new File(clazz.getClassLoader().getResource(base).getFile()).getParentFile();
    this.clazz = clazz;
  }

  @Override
  protected void before() throws Throwable {
    create();
  }

  @Override
  protected void after() {
    delete();
  }

  /**
   * for testing purposes only. Do not use.
   */
  public void create() throws IOException {
    folder = createTemporaryFolderIn(parentFolder);
  }

  public File createTemporaryFolderIn(File parentFolder) throws IOException {
    subFolder = createCommonSubFolder(parentFolder);
    return createCurrentTestFolder(subFolder);
  }

  private File createCurrentTestFolder(File subFolder) throws IOException {
    File createdFolder = new File(subFolder, clazz.getName());
    createdFolder.delete();
    createdFolder.mkdir();
    return createdFolder;
  }

  private File createCommonSubFolder(File parentFolder) {
    File subFolder = new File(parentFolder, SUBFODLER);
    subFolder.delete();
    subFolder.mkdir();
    return subFolder;
  }

  /**
   * @return the location of this temporary folder.
   */
  public File getRoot() {
    if (folder == null) {
      throw new IllegalStateException(
          "the temporary folder has not yet been created");
    }
    return folder;
  }

  /**
   * Delete all files and folders under the temporary folder. Usually not
   * called directly, since it is automatically applied by the {@link Rule}
   */
  public void delete() {
    if (subFolder != null) {
      try {
        FileUtils.forceDelete(subFolder);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
