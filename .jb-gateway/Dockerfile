FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
ENV LC_ALL=C.UTF-8

RUN apt-get update

RUN apt-get install -y \
  git \
  openjdk-11-jdk \
  scala \
  apt-transport-https \
  curl \
  gnupg2 \
  openssh-client

  
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
RUN chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
RUN apt-get update
RUN apt-get install -y sbt

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

RUN rm /bin/sh && \
	ln -s /bin/bash /bin/sh && \
	mkdir -p /root/.nvm

ENV NVM_DIR /root/.nvm
ENV NODE_VERSION 17.3.0

RUN apt-get update --fix-missing 
RUN curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.39.1/install.sh | bash 
RUN	source $NVM_DIR/nvm.sh  
RUN	nvm install $NODE_VERSION 
RUN nvm use $NODE_VERSION 

ENV PATH $NVM_DIR/versions/node/v$NODE_VERSION/bin:$PATH