package org.rescript;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.util.JImmutables;
import org.rescript.symbol.SymbolTable;

public class ScriptBuilder {

  private String script;

  private JImmutableList<String> imports = JImmutables.list();

  private JImmutableList<String> staticImports = JImmutables.list();

  public ScriptBuilder() {
    super();
  }

  private ScriptBuilder(
      String script,
      JImmutableList<String> imports,
      JImmutableList<String> staticImports) {
    super();
    this.script = script;
    this.imports = imports;
    this.staticImports = staticImports;
  }

  // TODO set variables
  // TODO import/allow function, make it so that e.g. StringUtils.isBlank can be called as if it were statically imported
  // e.g. isBlank("")

  // TODO allow/restrict var def
  // TODO allow/restrict function calls (e.g. only allow operators)

  // on every method call return a new RestrictedScriptBuilder, keep it immutable.
  // this way can reuse the already parsed script and e.g. imported function
  // and only have to e.g. setup variables again for a subsequent run

//  public String getScript() {
//    return script;
//  }

  public ScriptBuilder setScript(String script) {
    return new ScriptBuilder(script, imports, staticImports);
  }

  public ScriptBuilder addImport(String imp) {
    return new ScriptBuilder(
        script,
        imports.insert(imp),
        staticImports);
  }

  public ScriptBuilder addStaticImport(String imp) {
    return new ScriptBuilder(
        script,
        imports,
        staticImports.insert(imp));
  }

  public Script create() {
    SymbolTable symbols = new SymbolTable();
    imports.forEach(symbols.getJavaSymbols()::addImport);
    staticImports.forEach(symbols.getJavaSymbols()::addStaticImport);
    return new Script(script, symbols);
  }

}
