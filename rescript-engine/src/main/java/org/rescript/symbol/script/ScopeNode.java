package org.rescript.symbol.script;

import org.rescript.parser.Type;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.Value;

public interface ScopeNode {

  void defineVar(String name, Value val, Type type, ScriptContext ctx);

  void assignVar(String name, Value val);

  VarSymbol getVarSymbol(String name);

  VarSymbol getFunctionSymbol(String name);

  VarSymbol unset(String name);

  ScopeNode getParent();

}
