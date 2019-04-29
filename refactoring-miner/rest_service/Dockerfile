FROM openjdk:8-jdk

COPY /mine.sh /

# Working directory set for refactoring miner
WORKDIR /refactoringminer

#----------------------------
# Copy refactoring miner
COPY ./RefactoringMiner .

#Build Refactoring Miner
RUN ./gradlew distZip
#RUN ls build/libs

#RUN apt-get install unzip

RUN unzip ./build/distributions/RefactoringMiner.zip -d ./build/distributions/
RUN cp ./build/distributions/RefactoringMiner/bin/RefactoringMiner ../
#RUN rm -r /refactoringminer

#RUN git clone https://github.com/apache/mina-sshd.git ./mining_repo/
#RUN ./dist/RefactoringMiner/bin/RefactoringMiner -a ./mining_repo/

#----------------------------
# Working directory set for rest api
WORKDIR /restApi

# Copy rest api
COPY ./restApi .

#Build Rest Api
RUN ./gradlew build

# Expose port for REST API
EXPOSE 8080

VOLUME /results

CMD  [ "java", "-jar", "./build/libs/gs-rest-service-0.1.0.jar" ]