FROM ubuntu:16.04

ENV SBT_VERSION 1.4.3

RUN apt-get update -y && apt-get install -y curl

RUN apt-get install -y default-jdk

RUN \
  curl -L -o sbt-$SBT_VERSION.deb http://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && apt-get install sbt && \
  sbt -Dsbt.rootdir=true sbtVersion

COPY . /app
WORKDIR /app
RUN sbt compile && sbt test:compile

EXPOSE 9000

CMD sbt run