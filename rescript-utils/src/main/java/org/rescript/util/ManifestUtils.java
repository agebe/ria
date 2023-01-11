package org.rescript.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import org.rescript.ScriptException;

public class ManifestUtils {

  public static Manifest manifest(ClassLoader loader, String title) {
    try {
      Enumeration<URL> manifestUrls = loader.getResources("META-INF/MANIFEST.MF");
      while(manifestUrls.hasMoreElements()) {
        URL url = manifestUrls.nextElement();
        Manifest manifest = new Manifest(url.openStream());
        if(title.equals(manifest.getMainAttributes().getValue("Implementation-Title"))) {
          return manifest;
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException("failed on loading manifest for '%s'".formatted(title), e);
    }
    throw new ScriptException("manifest not found for '%s'".formatted(title));
  }

  public static String version(ClassLoader loader, String title) {
    Manifest manifest = manifest(loader, title);
    String version = manifest.getMainAttributes().getValue("Implementation-Version");
    if(version != null) {
      return version;
    } else {
      throw new ScriptException("could not determine version for '%s'".formatted(title));
    }
  }

}
