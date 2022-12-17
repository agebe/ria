//#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <dlfcn.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <stdbool.h>
#include <limits.h>

#include "bs.h"
#include "findLibJvm.h"
#include "launchJvm.h"
#include "files.h"

char bsHome[PATH_MAX];
char bsHomeVersion[PATH_MAX];
char bsHomeLibs[PATH_MAX];
char bsHomeBoot[PATH_MAX];

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
  makedir(bsInfo.bsHome);
  makedir(bsInfo.bsHomeVersion);
  makedir(bsInfo.bsHomeLibs);
  makedir(bsInfo.bsHomeBoot);
  //printf("%s\n", bshomelibs);
  initFiles();
  bool isSnapShotVersion = strstr(version, "SNAPSHOT") != NULL;
  for(int i=0;i<filesCount;i++) {
    char filename[PATH_MAX];
    if(files[i].boot) {
      snprintf(filename, sizeof(filename), "%s/%s", bsInfo.bsHomeBoot, files[i].name);
    } else {
      snprintf(filename, sizeof(filename), "%s/%s", bsInfo.bsHomeLibs, files[i].name);
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

void initBsInfo() {
  snprintf(bsHome, PATH_MAX, "%s/.bs", getenv("HOME"));
  snprintf(bsHomeVersion, PATH_MAX, "%s/%s", bsHome, version);
  snprintf(bsHomeLibs, PATH_MAX, "%s/libs", bsHomeVersion);
  snprintf(bsHomeBoot, PATH_MAX, "%s/boot", bsHomeVersion);
  bsInfo.version = version;
  bsInfo.bsHome = bsHome;
  bsInfo.bsHomeVersion = bsHomeVersion;
  bsInfo.bsHomeLibs = bsHomeLibs;
  bsInfo.bsHomeBoot = bsHomeBoot;
}

int main(int argc, char **argv) {
  initBsInfo();
  writeFiles();
  char* libjvm = findLibJvm();
  launchJvm(libjvm, argc, argv);
  return 0;
}
