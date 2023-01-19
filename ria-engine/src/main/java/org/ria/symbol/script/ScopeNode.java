package org.ria.symbol.script;

import org.ria.parser.Type;
import org.ria.run.ScriptContext;
import org.ria.symbol.VarSymbol;
import org.ria.value.Value;

public interface ScopeNode {

  void defineVar(String name, Value val, Type type, ScriptContext ctx);

  void assignVar(String name, Value val);

  VarSymbol getVarSymbol(String name);

  VarSymbol getFunctionSymbol(String name);

  VarSymbol unset(String name);

  ScopeNode getParent();

}
