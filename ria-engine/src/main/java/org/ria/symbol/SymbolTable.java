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
package org.ria.symbol;

import java.util.List;

import org.ria.run.ScriptContext;
import org.ria.statement.Function;
import org.ria.symbol.java.JavaSymbols;
import org.ria.symbol.java.RUtils;
import org.ria.symbol.script.ScriptSymbols;
import org.ria.value.FunctionValue;
import org.ria.value.SymbolValue;
import org.ria.value.Value;

public class SymbolTable {

  private ScriptSymbols scriptSymbols;

  private JavaSymbols javaSymbols;

  public SymbolTable() {
    this(new ScriptSymbols(), new JavaSymbols());
  }

  public SymbolTable(ScriptSymbols scriptSymbols, JavaSymbols javaSymbols) {
    super();
    this.scriptSymbols = scriptSymbols;
    this.javaSymbols = javaSymbols;
  }

  public ScriptSymbols getScriptSymbols() {
    return scriptSymbols;
  }

  public JavaSymbols getJavaSymbols() {
    return javaSymbols;
  }

  public Value resolveVarOrTypeOrStaticMember(String ident, ScriptContext ctx) {
    List<String> split = RUtils.splitTypeName(ident);
    String s0 = split.get(0);
    VarSymbol var = scriptSymbols.resolveVar(s0);
    if(var != null) {
      return valOrException(ident, javaSymbols.resolveRemaining(
          split.stream().skip(1).toList(),
          new SymbolValue(var),
          ctx));
    }
    List<Function> functions = scriptSymbols.findFunctions(ident);
    if(!functions.isEmpty()) {
      return new FunctionValue(functions);
    }
    return valOrException(ident, javaSymbols.resolveTypeOrStaticMember(ident, ctx));
  }

  private Value valOrException(String ident, Value v) {
    if(v != null) {
      return v;
    } else {
      throw new SymbolNotFoundException("'%s' could not be resolved".formatted(ident));
    }
  }

}
