FROM java:latest
RUN echo "deb http://dl.bintray.com/sbt/debian /" > /etc/apt/sources.list.d/sbt.list &&\
 apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823 &&\
 apt-get update &&\
 apt-get install --assume-yes --no-install-recommends sbt
WORKDIR /usr/src/app
COPY build.sbt /usr/src/app/
RUN sbt '+ update'
COPY . /usr/src/app/
