package org.ria.dependency;

import java.util.List;

public interface Dependency {
  List<DependencyNode> resolve();
}
