#ifndef _LAUNCHER_H
#define _LAUNCHER_H

struct EmbeddedFile {
  char* name;
  char* content;
  long contentLength;
  int boot;
  char* url;
  int download;
};

struct {
  char* version;
  char* home;
  char* homeVersion;
  char* homeLibs;
  char* homeBoot;
} launcherInfo;

#endif
