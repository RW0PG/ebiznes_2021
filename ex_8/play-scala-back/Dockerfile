FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

ENV JAVA_HOME /usr/lib/jvm/jdk8

ENV PATH $PATH:$JAVA_HOME/bin

RUN apt-get update && apt-get upgrade -y

RUN apt-get install -y vim gnupg2 sudo git wget unzip curl tree

#java 8

RUN apt-get install -y openjdk-8-jdk

# scala 2.12

RUN wget www.scala-lang.org/files/archive/scala-2.12.13.deb

RUN dpkg -i scala-2.12.13.deb

#npm
RUN curl -sL https://deb.nodesource.com/setup_15.x | bash -
RUN apt-get install -y nodejs

#sbt

RUN wget https://scala.jfrog.io/artifactory/debian/sbt-1.5.1.deb
RUN dpkg -i sbt-1.5.1.deb
RUN apt-get update
RUN apt-get install sbt

SHELL ["/bin/bash", "-c"]


EXPOSE 3000
EXPOSE 9000


VOLUME ["/home/rw0pg/ebiznes2021"]
COPY . /home/rw0pg/ebiznes2021
WORKDIR /home/rw0pg/ebiznes2021

RUN cd /home/rw0pg/ebiznes2021
CMD ["sbt"]
