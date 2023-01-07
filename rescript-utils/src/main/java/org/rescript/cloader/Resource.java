package org.rescript.cloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.rescript.ScriptException;

record Resource(
    String name,
    File jar) {

  public URL toURL() {
    try {
      return new URL("jar:file:"+jar.getAbsolutePath()+"!/"+name);
    } catch (MalformedURLException e) {
      throw new ScriptException(e);
    }
  }
}
