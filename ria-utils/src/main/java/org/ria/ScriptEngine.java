package org.ria;

import java.io.File;

public interface ScriptEngine {
  void setDefaultMavenRepository(String url);
  void setScriptClassLoader(ClassLoader scriptClassLoader);
  void setShowErrorsOnConsole(boolean showErrorsOnConsole);
  void setArguments(String[] arguments);
  Object run(String script);
  void setHome(File home);
  void setDownloadDependenciesOnly(boolean downloadDependenciesOnly);
  void setDisplayInfo(boolean enabled);
  ScriptEngine setQuiet(boolean quiet);
}
