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
package org.ria.run;

import java.util.ArrayDeque;
import java.util.Deque;

import org.ria.ScriptException;
import org.ria.statement.Breakable;
import org.ria.statement.Continueable;
import org.ria.statement.Function;

public class Stackframe {

  private Function function;

  private int line;

  private Deque<Breakable> breakStack = new ArrayDeque<>();

  private Deque<Continueable> continueStack = new ArrayDeque<>();

  public Stackframe(Function function) {
    super();
    this.function = function;
  }

  public Function getFunction() {
    return function;
  }

  public void pushBreakable(Breakable breakable) {
    breakStack.push(breakable);
  }

  public Breakable popBreakable(Breakable breakable) {
    Breakable b = breakStack.pop();
    if(breakable != null) {
      if(breakable != b) {
        throw new ScriptException("internal, wrong breakable");
      }
    }
    return b;
  }

  public Breakable peekBreakable() {
    return breakStack.peek();
  }

  public void pushContinueable(Continueable continueable) {
    continueStack.push(continueable);
  }

  public Continueable popContinueable(Continueable continueable) {
    Continueable c = continueStack.pop();
    if(continueable != null) {
      if(continueable != c) {
        throw new ScriptException("internal, wrong continueable");
      }
    }
    return c;
  }

  public Continueable peekContinueable() {
    return continueStack.peek();
  }

  public int getLine() {
    return line;
  }

  public void setLine(int line) {
    this.line = line;
  }

}
