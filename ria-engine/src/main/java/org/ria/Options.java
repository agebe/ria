/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Options {

  /**
   * flag to indicate if the default imports should be applied to the script. If true the default imports are added
   * after the script import statements have been processed (on header exit) so the script imports take precedence.
   * This flag is true by default
   */
  public boolean defaultImportsEnabled = true;

  /**
   * modifiable list of default imports.
   */
  // used a Deque here so items can be added on both ends
  public Deque<String> defaultImports = new ArrayDeque<>(List.of(
      "java.io.*",
      "java.net.*",
      "java.nio.*",
      "java.nio.channels.*",
      "java.nio.charset.*",
      "java.nio.file.*",
      "java.nio.file.attribute.*",
      "java.math.*",
      "java.text.*",
      "java.time.*",
      "java.time.chrono.*",
      "java.time.format.*",
      "java.time.temporal.*",
      "java.time.zone.*",
      "java.util.*",
      "java.util.concurrent.*",
      "java.util.concurrent.atomic.*",
      "java.util.concurrent.locks.*",
      "java.util.function.*",
      "java.util.regex.*",
      "java.util.stream.*"
      ));

  public Deque<String> defaultStaticImports = new ArrayDeque<>();

  public boolean importDependencies = true;

  /**
   * Filters out all packages matching any of the regular expressions in the set.
   * Only relevant if the <code>importDependencies</code> flag is <code>true</code>
   * It is applied when iterating over the jar file entries as paths (so '/' instead of '.' separators)
   * and with classes included
   */
  public Set<String> importDependenciesFilter = new LinkedHashSet<>(
      // the META-INF.* filter is not really required as the package also get filtered out because
      // is it not a valid java package name but leave it in anyway as an example.
      List.of(
          "META-INF.*",
          "module-info\\.class"
          ));

  public boolean isDefaultImportsEnabled() {
    return defaultImportsEnabled;
  }

  public void setDefaultImportsEnabled(boolean defaultImportsEnabled) {
    this.defaultImportsEnabled = defaultImportsEnabled;
  }

  public Deque<String> getDefaultImports() {
    return defaultImports;
  }

  public void setDefaultImports(Deque<String> defaultImports) {
    this.defaultImports = defaultImports;
  }

  public boolean isImportDependencies() {
    return importDependencies;
  }

  public void setImportDependencies(boolean importDependencies) {
    this.importDependencies = importDependencies;
  }

  public Set<String> getImportDependenciesFilter() {
    return importDependenciesFilter;
  }

  public void setImportDependenciesFilter(Set<String> importDependenciesFilter) {
    this.importDependenciesFilter = importDependenciesFilter;
  }

  public Deque<String> getDefaultStaticImports() {
    return defaultStaticImports;
  }

  public void setDefaultStaticImports(Deque<String> defaultStaticImports) {
    this.defaultStaticImports = defaultStaticImports;
  }

}
