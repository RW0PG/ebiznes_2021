FROM ubuntu:18.04

ENV TZ=Europe/Warsaw


RUN apt-get update && apt-get upgrade -y

RUN apt-get install -y vim gnupg2 sudo git wget unzip curl tree

#java 8

RUN apt-get install -y openjdk-8-jdk
ENV JAVA_HOME /usr/lib/jvm/jdk8
ENV PATH $PATH:$JAVA_HOME/bin

# scala 2.12

RUN wget www.scala-lang.org/files/archive/scala-2.12.13.deb

RUN dpkg -i scala-2.12.13.deb

#npm
RUN curl -sL https://deb.nodesource.com/setup_15.x | bash -
RUN apt-get install -y nodejs

#sbt

RUN mkdir -p "/usr/local/sbt"
RUN wget -qO - --no-check-certificate "https://github.com/sbt/sbt/releases/download/v1.5.1/sbt-1.5.1.tgz" | tar xz -C /usr/local/sbt --strip-components=1

SHELL ["/bin/bash", "-c"]


EXPOSE 3000
EXPOSE 9000

WORKDIR .

COPY . .

RUN /usr/local/sbt/bin/sbt package -Dsbt.rootdir=true
ENTRYPOINT bash /usr/local/sbt/bin/sbt run -Dsbt.rootdir=true