#!/bin/sh
if [ $# -eq 3 ]; then
	echo "Analyzing repo $2 from issue-tracker $1 with issue-code $3 ..."
	#echo "cd to /SZZUnleashed/fetch_jira_bugs/"
	cd /SZZUnleashed/fetch_jira_bugs/
	echo "Executing fetch.py with issue code $3 and jira-project $1"
	python3 fetch.py --issue-code "$3" --jira-project "$1"

	rm -r /szz_repo 2>nul
	echo "Cloning git repo $2 ..."
	git clone "$2" /szz_repo/

	cd /szz_repo/
	commit=$(git rev-list HEAD | tail -n 1)
	echo "tail commit found is $commit"

	echo "executing git_log_to_array.py"
	cd /SZZUnleashed/fetch_jira_bugs/
	python3 git_log_to_array.py --repo-path /szz_repo --from-commit $commit

	echo "executing find_bug_fixes.py with apache pattern"
	python3 find_bug_fixes.py --gitlog ./gitlog.json --issue-list ./issues --gitlog-pattern "<r$3'-{nbr}\D|#{nbr}\D|HUDSON-{nbr}\D'>"
	cd ../szz
	echo "executing szz_find_bug_introducers-0"
	java -jar ./build/libs/szz_find_bug_introducers-0.1.jar -i /SZZUnleashed/fetch_jira_bugs/issue_list.json -r /szz_repo
elif [ $# -eq 4 ]; then
	echo "cd to fetch_jira_bugs/"
	cd fetch_jira_bugs/
	echo "executing fetch.py"
	python3 fetch.py --issue-code "$3" --jira-project "$1"

	rm -r /szz_repo 2>nul
	echo "Cloning git repo $2 ..."
	git clone "$2" /szz_repo/

	echo "executing git_log_to_array.py from commit $4"
	python3 git_log_to_array.py --repo-path /szz_repo --from-commit "$4"

	echo "executing find_bug_fixes.py with pattern"
	python3 find_bug_fixes.py --gitlog ./gitlog.json --issue-list ./issues --gitlog-pattern "<r$3'-{nbr}\D|#{nbr}\D|HUDSON-{nbr}\D'>"
	cd ../szz
	echo "executing szz_find_bug_introducers-0"
	java -jar ./build/libs/szz_find_bug_introducers-0.1.jar -i /SZZUnleashed/fetch_jira_bugs/issue_list.json -r /szz_repo
else
    echo "Wrong number of parameters."
    exit 1
fi