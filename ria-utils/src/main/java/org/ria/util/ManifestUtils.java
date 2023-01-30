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
package org.ria.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import org.ria.ScriptException;

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

  public static String gitVersion(ClassLoader loader, String title) {
    Manifest manifest = manifest(loader, title);
    String version = manifest.getMainAttributes().getValue("git-version");
    if(version != null) {
      return version;
    } else {
      throw new ScriptException("could not determine git version for '%s'".formatted(title));
    }
  }

}
