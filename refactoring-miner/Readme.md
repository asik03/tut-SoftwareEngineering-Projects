#Refactoring Miner

**-----------------------------These comands have to be executed to run the miner in the docker container----

**execute in docker console

docker build -t sprint01/jar:0.0.6 .

docker run -it --rm sprint01/jar:0.0.6 sh mine.sh

**after its done copy results by this comand from docker, replace container ID
docker cp 6e110247ce6e:/miner/repositories/mina-sshd/all_refactorings.csv "C:\Users\danie\OneDrive\Dokumente\TUT\SoftwareEngineeringMethodologies\Project\2\refactoring-miner"