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
package org.ria.launcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CliOptions {

  public boolean version;

  public boolean downloadDependenciesOnly;

  public boolean trace;

  public boolean debug;

  public boolean info;

  public boolean quiet;

  public boolean help;

  public String mavenRepo;

  public List<File> classpath = new ArrayList<>();

  public Path scriptEngineHome;

  public Path scriptFile;

  public String[] scriptArgs;

  private String pathSeparator() {
    return System.getProperty("path.separator");
  }

  private List<File> paths(String s) {
    return Arrays.stream(s.split(pathSeparator()))
        .map(File::new)
        .peek(f -> {
          if(!f.exists()) {
            System.out.println("WARN: classpath item '%s' does not exist".formatted(f.getAbsolutePath()));
          }
        })
        .toList();
  }

  public CliOptions(String[] args) {
    scriptEngineHome = Paths.get(args[0]);
    for(int i=1;i<args.length;i++) {
      String s = args[i];
      if(equalsAny(s, "--version", "-v")) {
        version = true;
      } else if(equalsAny(s, "--download-dependencies-only", "-d")) {
        downloadDependenciesOnly = true;
      } else if(equalsAny(s, "--maven-repository", "-r")) {
        i++;
        mavenRepo = args[i];
      } else if(equalsAny(s, "--classpath", "-cp")) {
        i++;
        classpath.addAll(paths(args[i]));
      } else if(equalsAny(s, "--trace")) {
        trace = true;
      } else if(equalsAny(s, "--debug")) {
        debug = true;
      } else if(equalsAny(s, "--info")) {
        info = true;
      } else if(equalsAny(s, "--quiet", "-q")) {
        quiet = true;
      } else if(equalsAny(s, "--home")) {
        // ignore, this is passed as argument 0 from the native launcher
        i++;
      } else if(equalsAny(s, "--help", "-h")) {
        help = true;
      } else {
        scriptFile = Paths.get(args[i]);
        int remainingArgs = args.length - (i+1);
        if(remainingArgs > 0) {
          scriptArgs = new String[remainingArgs];
          System.arraycopy(args, i+1, scriptArgs, 0, remainingArgs);
        } else {
          scriptArgs = new String[0];
        }
        break;
      }
    }
  }

  public void printHelp() {
    System.out.println("""
        Usage: ria [ options ... ] script-file script-arguments
        where options include:
          --version, -v                     display version
          --download-dependencies-only, -d  download missing dependencies into the cache and exit
          --maven-repository, -r            the default maven repo to use if not specified in the script
          --home                            set script engine home, e.g. maven download cache ('%s')
          --classpath, -cp                  add to script classpath in addition to %s/classpath and script dependencies.
                                            can be a e.g. a single jar file or directory
                                            containing jars or classes or other resources.
                                            specify multiple times or separate paths with '%s'
          --trace                           display trace output
          --debug                           display debug output
          --info                            display info output on std error
          --quiet, -q                       display less output on std error
          --help, -h                        display this help and exit
        """.formatted(scriptEngineHome, scriptEngineHome, pathSeparator()));
  }

  private boolean equalsAny(String s, String... others) {
    for(String o : others) {
      if(s.equals(o)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "CliOptions [version=" + version + ", downloadDependenciesOnly=" + downloadDependenciesOnly + ", debug="
        + debug + ", info=" + info + ", quiet=" + quiet + ", help=" + help + ", mavenRepo=" + mavenRepo
        + ", scriptEngineHome=" + scriptEngineHome + ", scriptFile=" + scriptFile + ", scriptArgs="
        + Arrays.toString(scriptArgs) + "]";
  }

}
