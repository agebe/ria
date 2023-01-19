package org.ria.java;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

public class CfClassLoader extends ClassLoader implements AutoCloseable {

  static {
    ClassLoader.registerAsParallelCapable();
  }

  private List<ClassFile> files;

  public CfClassLoader(List<ClassFile> files, ClassLoader parent) {
    super(parent);
    this.files = files;
  }

  private static String classNameToResourceName(String className) {
    // from URLClassLoader.findClass()
    return className.replace('.', '/').concat(".class");
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    String resource = "/" + classNameToResourceName(name);
//    log.info("resource name '{}'", resource);
    List<ClassFile> l = files
        .stream()
//        .peek(cfo -> log.info("cfo '{}'", cfo.toUri().getPath()))
        .filter(cfo -> cfo.toUri().getPath().equals(resource))
        .toList();
    if(!l.isEmpty()) {
      byte[] b = l.get(0).getBytes();
      return defineClass(name, b, 0, b.length);
    } else {
      throw new ClassNotFoundException(name);
    }
////    System.out.println("findClass: "+name);
//    try {
//      URL url = findResource(classNameToResourceName(name));
//      if(url != null) {
//        byte[] b = toBytes(url);
//        return defineClass(name, b, 0, b.length);
//      } else {
////        System.out.println("class not found: "+name);
//        throw new ClassNotFoundException("class not found " + name);
//      }
//    } finally {
////      System.out.println("exit findClass: "+name);
//    }
  }

//  private byte[] toBytes(URL url) {
//    try(InputStream in = url.openStream()) {
//      return in.readAllBytes();
//    } catch(IOException e) {
//      throw new UncheckedIOException(
//          "failed to load class data from '%s'".formatted(url.toString()), e);
//    }
//  }

  @Override
  public void close() {
    // added close method to be compatible with URLClassLoader
  }

  @Override
  protected URL findResource(String name) {
    return super.findResource(name);
//    try {
//      Enumeration<URL> enumeration = findResources(name);
//      return enumeration.hasMoreElements()?enumeration.nextElement():null;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
  }

  @Override
  protected Enumeration<URL> findResources(String name) throws IOException {
    return super.findResources(name);
//    List<Resource> l = resources.get(name);
//    return Collections.enumeration(l!=null?
//        l.stream()
//        .map(Resource::toURL)
//        .toList()
//        :List.of());
  }

}
