package org.rescript;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.util.JImmutables;

public class ScriptBuilder {

  private JImmutableList<String> header = JImmutables.list();

  private String script;

  public ScriptBuilder() {
    super();
  }

  private ScriptBuilder(
      JImmutableList<String> header,
      String script) {
    super();
    this.header = header;
    this.script = script;
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
    return new ScriptBuilder(header, script);
  }

  public ScriptBuilder addImport(String imp) {
    return new ScriptBuilder(
        header.insert("import " + imp + ";"),
        script);
  }

  public ScriptBuilder addStaticImport(String imp) {
    return new ScriptBuilder(
        header.insert("import static " + imp + ";"),
        script);
  }

  public ScriptBuilder addFunctionAlias(String alias, String target) {
    throw new ScriptException("not implemented");
  }

  public Script create() {
    String header = this.header.stream().collect(Collectors.joining());
    String s = header + (StringUtils.isNotBlank(script)?script:"");
    return new Script(s);
  }

}
