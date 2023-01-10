package org.rescript;

public interface ScriptEngine {
  void setDefaultMavenRepository(String url);
  void setScriptClassLoader(ClassLoader scriptClassLoader);
  void setShowErrorsOnConsole(boolean showErrorsOnConsole);
  void setArguments(String[] arguments);
  Object run(String script);
}
