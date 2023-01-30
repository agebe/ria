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
package org.ria;

public class Features {

  public boolean javaSourceEnabled = true;

  public boolean dependenciesEnabled = true;

  public boolean loopsEnabled = true;

  public boolean isJavaSourceEnabled() {
    return javaSourceEnabled;
  }

  public Features setJavaSourceEnabled(boolean javaSourceEnabled) {
    this.javaSourceEnabled = javaSourceEnabled;
    return this;
  }

  public boolean isDependenciesEnabled() {
    return dependenciesEnabled;
  }

  public Features setDependenciesEnabled(boolean dependenciesEnabled) {
    this.dependenciesEnabled = dependenciesEnabled;
    return this;
  }

  public boolean isLoopsEnabled() {
    return loopsEnabled;
  }

  public Features setLoopsEnabled(boolean loopsEnabled) {
    this.loopsEnabled = loopsEnabled;
    return this;
  }

}
