FROM openjdk:8-jdk


#----------------------------
# Copy Script
COPY /scripts /miner

# Copy RefactoringMiner Jar
COPY /lib /miner/lib

#Copy Repositories to be analyzed
COPY /repositories /miner/repositories

# Go to directory to execute
WORKDIR /miner

# Expose port for REST
EXPOSE 8080

# start script to miner
#RUN sh mine.sh