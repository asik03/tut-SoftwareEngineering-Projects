## Build the docker image by using:

    docker build . -t=refactoring_miner_service

## Run a docker container of the image by using:

    docker run -p 8080:8080 refactoring_miner_service

  

## Call the service by using:
To analyze all commits of a repo:

    address:8080/parameters?repo=[url]
   <br>
To analyze between commits of a repo:

    address:8080/parameters?repo=[url]&commit1=[begin]&commit2=[end]
   <br>
To check the status of the refactoring miner tool:

    address:8080/status
   <br>
To obtain the results of the analyzed repo:

    address:8080/results
   <br>

