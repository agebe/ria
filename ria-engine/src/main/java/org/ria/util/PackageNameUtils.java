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

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class PackageNameUtils {

  private static final Set<String> RESERVED = Set.of(
      "abstract", "assert", "boolean", "break", "byte",
      "case", "catch", "char", "class", "const", "continue", "default", "do",
      "double", "else", "enum", "extends", "false", "final", "finally",
      "float", "for", "if", "goto", "implements", "import", "instanceof",
      "int", "interface", "long", "native", "new", "null", "package",
      "private", "protected", "public", "record", "return", "short", "static",
      "strictfp", "super", "switch", "synchronized", "this", "throw",
      "throws", "transient", "true", "try", "void", "volatile", "while");

  public static boolean isPackageNameValid(String name) {
    if(StringUtils.isBlank(name)) {
      return false;
    }
    for(String s : StringUtils.split(name, '.')) {
      if(RESERVED.contains(s)) {
        return false;
      }
      for(int i=0;i<s.length();i++) {
        if(i == 0) {
          if(!Character.isJavaIdentifierStart(s.codePointAt(i))) {
            return false;
          }
        } else {
          if(!Character.isJavaIdentifierPart(s.codePointAt(i))) {
            return false;
          }
        }
      }
    }
    return true;
  }

}
