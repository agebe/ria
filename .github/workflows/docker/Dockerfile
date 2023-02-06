FROM debian:bullseye
# https://stackoverflow.com/a/28406007/20615256
RUN apt update && \
  apt install gcc openjdk-17-jdk-headless wget unzip git locales -y && \
  sed -i '/en_US.UTF-8/s/^# //g' /etc/locale.gen && \
  locale-gen && \
  wget "https://services.gradle.org/distributions/gradle-7.6-bin.zip" && \
  unzip gradle-7.6-bin.zip && \
  ln -s /gradle-7.6/bin/gradle /usr/local/bin/ && \
  rm -rf /var/lib/apt/lists/*
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
