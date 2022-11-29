//package org.rescript.dependency;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.rescript.ScriptException;
//
//public class DependencyTree {
//
//  private DependencyNode root = new DependencyNode();
//
//  private Set<File> unmanaged = new HashSet<>();
//
//  
//
//  public void addUnmanaged(File file) {
//    try {
//      unmanaged.add(file.getCanonicalFile());
//    } catch (IOException e) {
//      throw new ScriptException("failed to add unmanaged file " + file.getAbsolutePath());
//    }
//  }
//
//  public void addAllUnmanaged(Collection<File> files) {
//    files.forEach(this::addUnmanaged);
//  }
//
//}
