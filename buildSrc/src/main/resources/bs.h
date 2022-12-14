#ifndef _BS_H
#define _BS_H

struct EmbeddedFile {
  char* name;
  char* content;
  long contentLength;
};

struct {
  char* version;
  char* bsHome;
  char* bsHomeVersion;
  char* bsHomeLibs;
  char* bsHomeBoot;
} bsInfo;

#endif
