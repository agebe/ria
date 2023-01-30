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
package org.ria.java;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import org.ria.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileManager extends ForwardingJavaFileManager<JavaFileManager> {

  private static final Logger log = LoggerFactory.getLogger(FileManager.class);

  private List<ClassFile> files = new ArrayList<>();

  public FileManager(JavaFileManager wrapped) {
    super(wrapped);
  }

  @Override
  public int isSupportedOption(String option) {
    int s = super.isSupportedOption(option);
    log.trace("isSupportedOption '{}', result '{}'", option, s);
    return s;
  }

  @Override
  public ClassLoader getClassLoader(Location location) {
    ClassLoader c = super.getClassLoader(location);
    log.trace("getClassLoader location '{}', result '{}'", location, c);
    return c;
  }

  @Override
  public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
      throws IOException {
    Iterable<JavaFileObject> i = super.list(location, packageName, kinds, recurse);
    log.trace("list location, '{}', package '{}', kinds '{}', recurse '{}', result '{}'",
        location, packageName, kinds, recurse, i);
    return i;
  }

  @Override
  public String inferBinaryName(Location location, JavaFileObject file) {
    String s = super.inferBinaryName(location, file);
    log.trace("inferBinaryName location '{}', file '{}', result '{}'", location, file, s);
    return s;
  }

  @Override
  public boolean isSameFile(FileObject a, FileObject b) {
    boolean same = super.isSameFile(a, b);
    log.trace("isSameFile file '{}', file '{}', result '{}'", a, b, same);
    return same;
  }

  @Override
  public boolean handleOption(String current, Iterator<String> remaining) {
    boolean b = super.handleOption(current, remaining);
    log.trace("handleOption current '{}', remaining '{}', result '{}'", current, remaining, b);
    return b;
  }

  @Override
  public boolean hasLocation(Location location) {
    boolean b = super.hasLocation(location);
    log.trace("hasLocation location '{}', result '{}'", location, b);
    return b;
  }

  @Override
  public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
    JavaFileObject o = super.getJavaFileForInput(location, className, kind);
    log.trace("getJavaFileForInput location '{}', className '{}', kind '{}', result '{}'",
        location, className, kind, o);
    return o;
  }

  private static String classNameToResourceName(String className) {
    // from URLClassLoader.findClass()
    return className.replace('.', '/').concat(".class");
  }

  @Override
  public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
      throws IOException {
    String u = "class:/" + classNameToResourceName(className);
    try {
      ClassFile f = new ClassFile(new URI(u), kind);
      files.add(f);
      //    JavaFileObject o = super.getJavaFileForOutput(location, className, kind, sibling);
      log.trace("getJavaFileForOutput location '{}', className '{}', kind '{}', sibling '{}', result '{}'",
          location, className, kind, sibling, f);
      return f;
    } catch (Exception e) {
      throw new ScriptException("failed on getJavaFileForOutput with location %s, className %s, kind %s, sibling %s"
          .formatted(location, className, kind, sibling), e);
    }
  }

  @Override
  public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
    FileObject o = super.getFileForInput(location, packageName, relativeName);
    log.trace("getFileForInput location '{}', packageName '{}', relativeName '{}', result '{}'",
        location, packageName, relativeName, o);
    return o;
  }

  @Override
  public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling)
      throws IOException {
    throw new UnsupportedOperationException("getFileForOutput not supported");
//    FileObject o = super.getFileForOutput(location, packageName, relativeName, sibling);
//    log.trace("getFileForOutput location '{}', packageName '{}', relativeName '{}', sibling {}', result '{}'",
//        location, packageName, relativeName, sibling, o);
//    return o;
  }

  @Override
  public void flush() throws IOException {
    log.trace("flush");
    super.flush();
  }

  @Override
  public void close() throws IOException {
    log.trace("close");
    super.close();
  }

  public List<ClassFile> getFiles() {
    return files;
  }

}
