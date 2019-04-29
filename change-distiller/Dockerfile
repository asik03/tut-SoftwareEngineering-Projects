FROM java:8-jdk-alpine

#install bash
RUN apk update && apk add bash

#install java
RUN apk add --no-cache --update openjdk8 curl

#install maven into the docker container
ENV MAVEN_VERSION 3.3.9
RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven

#install Git
RUN apk update && apk upgrade && \
    apk add --no-cache bash git openssh

#add files to the docker container
WORKDIR /change-distiller
ADD ./restAPI .

# install maven to change distiller
RUN mvn install -e -U

#build restAPI
#RUN ./gradlew build

#expose port for REST
EXPOSE 8080

#create volume for results
VOLUME /change-distiller/results

#execute REST API
CMD [ "java", "-jar", "./target/gs-rest-service-0.1.0.jar" ]