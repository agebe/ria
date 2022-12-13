#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <dlfcn.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <stdbool.h>

#include "bs.h"
#include "findLibJvm.h"
#include "files.h"

char bshome[1000];
char bshomeversion[1100];
char bshomelibs[1200];

char classpath[64*1024] = "-Djava.class.path=";

void makedir(char *name) {
  struct stat st = {0};
  if(stat(name, &st) == -1) {
    if(mkdir(name, 0700) != 0) {
      printf("mkdir '%s' failed\n", name);
      exit(1);
    }
  }
}
  
void writeFiles() {
  snprintf(bshome, 1000, "%s/.bs", getenv("HOME"));
  snprintf(bshomeversion, 1100, "%s/%s", bshome, version);
  snprintf(bshomelibs, 1200, "%s/libs", bshomeversion);
  makedir(bshome);
  makedir(bshomeversion);
  makedir(bshomelibs);
  //printf("%s\n", bshomelibs);
  initFiles();
  bool isSnapShotVersion = strstr(version, "SNAPSHOT") != NULL;
  for(int i=0;i<filesCount;i++) {
    char filename[2000];
    snprintf(filename, sizeof(filename), "%s/%s", bshomelibs, files[i].name);
    //printf("%s\n", filename);
    strcat(classpath, filename);
    if((i+1)<filesCount) {
      strcat(classpath, ":");
    }
    struct stat st = {0};
    if(isSnapShotVersion || (stat(filename, &st) == -1)) {
      FILE *write_ptr;
      write_ptr = fopen(filename,"wb");
      fwrite(files[i].content,1,files[i].contentLength,write_ptr);
      fclose(write_ptr);
    }
  }
}

int main(int argc, char **argv) {
  writeFiles();
  //printf("%s\n", classpath);
  JavaVMOption jvmopt[1];
  jvmopt[0].optionString = classpath;
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
  char* libjvm = findLibJvm();
  if(libjvm == NULL) {
    printf("libjvm.so not found, consider setting JAVA_HOME or BS_JAVA_HOME or BS_LIBJVM (pointing to e.g. $JAVA_HOME/lib/server/libjvm.so\n");
    return 1;
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
    printf("Error creating VM. Exiting...\n");
    return 1;
  }
  jclass jcls = (*env)->FindClass(env, "org/rescript/launcher/ScriptLauncher");
  if (jcls == NULL) {
    (*env)->ExceptionDescribe(env);
    (*javaVM)->DestroyJavaVM(javaVM);
    return 1;
  }
  if (jcls != NULL) {
    //https://stackoverflow.com/a/9437132
    //https://docs.oracle.com/javase/1.5.0/docs/guide/jni/spec/types.html#wp276
    jmethodID methodId = (*env)->GetStaticMethodID(env, jcls, "main", "([Ljava/lang/String;)V");
    if(methodId != NULL) {
      jobjectArray array = (jobjectArray)(*env)->NewObjectArray(env, argc-1,(*env)->FindClass(env, "java/lang/String"),(*env)->NewStringUTF(env, ""));
      for(int i=1;i<argc;i++) {
        (*env)->SetObjectArrayElement(env, array, i-1, (*env)->NewStringUTF(env, argv[i]));
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
  return 0;
}
