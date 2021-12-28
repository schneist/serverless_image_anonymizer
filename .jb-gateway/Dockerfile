FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
ENV LC_ALL=C.UTF-8

RUN apt-get update
RUN apt-get install -y \
  git \
  openjdk-11-jdk \
  scala \
  nodejs \
  && rm -rf /var/lib/apt/lists/*

RUN \
	echo "deb http://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list \
	&& apt-get update -y \
	&& apt-get install -y sbt

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64