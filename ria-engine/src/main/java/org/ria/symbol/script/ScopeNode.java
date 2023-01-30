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
package org.ria.symbol.script;

import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.symbol.VarSymbol;
import org.ria.value.Value;

public interface ScopeNode {

  void defineVar(String name, Value val, Type type, ScriptContext ctx);

  void defineVarUninitialized(String name, Type type, ScriptContext ctx);

  void assignVar(String name, Value val);

  VarSymbol getVarSymbol(String name);

  VarSymbol getFunctionSymbol(String name);

  VarSymbol unset(String name);

  ScopeNode getParent();

}
