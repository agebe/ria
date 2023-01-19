#include "launchJvm.h"
#include "launcher.h"

#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <dlfcn.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <stdbool.h>
#include <dirent.h>
#include <limits.h>

char* classpath;

void createClasspath() {
  char buf[PATH_MAX];
  char* bootDir = launcherInfo.homeBoot;
  int files = 0;
  int namesSize = 0;
  // https://stackoverflow.com/a/12506/20615256
  DIR *dp;
  struct dirent *ep;
  dp = opendir(bootDir);
  if(dp != NULL) {
    while((ep = readdir (dp)) != NULL) {
      // man readdir
      if(ep->d_type == DT_REG) {
        files++;
        namesSize += strlen(ep->d_name);
      }
    }
    (void) closedir(dp);
  } else {
    printf("failed to open dir %s\n", bootDir);
    exit(1);
  }
  char* clOption = "-Djava.class.path=";
  size_t size = sizeof(char) * ( strlen(clOption) + ( strlen(bootDir) + 1) * files + namesSize + files );
  //printf("files %i\n", files);
  //printf("names %i\n", namesSize);
  //printf("libs %zu\n", strlen(bootDir));
  //printf("%zu\n", size);
  classpath = malloc(size);
  classpath[0] = 0;
  strcat(classpath, clOption);
  dp = opendir (bootDir);
  int i = 0;
  if(dp != NULL) {
    while((ep = readdir (dp)) != NULL) {
      // man readdir
      if(ep->d_type == DT_REG) {
        //printf("%s\n", ep->d_name);
        snprintf(buf, sizeof(buf), "%s/%s", bootDir, ep->d_name);
        strcat(classpath, buf);
        if((i+1)<files) {
          strcat(classpath, ":");
        }
        i++;
      }
    }
    (void) closedir(dp);
  } else {
    printf("failed to open dir %s\n", bootDir);
    exit(1);
  }
}

struct {
  int argc;
  char** argv;
} args;

void initArgs(int argc, char **argv) {
  args.argc = argc;
  args.argv = malloc(sizeof(argv) * args.argc);
  args.argv[0] = launcherInfo.home;
  for(int i=1;i<argc;i++) {
    args.argv[i] = argv[i];
  }
}

void launchJvm(char* libjvm, int argc, char **argv) {
  createClasspath();
  initArgs(argc, argv);
  //printf("%s\n", classpath);
  //printf("classpath length %zu\n", strlen(classpath));
  JavaVMOption jvmopt[1];
  jvmopt[0].optionString = classpath;
  //jvmopt[0].optionString = foo2;
  JavaVMInitArgs vmArgs;
  vmArgs.version = JNI_VERSION_1_2;
  vmArgs.nOptions = 1;
  vmArgs.options = jvmopt;
  vmArgs.ignoreUnrecognized = JNI_TRUE;
  // Create the JVM
  JavaVM *javaVM;
  JNIEnv *env;
  void *handle;
  char *error;
  if(libjvm == NULL) {
    printf("libjvm.so not found, consider setting JAVA_HOME or BS_JAVA_HOME or BS_LIBJVM (pointing to e.g. $JAVA_HOME/lib/server/libjvm.so\n");
    exit(1);
  }
  struct stat st = {0};
  if(stat(libjvm, &st) == -1) {
    printf("'%s' file not found\n", libjvm);
    exit(1);
  }
  // loading shared object via dlopen
  //https://stackoverflow.com/a/1142169
  handle = dlopen (libjvm, RTLD_LAZY);
  if(!handle) {
    fprintf (stderr, "%s\n", dlerror());
    exit(1);
  }
  // clear any existing error
  dlerror();
  jint (*JNI_CreateJavaVM)(JavaVM **pvm, void **penv, void *args);
  JNI_CreateJavaVM = dlsym(handle, "JNI_CreateJavaVM");
  if((error = dlerror()) != NULL)  {
    fprintf (stderr, "%s\n", error);
    exit(1);
  }
  long flag = (*JNI_CreateJavaVM)(&javaVM, (void**)&env, &vmArgs);
  if (flag == JNI_ERR) {
    printf("error creating jvm. exiting...\n");
    exit(1);
  }
  jclass jcls = (*env)->FindClass(env, "org/rescript/launcher/ScriptLauncher");
  if (jcls == NULL) {
    (*env)->ExceptionDescribe(env);
    (*javaVM)->DestroyJavaVM(javaVM);
    printf("launcher main class org.rescript.launcher.ScriptLauncher not found\n");
    exit(1);
  }
  if (jcls != NULL) {
    //https://stackoverflow.com/a/9437132
    //https://docs.oracle.com/javase/1.5.0/docs/guide/jni/spec/types.html#wp276
    jmethodID methodId = (*env)->GetStaticMethodID(env, jcls, "main", "([Ljava/lang/String;)V");
    if(methodId != NULL) {
      jobjectArray array = (jobjectArray)(*env)->NewObjectArray(env, args.argc,(*env)->FindClass(env, "java/lang/String"),(*env)->NewStringUTF(env, ""));
      for(int i=0;i<args.argc;i++) {
        (*env)->SetObjectArrayElement(env, array, i, (*env)->NewStringUTF(env, args.argv[i]));
      }
      (*env)->CallStaticVoidMethod(env, jcls, methodId, array);
      if((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionDescribe(env);
        (*env)->ExceptionClear(env);
      }
    } else {
      printf("script launcher main method not found\n");
    }
  }
  (*javaVM)->DestroyJavaVM(javaVM);
  dlclose(handle);
}
