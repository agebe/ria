package org.ria;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.ria.parser.Parser;
import org.ria.parser.ParserListener;
import org.ria.run.ScriptRunner;
import org.ria.statement.Function;
import org.ria.symbol.SymbolTable;
import org.ria.symbol.VarSymbol;
import org.ria.util.ManifestUtils;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Script implements ScriptEngine {

  private static final Logger log = LoggerFactory.getLogger(Script.class);

  private SymbolTable symbols;

  private String script;

  private Function entry;

  private boolean showErrorsOnConsole;

  private String[] arguments;

  // the class loader to load types used by the script
  private ClassLoader scriptClassLoader = this.getClass().getClassLoader();

  private String defaultMavenRepo;

  private File home;

  private boolean downloadDependenciesOnly;

  private boolean displayInfo;

  private boolean quiet;

  public Script() {
    this(null, null);
  }

  public Script(String script) {
    this(script, null);
  }

  public Script(String script, SymbolTable symbols) {
    super();
    this.script = script;
    this.symbols = symbols!=null?symbols:new SymbolTable();
  }

  private Value runVal() {
    // remember the context class loader. It is set in HeaderExitStatement, after the dependencies have been resolved
    // and the java types have been compiled.
    ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
    try {
      parse(script);
      setupArguments();
      return new ScriptRunner(symbols).run();
    } finally {
      Thread.currentThread().setContextClassLoader(ctxLoader);
    }
  }

  private void setupArguments() {
    if(this.arguments != null) {
      for(int i=0;i<arguments.length;i++) {
        this.setVariable("$"+i, arguments[i]);
      }
      this.setVariable("$", arguments);
    } else {
      this.setVariable("$", new String[0]);
    }
  }

  public Object run() {
    Value v = runVal();
    return v!=null?v.val():null;
  }

  public Object runUnwrapException() throws Throwable {
    try {
      return run();
    } catch(CheckedExceptionWrapper e) {
      throw e.getCause();
    }
  }

  @Override
  public Object run(String script) {
    return reparse(script).run();
  }

  public Object runUnwrapException(String script) throws Throwable {
    try {
      return run(script);
    } catch(CheckedExceptionWrapper e) {
      throw e.getCause();
    }
  }

  public <T> T runReturning(Class<T> type) {
    Value val = runVal();
    return val.isNull()?null:type.cast(val.val());
  }

  public <T> T runReturning(String script, Class<T> type) {
    return reparse(script).runReturning(type);
  }

  // TODO also add support for generic types, like e.g. List<String>

  public boolean evalPredicate() {
    return runVal().toBoolean();
  }

  public boolean evalPredicate(String script) {
    return reparse(script).evalPredicate();
  }

  public double evalDouble() {
    return runVal().toDouble();
  }

  public double evalDouble(String script) {
    return reparse(script).evalDouble();
  }

  public float evalFloat() {
    return runVal().toFloat();
  }

  public float evalFloat(String script) {
    return reparse(script).evalFloat();
  }

  public long evalLong() {
    return runVal().toLong();
  }

  public long evalLong(String script) {
    return reparse(script).evalLong();
  }

  public int evalInt() {
    return runVal().toInt();
  }

  public int evalInt(String script) {
    return reparse(script).evalInt();
  }

  public char evalChar() {
    return runVal().toChar();
  }

  public char evalChar(String script) {
    return reparse(script).evalChar();
  }

  public short evalShort() {
    return runVal().toShort();
  }

  public short evalShort(String script) {
    return reparse(script).evalShort();
  }

  public byte evalByte() {
    return runVal().toByte();
  }

  public byte evalByte(String script) {
    return reparse(script).evalByte();
  }

  public void setVariable(String name, Object val) {
    symbols.getScriptSymbols().defineOrAssignVarRoot(name, Value.of(val));
  }

  public Object getVariable(String name) {
    Value val = symbols.getScriptSymbols().getVariable(name);
    return val!=null?val.val():null;
  }

  public Object unsetVariable(String name) {
    VarSymbol s = symbols.getScriptSymbols().unsetRoot(name);
    return s!=null?s.getObjectOrNull():null;
  }

  private Script reparse(String script) {
    this.entry = null;
    return parse(script);
  }

  private String getVersion() {
    try {
      // try to read the version from the manifest first
      return ManifestUtils.version(this.getClass().getClassLoader(), "ria-engine");
    } catch(Exception e) {
      try {
        // if we are running from inside eclipse (e.g. run junit tests) the version might be here:
        return StringUtils.strip(FileUtils.readFileToString(new File("../version"), StandardCharsets.UTF_8));
      } catch(Exception e2) {
        throw new ScriptException("failed to determine script engine version");
      }
    }
  }

  private File getCacheBase() {
    File home = getHome()!=null?getHome():new File(new File(System.getProperty("user.home")), ".ria");
    File versionBase = new File(home, getVersion());
    File cacheBase = new File(versionBase, "cache");
    if(!cacheBase.exists()) {
      cacheBase.mkdirs();
    }
    if(!(cacheBase.exists() && cacheBase.isDirectory())) {
      throw new ScriptException("failed to create cache directory '%s'".formatted(cacheBase));
    }
    log.debug("cache base '{}'", cacheBase.getAbsolutePath());
    return cacheBase;
  }

  private Script parse(String script) {
    if(this.entry == null) {
      ParserListener listener = new Parser(showErrorsOnConsole, defaultMavenRepo)
          .parse(script, scriptClassLoader, getCacheBase(), downloadDependenciesOnly, displayInfo, quiet);
      this.entry = listener.getMainFunction();
      this.symbols.getScriptSymbols().setMain(entry);
    }
    return this;
  }

  public boolean isShowErrorsOnConsole() {
    return showErrorsOnConsole;
  }

  @Override
  public void setShowErrorsOnConsole(boolean showErrorsOnConsole) {
    this.showErrorsOnConsole = showErrorsOnConsole;
  }

  public String[] getArguments() {
    return arguments;
  }

  @Override
  public void setArguments(String[] arguments) {
    this.arguments = arguments;
  }

  public ClassLoader getScriptClassLoader() {
    return scriptClassLoader;
  }

  @Override
  public void setScriptClassLoader(ClassLoader scriptClassLoader) {
    this.scriptClassLoader = scriptClassLoader;
  }

  @Override
  public void setDefaultMavenRepository(String url) {
    this.defaultMavenRepo = url;
  }

  public File getHome() {
    return home;
  }

  @Override
  public void setHome(File home) {
    this.home = home;
  }

  @Override
  public void setDownloadDependenciesOnly(boolean downloadDependenciesOnly) {
    this.downloadDependenciesOnly = downloadDependenciesOnly;
  }

  @Override
  public void setDisplayInfo(boolean enabled) {
    this.displayInfo = enabled;
  }

  @Override
  public void setQuiet(boolean quiet) {
    this.quiet = quiet;
  }

}
