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
// #include <curl/curl.h>

#include "bs.h"
#include "findLibJvm.h"
#include "launchJvm.h"
#include "files.h"

const char* BS_HOME = "BS_HOME";

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

// void download(char *url, char* outfilename) {
//   CURL *curl;
//   FILE *fp;
//   CURLcode res;
//   curl = curl_easy_init();
//   if (curl) {
//     fp = fopen(outfilename,"wb");
//     curl_easy_setopt(curl, CURLOPT_URL, url);
//     curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, NULL);
//     curl_easy_setopt(curl, CURLOPT_WRITEDATA, fp);
//     res = curl_easy_perform(curl);
//     curl_easy_cleanup(curl);
//     fclose(fp);
//   }
// }

void writeLibsFile() {
  char filename[PATH_MAX];
  snprintf(filename, sizeof(filename), "%s/libs.txt", bsInfo.bsHomeVersion);
  FILE *f = fopen(filename, "w");
  if (f == NULL) {
    printf("Error opening file!\n");
    exit(1);
  }
  for(int i=0;i<filesCount;i++) {
    if(!files[i].boot && (files[i].url != NULL)) {
      //printf("%s\n", files[i].url);
      fprintf(f, "%s\n", files[i].url);
    }
  }
  fclose(f);
}

void writeFiles() {
  makedir(bsInfo.bsHome);
  makedir(bsInfo.bsHomeVersion);
  makedir(bsInfo.bsHomeLibs);
  makedir(bsInfo.bsHomeBoot);
  initFiles();
  writeLibsFile();
  bool isSnapShotVersion = strstr(version, "SNAPSHOT") != NULL;
  for(int i=0;i<filesCount;i++) {
    char filename[PATH_MAX];
    if(files[i].boot) {
      snprintf(filename, sizeof(filename), "%s/%s", bsInfo.bsHomeBoot, files[i].name);
    } else {
      snprintf(filename, sizeof(filename), "%s/%s", bsInfo.bsHomeLibs, files[i].name);
    }
    struct stat st = {0};
    bool notExists = stat(filename, &st) == -1;
    if(isSnapShotVersion && !files[i].download) {
      FILE *write_ptr;
      write_ptr = fopen(filename,"wb");
      fwrite(files[i].content,1,files[i].contentLength,write_ptr);
      fclose(write_ptr);
    } else if(notExists) {
      if(files[i].download) {
//        printf("%s\n",files[i].url);
//        download(files[i].url, filename);
      } else {
        FILE *write_ptr;
        write_ptr = fopen(filename,"wb");
        fwrite(files[i].content,1,files[i].contentLength,write_ptr);
        fclose(write_ptr);
      }
    }
  }
}

char* getHomeDir(int argc, char **argv) {
  for(int i=0;i<argc;i++) {
    char* s = argv[i];
    if(strcmp("--home", s) == 0) {
      i++;
      if(i < argc) {
        return argv[i];
      } else {
        return NULL;
      }
    }
  }
  return NULL;
}

void initBsInfo(int argc, char **argv) {
  char* homeDir = getHomeDir(argc, argv);
  if(homeDir) {
    snprintf(bsHome, PATH_MAX, "%s", homeDir);
  } else if(getenv(BS_HOME)) {
    snprintf(bsHome, PATH_MAX, "%s", getenv("BS_HOME"));
  } else {
    snprintf(bsHome, PATH_MAX, "%s/.bs", getenv("HOME"));
  }
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
  initBsInfo(argc, argv);
  writeFiles();
  char* libjvm = findLibJvm();
  launchJvm(libjvm, argc, argv);
  return 0;
}
