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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.ria.ScriptException;

public record FileResource(File file, File base) implements Resource {

  @Override
  public URL toURL() {
    try {
      return file.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new ScriptException("failed to convert file path '%s' to url".formatted(file.getAbsolutePath()), e);
    }
  }

  @Override
  public String name() {
    if(base == null) {
      return file.getName();
    } else {
      return base.toPath().relativize(file.toPath()).toString();
    }
  }

}
