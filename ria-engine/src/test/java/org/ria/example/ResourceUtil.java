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
package org.ria.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.ria.ScriptException;

public class ResourceUtil {

  public static String resourceAsString(String name) {
    try {
      return IOUtils.toString(ResourceUtil.class.getResourceAsStream(name), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new ScriptException("failed to load resource '%s'".formatted(name), e);
    }
  }

}
