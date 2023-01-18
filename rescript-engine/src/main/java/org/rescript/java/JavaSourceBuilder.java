package org.rescript.java;

import org.rescript.run.ScriptContext;

public interface JavaSourceBuilder {
  JavaSource create(ScriptContext ctx);
}
