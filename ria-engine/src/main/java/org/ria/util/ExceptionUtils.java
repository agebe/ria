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

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.ria.CheckedExceptionWrapper;
import org.ria.run.JavaMethodInvoker;
import org.ria.run.ScriptContext;

public class ExceptionUtils {

  public static void wrapCheckedAndThrow(Throwable t) {
    if(t instanceof RuntimeException r) {
      throw r;
    } else if(t instanceof Error error) {
      throw error;
    } else {
      CheckedExceptionWrapper wrapper = new CheckedExceptionWrapper(t.getMessage(), t);
      wrapper.setStackTrace(t.getStackTrace());
      throw wrapper;
    }
  }

  public static <T extends Throwable> T replaceStackTrace(T t, ScriptContext ctx) {
    t.setStackTrace(ctx.getStackTrace());
    return t;
  }

  public static <T extends Throwable> T fixStackTrace(T t, ScriptContext ctx) {
    int indexOfCaller = indexOfCaller(t);
    if(indexOfCaller == -1) {
      // in this case the exception seems to have been created on the script side
      t.setStackTrace(ctx.getStackTrace());
    } else {
      // in this case the exception was thrown while calling a java method.
      // preserve the java side stack trace until we reach the script...
      ArrayList<StackTraceElement> l = new ArrayList<>();
      for(int i=0;i<indexOfCaller;i++) {
        l.add(t.getStackTrace()[i]);
      }
      StackTraceElement[] scriptStackTrace = ctx.getStackTrace();
      for(int i=0;i<scriptStackTrace.length;i++) {
        l.add(scriptStackTrace[i]);
      }
      // we could also fix the exception cause chain but for debugging the
      // script system itself it is better to leave the causing exceptions as is.
      t.setStackTrace(l.toArray(StackTraceElement[]::new));
    }
    return t;
  }

  private static int indexOfCaller(Throwable t) {
    String caller = JavaMethodInvoker.class.getName();
    for(int i=0;i<t.getStackTrace().length;i++) {
      StackTraceElement e = t.getStackTrace()[i];
      if(StringUtils.equals(caller, e.getClassName())) {
        return i;
      }
    }
    return -1;
  }

}
