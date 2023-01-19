package org.rescript;

import java.io.File;

public interface ScriptEngine {
  void setDefaultMavenRepository(String url);
  void setScriptClassLoader(ClassLoader scriptClassLoader);
  void setShowErrorsOnConsole(boolean showErrorsOnConsole);
  void setArguments(String[] arguments);
  Object run(String script);
  void setRescriptHome(File rescriptHome);
  void setDownloadDependenciesOnly(boolean downloadDependenciesOnly);
  void setDisplayInfo(boolean enabled);
  void setQuiet(boolean quiet);
}
