FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

RUN apt-get update && apt-get upgrade -y

RUN apt-get install -y vim gnupg2 sudo git wget unzip curl

#java 8

RUN apt-get install -y openjdk-8-jdk 

# scala 2.12

RUN wget www.scala-lang.org/files/archive/scala-2.12.10.deb

RUN dpkg -i scala-2.12.10.deb

#sbt 

RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list

RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823

RUN apt-get update && apt-get install -y sbt 

#npm 

RUN curl -sL https://deb.nodesource.com/setup_15.x | bash - 
RUN apt-get install -y nodejs 

EXPOSE 3000

# user 

RUN useradd -ms /bin/bash rw0pg
RUN adduser rw0pg sudo

# workdir 

USER rw0pg
RUN mkdir /home/rw0pg/ebiznes2021
WORKDIR /home/rw0pg
VOLUME /home/rw0pg/ebiznes2021

