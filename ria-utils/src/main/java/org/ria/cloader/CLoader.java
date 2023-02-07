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
package org.ria.cloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.ria.ScriptException;

public class CLoader extends ClassLoader implements AutoCloseable {

  static {
    ClassLoader.registerAsParallelCapable();
  }

  private List<File> files;

  private Map<String, List<Resource>> resources;

  public CLoader(String name, URL[] urls, ClassLoader parent) {
    super(name, parent);
    files = Arrays.stream(urls)
        .map(this::toFile)
        .toList();
    initResources();
  }

  private File toFile(URL url) {
    try {
      return Paths.get(url.toURI()).toFile();
    } catch (URISyntaxException e) {
      throw new ScriptException(e);
    }
  }

  private void initResources() {
    resources = files.stream()
        .flatMap(this::initResources)
        .collect(Collectors.groupingBy(Resource::name));
  }

  private boolean isZipFile(File f) {
    if(f == null) {
      return false;
    }
    try(ZipFile zipFile = new ZipFile(f)) {
      return true;
    } catch(Exception e) {
      return false;
    }
  }

  private Stream<Resource> initZipResource(File f) {
    try(ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(f)))) {
      List<Resource> resources = new ArrayList<>();
      for(;;) {
        ZipEntry e = zip.getNextEntry();
        if(e == null) {
          break;
        }
        if(e.isDirectory()) {
          continue;
        }
        resources.add(new JarResource(e.getName(), f));
//        System.out.println(e.getName());
      }
      return resources.stream();
    } catch(Exception e) {
      throw new ScriptException(e);
    }
  }

  private Stream<Resource> initResources(File f) {
    if(isZipFile(f)) {
      return initZipResource(f);
    } else {
      return Stream.of(new FileResource(f));
    }
  }

  private static String classNameToResourceName(String className) {
    // from URLClassLoader.findClass()
    return className.replace('.', '/').concat(".class");
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
//    System.out.println("findClass: "+name);
    try {
      URL url = findResource(classNameToResourceName(name));
      if(url != null) {
        byte[] b = toBytes(url);
        return defineClass(name, b, 0, b.length);
      } else {
//        System.out.println("class not found: "+name);
        throw new ClassNotFoundException("class not found " + name);
      }
    } finally {
//      System.out.println("exit findClass: "+name);
    }
  }

  private byte[] toBytes(URL url) {
    try(InputStream in = url.openStream()) {
      return in.readAllBytes();
    } catch(IOException e) {
      throw new UncheckedIOException(
          "failed to load class data from '%s'".formatted(url.toString()), e);
    }
  }

  @Override
  public void close() {
    // added close method so CLoader is compatible with URLClassLoader and
    // can be used in try-with-resources
  }

  @Override
  protected URL findResource(String name) {
    try {
      Enumeration<URL> enumeration = findResources(name);
      return enumeration.hasMoreElements()?enumeration.nextElement():null;
    } catch (IOException e) {
      throw new ScriptException(e);
    }
  }

  @Override
  protected Enumeration<URL> findResources(String name) throws IOException {
    List<Resource> l = resources.get(name);
    return Collections.enumeration(l!=null?
        l.stream()
        .map(Resource::toURL)
        .toList()
        :List.of());
  }

  public List<File> getFiles() {
    return Collections.unmodifiableList(files);
  }

}

