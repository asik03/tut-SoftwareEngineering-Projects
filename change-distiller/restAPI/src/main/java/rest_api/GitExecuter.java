package rest_api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;


import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.distilling.Distiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;


public class GitExecuter {
		
	public static String cloneRepo (String repo_) throws Exception
	{
		
		String repo = repo_;
		String dir = "";
		String[] dir_ = repo.split("//")[1].split("/");
		dir = dir_[dir_.length-1];
		
		File src = new File (dir);
		File dest = new File (dir+"_");
			
		try {
			Runtime rt = Runtime.getRuntime();
			String cmd = String.format("git clone %s", repo);
			Process process = rt.exec(cmd);
					
			process.waitFor();
			System.out.println ("Repository cloned...");

			
			GitExecuter gitExecuter_ = new GitExecuter();
			gitExecuter_.copydir(src, dest);
			
		}
		catch (IOException e) {
			System.out.println ("Couldn't clone repository, error occured...");
		}
		
		return dir;
	}
	
	
	public void copydir(File src, File dest) throws IOException
    {

        if (src.isDirectory())
        {

            // if directory not exists, create it
            if (!dest.exists())
            {
                dest.mkdir();
                /*System.out.println("Directory copied from " + src + "  to "
                        + dest);*/
            }

            // list all the directory contents
            String files[] = src.list();

            for (String fileName : files)
            {
                // construct the src and dest file structure
                File srcFile = new File(src, fileName);
                File destFile = new File(dest, fileName);
                // recursive copy
                copydir(srcFile, destFile);
            }

        }
        else
        {
            // If file, then copy it
            fileCopy(src, dest);
        }
    }
	
