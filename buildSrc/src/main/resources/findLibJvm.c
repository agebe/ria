/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
#include "findLibJvm.h"
#include <stddef.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <limits.h>
#include <libgen.h>

const char* RIA_LIBJVM = "RIA_LIBJVM";
const char* RIA_JAVA_HOME = "RIA_JAVA_HOME";
const char* JAVA_HOME = "JAVA_HOME";

char libjvm[PATH_MAX];

char* which(char* name) {
  char* path = getenv("PATH");
  if(path == NULL) {
    return NULL;
  }
  int pathLen = strlen(path);
  char pc[pathLen+1];
  strcpy(pc, path);
  //printf("%s\n", pc);
  char delims[] = ":";
  char *result = NULL;  
  result = strtok(pc, delims);
  char buf[64*1024];
  while(result != NULL) {  
    char* dp = result;
    //printf("%s\n", dp);
    snprintf(buf, sizeof(buf), "%s/%s", dp, name);
    //printf("%s\n", buf);
    struct stat st = {0};
    if(stat(buf, &st) == 0) {
      char *p = malloc( sizeof(char) * ( strlen(buf) + 1 ) );
      strcpy(p, buf);
      return p;
    }
    result = strtok(NULL, delims);
  }
  return NULL;
}

char* findJavaHome() {
  char* java = which("java");
  if(java == NULL) {
    return NULL;
  }
  char buf[PATH_MAX]; /* PATH_MAX incudes the \0 so +1 is not required */
  // man 3 realpath
  // https://stackoverflow.com/a/1563237/20615256
  char *res = realpath(java, buf);
  char* javaHome = NULL;
  if(res) {
    //printf("%s\n", buf);
    //printf("%s\n", dirname(dirname(buf)));
    // man 3 dirname
    char* dir = dirname(dirname(buf));
    javaHome = malloc( sizeof(char) * ( strlen(dir) + 1 ) );
    strcpy(javaHome, buf);
  }
  free(java);
  return javaHome;
}

char* findLibJvm() {
  if(getenv(RIA_LIBJVM)) {
    snprintf(libjvm, sizeof(libjvm), "%s", getenv(RIA_LIBJVM));
  } else if(getenv(RIA_JAVA_HOME)) {
    snprintf(libjvm, sizeof(libjvm), "%s/lib/server/libjvm.so", getenv(RIA_JAVA_HOME));
  } else if(getenv(JAVA_HOME)) {
    snprintf(libjvm, sizeof(libjvm), "%s/lib/server/libjvm.so", getenv(JAVA_HOME));
  } else {
    char* javaHome = findJavaHome();
    if(javaHome == NULL) {
      return NULL;
    }
    snprintf(libjvm, sizeof(libjvm), "%s/lib/server/libjvm.so", javaHome);
    free(javaHome);
  }
  return libjvm;
}
