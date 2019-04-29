FROM java:8-jdk-alpine

#install python3 and git
RUN apk add --no-cache --update python3 git

#install bash
RUN apk update && apk add bash

#clone SZZUnleashed
#RUN git clone https://course-gitlab.tut.fi/sweng-2019/szzunleashed.git /SZZUnleashed

WORKDIR /SZZUnleashed
ADD ./szzunleashed .
ADD ./szz.sh .

#install java
RUN apk add --no-cache --update openjdk8 curl

#build SZZUnleashed
RUN mkdir /usr/lib/gradle
WORKDIR /usr/lib/gradle
RUN set -x \
 && curl -L -O https://services.gradle.org/distributions/gradle-4.10.3-bin.zip \
 && unzip gradle-4.10.3-bin.zip

ENV PATH ${PATH}:/usr/lib/gradle/gradle-4.10.3/bin

WORKDIR /SZZUnleashed/szz

RUN gradle build && gradle fatJar

#add restAPI
WORKDIR /restAPI
ADD ./restAPI .

#build restAPI
RUN ./gradlew build

#expose port for REST
EXPOSE 8080

#create volume for results
VOLUME /results

#execute REST API
CMD [ "java", "-jar", "./build/libs/gs-rest-service-0.1.0.jar" ]