	private void fileCopy(File src, File dest)
            throws FileNotFoundException, IOException
    {

        InputStream in = null;

        try (OutputStream out = new FileOutputStream(dest))
        {
            // If file, then copy it
            in = new FileInputStream(src);
            

            byte[] buffer = new byte[1024];

            int length;
            // Copy the file content in bytes
            while ((length = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, length);
            }

        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
    }
	
	public static List takeDiff (String commit1_, String commit2_, String dir_) throws Exception
	{
		List commitDiff = new ArrayList();
		String line = "";
		
		try{
			Runtime rt = Runtime.getRuntime();	
			String commit1 = commit1_;
			String commit2 = commit2_;
			String dir = dir_;
			String cmd = String.format("git -C %s --no-pager diff --name-only %s %s", dir, commit1, commit2);
			Process pr = rt.exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			commitDiff = new ArrayList();
			while ((line = input.readLine()) != null) {
				System.out.println(line);
				commitDiff.add(line);
			  }
					
			input.close();
			}
		catch (IOException e) {
			System.out.println ("Error occured");
		}
		
		return commitDiff;
	}

	/**
	 * 
	 * @param path Path to the csv that wants to be written
	 * @return A PrintWriter for a results csv file with the headers already
	 * 			written (Git Date, Git Hash, File, Distiller).
	 * 		   Returns null if it could not be created.
	 */
	static PrintWriter csvWriter(String path){
		try (PrintWriter writer = new PrintWriter(new File(path))) {
			StringBuilder sb = new StringBuilder();
			sb.append("Git Dates;");
			sb.append("Git Hashes;");
			sb.append("File;");
			sb.append("Distiller Result");
			sb.append('\n');
		
			writer.write(sb.toString());
			writer.close();	
			return writer;		
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 
	 * @param commitHash Hash of the commit whose date we want to obtain
	 * @param repoDir DirPath for repo
	 * @return Date of the commit in string with format %ci
	 */
	public static String getCommitDate(String commitHash, String repoDir){
		String date = "";
		String cmd = "git -C " + repoDir + " show -s --format=%ci "
		 + commitHash;

		 try{
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(cmd);
			BufferedReader input = new BufferedReader(
				new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				date = line;
			}
			input.close();
		} catch (Throwable t)
		{
			//t.printStackTrace();
			System.out.println("Error when getting commit date");
		}

		return date;
	}
	/**
	 * 
	 * @param commit1 Hash for left commit to compare
	 * @param commit2 Hash for right commit to compare
	 * @param diffList List of files modified between commits
	 * @param resPath Path for results CSV file
	 * @param repoDirName DirPath for repo
	 */
	public static void writeChangesToCsv(String commit1, String commit2,
	List<String> diffList, String resPath, String repoDirName){
		
		String commit1Date = getCommitDate(commit1, repoDirName + "/");
		String commit2Date = getCommitDate(commit2, repoDirName + "/");

		//System.out.println(commit1Date);
		//System.out.println(commit2Date);

		//Check differences between files from both commits
		for (int j =0; j<diffList.size(); ++j){
			String fileName = diffList.get(j);
			
			//Paths to files to compare
			String pathOne = repoDirName + "/" + fileName;
			String pathTwo = repoDirName + "_/" + fileName;

			//Files to compare
			File firstFile = new File(pathOne);
			File secondFile = new File(pathTwo);
			
			//Check that the file exists in both commits	
			if(firstFile.exists() && !firstFile.isDirectory() &&
			secondFile.exists() && !secondFile.isDirectory()){ 

				//Initiate change distiller
				FileDistiller distiller = 
				ChangeDistiller.createFileDistiller(Language.JAVA);
				
				//Execute change distiller on both files
				try {
					distiller.extractClassifiedSourceCodeChanges(firstFile, secondFile);
				} catch(Exception e) {
					System.err.println("Warning: error while change distilling. " + e.getMessage());
				}
				
				//Print changes to CSV file
				List<SourceCodeChange> changeList = distiller.getSourceCodeChanges();	
				if(changeList != null) {
					for(SourceCodeChange change : changeList) {
						StringBuilder sb = new StringBuilder();
						sb.append(commit1Date + "->" + commit2Date);
						sb.append(';');
						sb.append(commit1 + "->" + commit2);
						sb.append(';');
						sb.append(fileName);
						sb.append(';');
						sb.append(change.toString().replaceAll("\n", "\\\\n"));
						sb.append('\n');						 

						try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(resPath), true))) {			
							writer.write(sb.toString());	
							writer.flush();
							writer.close();
						} catch (FileNotFoundException e) {
							System.out.println(e.getMessage());
						}
					}
				}
			}
		}
	}
	public static void callExecuter(String repo_, String cmt1, String cmt2) throws Exception {
		
		try {
			String dir = cloneRepo (repo_);
			Runtime rt = Runtime.getRuntime();
			List IDs = new ArrayList();
			Process pc1;
			Process pc2;
			String line;
			
			//Create results folder if it does not exist
			String resDirPath = "./results/";
			File resDir = new File(String.valueOf(resDirPath));
			if(!resDir.exists()){
				resDir.mkdir();
			}

			//Create PrintWriter for CSV file
			String resPath = resDirPath + dir + "_results.csv";
			PrintWriter writer = csvWriter(resPath);
			if(writer == null){
				System.out.println("Could not create csv file");
				return;
			}

			if (cmt1 == null || cmt2== null || cmt1.isEmpty() || cmt2.isEmpty())
			{
				try {

					Process p = rt.exec(String.format("git -C %s --no-pager log --pretty=format:'%%h'", dir));
					TimeUnit.SECONDS.sleep(2);
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					
					while ((line = input.readLine()) != null) {
						IDs.add(line);
					}
					input.close();
					
					List commitDiff = new ArrayList();
					for (int i=0; i < IDs.size()-1; ++i)
					{
						//System.out.println(IDs.get(i) +" : " + IDs.get(i+1));

						//Get commits to compare
						String commit1 = (IDs.get(i).toString())
						.replaceAll("\'","");
						String commit2 = (IDs.get(i+1).toString())
						.replaceAll("\'","");

						//Check modified files between commits
						commitDiff = takeDiff (commit1, commit2, dir);

						//Checkout to both commits
						pc1 = rt.exec(String.format("git -C %s checkout %s", dir, commit1));
					
						pc2 = rt.exec(String.format("git -C %s checkout %s", dir+"_", commit2));

						
						//Check changes on all modified files and write to CSV
						writeChangesToCsv(commit1, commit2, commitDiff, resPath,
						dir);

						
					}
						
				} catch (IOException e) {
					System.out.println ("Error occured");
				}
				
			}
			else if (cmt1 != null && cmt2 != null && !cmt1.isEmpty() && !cmt2.isEmpty())
			{
				List commitDiff = new ArrayList();
				
				//Check modified files between commits
				commitDiff = takeDiff(cmt1, cmt2, dir);
					  
				//Checkout to both commits
				pc1 = rt.exec(String.format("git -C %s checkout %s", dir, cmt1));
						
				pc2 = rt.exec(String.format("git -C %s checkout %s", dir+"_", cmt2));

				//Check changes on all modified files and write to CSV
				writeChangesToCsv(cmt1, cmt2, commitDiff, resPath,
				dir);

				
			}
			
			pc1 = rt.exec(String.format("git -C %s checkout master", dir)); 
			pc1 = rt.exec(String.format("git -C %s checkout master", dir+"_"));
		}
		catch (IOException e) {
			System.out.println("Couldn't clone repository");
		}
	}
	
	/*public static void main(String[]args) throws Exception {
		if (args.length == 1)
			callExecuter (args[0], "", "");
		else if (args.length == 3)
			callExecuter (args[0], args[1], args[2]);
		else
			System.out.println("Incorrect Number of Parameters given");
	}*/

}




