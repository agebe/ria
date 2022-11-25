package org.rescript.dependency;

import java.io.File;
import java.util.List;

public interface Dependency {
  List<File> resolve();
}
