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
package org.ria.firewall;

import org.ria.ScriptException;

public class AccessDeniedException extends ScriptException {

  private static final long serialVersionUID = 7225448774273448410L;

  public AccessDeniedException() {
    super();
  }

  public AccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public AccessDeniedException(String message, Throwable cause) {
    super(message, cause);
  }

  public AccessDeniedException(String message) {
    super(message);
  }

  public AccessDeniedException(Throwable cause) {
    super(cause);
  }

}
