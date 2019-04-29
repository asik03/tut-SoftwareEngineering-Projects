
## Rest-API Calls:
To analyze the changes from all the commits of a given repo:

    address:8080/parameters?repo=[url]
To analyze the changes of a repo from specific commits:

    address:8080/parameters?repo=[url]&commit1=[commit_hash]&commit2=[commit_hash]
To check the status of the analyzing process:

    address:8080/status
To get the results of the analyzed repo:

    address:8080/results

The results from the repo analyzed
 are copied to the directory change-distiller/results after the process is
 finished. Docker volume can be specified to save the data into the local machine.