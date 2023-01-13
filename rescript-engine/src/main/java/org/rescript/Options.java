package org.rescript;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

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

  public boolean importDependencies = true;

}
