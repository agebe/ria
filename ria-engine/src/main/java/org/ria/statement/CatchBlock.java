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
package org.ria.statement;

import java.util.List;

import org.ria.parser.ParseItem;
import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.value.ObjValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatchBlock implements ParseItem {

  private static final Logger log = LoggerFactory.getLogger(CatchBlock.class);

  private List<Type> exceptionsTypes;

  private String ident;

  private BlockStatement block;

  public CatchBlock(List<Type> exceptionsTypes, String ident, BlockStatement block) {
    super();
    this.exceptionsTypes = exceptionsTypes;
    this.ident = ident;
    this.block = block;
  }

  public List<Type> getExceptionsTypes() {
    return exceptionsTypes;
  }

  public String getIdent() {
    return ident;
  }

  public BlockStatement getBlock() {
    return block;
  }

  public boolean handles(ScriptContext ctx, Throwable t) {
    for(Type type : exceptionsTypes) {
      Class<?> exceptionType = type.resolve(ctx);
      // TODO null check?
      log.debug("'{}' isAssignableFrom '{}': '{}'",
          exceptionType, t.getClass(), exceptionType.isAssignableFrom(t.getClass()));
      if(exceptionType.isAssignableFrom(t.getClass())) {
        return true;
      }
    }
    return false;
  }

  public void execute(ScriptContext ctx, Throwable t) {
    try {
      ctx.getSymbols().getScriptSymbols().enterScope();
      ctx.getSymbols().getScriptSymbols().defineVar(ident, new ObjValue(t.getClass(), t));
      this.block.execute(ctx);
    } finally {
      ctx.getSymbols().getScriptSymbols().exitScope();
    }
  }

}
