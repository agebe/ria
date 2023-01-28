package org.ria;

import java.io.File;

public interface ScriptEngine {
  ScriptEngine setDefaultMavenRepository(String url);
  ScriptEngine setScriptClassLoader(ClassLoader scriptClassLoader);
  ScriptEngine setShowErrorsOnConsole(boolean showErrorsOnConsole);
  ScriptEngine setArguments(String[] arguments);
  ScriptEngine setHome(File home);
  ScriptEngine setDownloadDependenciesOnly(boolean downloadDependenciesOnly);
  ScriptEngine setDisplayInfo(boolean enabled);
  ScriptEngine setQuiet(boolean quiet);
  Object run(String script);
}
