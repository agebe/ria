#!/bin/bash
docker build --pull -t ria-build .
docker tag ria-build:latest agebe/ria-build:latest
docker push agebe/ria-build:latest
