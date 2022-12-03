package org.rescript;

import org.junit.jupiter.api.Test;

public class DependencyTest {

  @Test
  public void simple() {
    new Script("""
        dependencies {
          '''
          <dependency>
            <groupId>io.github.agebe</groupId>
            <artifactId>kvd-client</artifactId>
            <version>0.6.2</version>
            <exclusions>
              <exclusion>
                <groupId>org.slf4j</groupId>
                <artifactId>slf4j-api</artifactId>
              </exclusion>
            </exclusions>
          </dependency>
          <!-- commons lang not really required, just testing pom import -->
          <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
          </dependency>
          '''
          //'io.github.agebe:kvd-client:0.6.2'
        }
        import kvd.client.KvdClientBuilder;
        var builder = new KvdClientBuilder();
        println(typeof builder);
        println(builder);
        """).run();
  }

}

/*
FIXME comment out dependency and fix the exception

org.rescript.ScriptException: script execution failed
  at org.rescript.Script.runVal(Script.java:44)
  at org.rescript.Script.run(Script.java:62)
  at org.rescript.DependencyTest.simple(DependencyTest.java:17)
  at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
  at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
  at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
  at java.base/java.lang.reflect.Method.invoke(Method.java:568)
  at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:727)
  at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
  at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
  at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:156)
  at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestableMethod(TimeoutExtension.java:147)
  at org.junit.jupiter.engine.extension.TimeoutExtension.interceptTestMethod(TimeoutExtension.java:86)
  at org.junit.jupiter.engine.execution.InterceptingExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(InterceptingExecutableInvoker.java:103)
  at org.junit.jupiter.engine.execution.InterceptingExecutableInvoker.lambda$invoke$0(InterceptingExecutableInvoker.java:93)
  at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
  at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
  at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
  at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
  at org.junit.jupiter.engine.execution.InterceptingExecutableInvoker.invoke(InterceptingExecutableInvoker.java:92)
  at org.junit.jupiter.engine.execution.InterceptingExecutableInvoker.invoke(InterceptingExecutableInvoker.java:86)
  at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$7(TestMethodTestDescriptor.java:217)
  at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
  at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:213)
  at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:138)
  at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:68)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
  at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
  at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
  at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
  at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
  at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
  at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
  at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
  at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
  at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
  at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
  at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
  at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
  at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
  at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
  at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
  at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
  at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
  at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:107)
  at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:88)
  at org.junit.platform.launcher.core.EngineExecutionOrchestrator.lambda$execute$0(EngineExecutionOrchestrator.java:54)
  at org.junit.platform.launcher.core.EngineExecutionOrchestrator.withInterceptedStreams(EngineExecutionOrchestrator.java:67)
  at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:52)
  at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:114)
  at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:95)
  at org.junit.platform.launcher.core.DefaultLauncherSession$DelegatingLauncher.execute(DefaultLauncherSession.java:91)
  at org.junit.platform.launcher.core.SessionPerRequestLauncher.execute(SessionPerRequestLauncher.java:60)
  at org.eclipse.jdt.internal.junit5.runner.JUnit5TestReference.run(JUnit5TestReference.java:98)
  at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:40)
  at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:529)
  at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:756)
  at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:452)
  at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:210)
Caused by: java.lang.NullPointerException: Cannot invoke "java.lang.Class.getName()" because "c" is null
  at org.rescript.symbol.java.JavaSymbols.resolveTypeInternal(JavaSymbols.java:345)
  at org.rescript.symbol.java.JavaSymbols.resolveType(JavaSymbols.java:214)
  at org.rescript.run.JavaConstructorCaller.call(JavaConstructorCaller.java:27)
  at org.rescript.expression.NewOp.eval(NewOp.java:24)
  at org.rescript.expression.AssignmentOp.eval(AssignmentOp.java:22)
  at org.rescript.statement.VarDef.execute(VarDef.java:44)
  at org.rescript.statement.VardefStatement.lambda$0(VardefStatement.java:21)
  at java.base/java.lang.Iterable.forEach(Iterable.java:75)
  at org.rescript.statement.VardefStatement.execute(VardefStatement.java:21)
  at org.rescript.statement.BlockStatement.execute(BlockStatement.java:48)
  at org.rescript.statement.Function.executeFunction(Function.java:68)
  at org.rescript.run.ScriptRunner.run(ScriptRunner.java:17)
  at org.rescript.Script.runVal(Script.java:42)
  ... 71 more

*/
