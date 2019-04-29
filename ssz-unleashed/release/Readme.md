## Build Docker Container
Build the Docker container:

	docker build -t szz .
Run the Docker container:

	docker run -p 8080:8080 szz
## Rest-API Calls:
To analyze the issues from all the commits of a given repo:

    address:8080/parameters?repo=[url]&tracker=[url]&code=[projectIssueCode]
To analyze the issues of a repo from a specific commit:

    address:8080/parameters?repo=[url]&tracker=[url]&code=[projectIssueCode]&from=[commitID]
To check the status of the analyzing process:

    address:8080/status
To get the results of the analyzed repo:

    address:8080/results

## Communication with the command-line tool:
To analyze the issues from all the commits of a given repo:

    szz.sh issue_url repo_url issue_code
To analyze the issues of a repo from a specific commit:
       
    szz.sh issue_url repo_url issue_code commitID
    
The results from "fix_and_introducers_pairs.json"
 are copied to the directory /results after the process is finished.