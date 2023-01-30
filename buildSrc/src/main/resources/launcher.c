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

#include "launcher.h"
#include "findLibJvm.h"
#include "launchJvm.h"
#include "files.h"

const char* RIA_HOME = "RIA_HOME";

char launcherHome[PATH_MAX];
char launcherHomeVersion[PATH_MAX];
char launcherHomeLibs[PATH_MAX];
char launcherHomeBoot[PATH_MAX];

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
  snprintf(filename, sizeof(filename), "%s/libs.txt", launcherInfo.homeVersion);
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
  makedir(launcherInfo.home);
  makedir(launcherInfo.homeVersion);
  makedir(launcherInfo.homeLibs);
  makedir(launcherInfo.homeBoot);
  initFiles();
  writeLibsFile();
  bool isSnapShotVersion = strstr(version, "SNAPSHOT") != NULL;
  for(int i=0;i<filesCount;i++) {
    char filename[PATH_MAX];
    if(files[i].boot) {
      snprintf(filename, sizeof(filename), "%s/%s", launcherInfo.homeBoot, files[i].name);
    } else {
      snprintf(filename, sizeof(filename), "%s/%s", launcherInfo.homeLibs, files[i].name);
    }
    struct stat st = {0};
    bool notExists = stat(filename, &st) == -1;
    //printf("%s\n", filename);
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

void initLauncherInfo(int argc, char **argv) {
  char* homeDir = getHomeDir(argc, argv);
  if(homeDir) {
    snprintf(launcherHome, PATH_MAX, "%s", homeDir);
  } else if(getenv(RIA_HOME)) {
    snprintf(launcherHome, PATH_MAX, "%s", getenv(RIA_HOME));
  } else {
    snprintf(launcherHome, PATH_MAX, "%s/.ria", getenv("HOME"));
  }
  snprintf(launcherHomeVersion, PATH_MAX, "%s/%s", launcherHome, version);
  snprintf(launcherHomeLibs, PATH_MAX, "%s/libs", launcherHomeVersion);
  snprintf(launcherHomeBoot, PATH_MAX, "%s/boot", launcherHomeVersion);
  launcherInfo.version = version;
  launcherInfo.home = launcherHome;
  launcherInfo.homeVersion = launcherHomeVersion;
  launcherInfo.homeLibs = launcherHomeLibs;
  launcherInfo.homeBoot = launcherHomeBoot;
}

int main(int argc, char **argv) {
  initLauncherInfo(argc, argv);
  writeFiles();
  char* libjvm = findLibJvm();
  launchJvm(libjvm, argc, argv);
  return 0;
}
