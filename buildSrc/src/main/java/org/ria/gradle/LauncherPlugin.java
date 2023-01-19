//package org.ria.gradle;
//
//import java.util.Map;
//
//import org.gradle.api.Plugin;
//import org.gradle.api.Project;
//import org.gradle.api.Task;
//import org.gradle.api.Action;
//
//public class LauncherPlugin implements Plugin<Project> {
//
//  private static Action action = t -> System.out.println("foo task, " + t);
//
//  @Override
//  public void apply(Project project) {
//    System.out.println("Hello LauncherPlugin");
//    Task foo = project.task(
//        Map.<String, Object>of(
//            "group","launcher",
//            "description", "build the launcher",
//            "actrion", action
//            ), "foo");
//    
//  }
//
//  public static void sayHello() {
//    System.out.println("Hello from buildSrc!");
//  }
//
//}
//
