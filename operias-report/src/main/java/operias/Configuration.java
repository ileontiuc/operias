package operias;

import java.io.File;
import java.security.InvalidParameterException;

import operias.git.Git;


/**
 * Configuration of operias, checks and sets the source and repository directories and a branch
 * @author soosterwaal
 *
 */
public class Configuration {
	
    public static final String USAGE_INSTRUCTIONS = "" +
            "Usage: operias-report -d <directory> -od <directory> -rd <directory> [options] \n" +
            "   or: operias-report -d <directory> -ru <url> -oc <commit-id> -obn <branch-name> -rc <commit-id> -rbn <branch-name> [options] \n" +
            "   or: operias-report -d <directory> -oru <url> -oc <commit-id> -obn <branch-name> -rru <url> -rc <commit-id> -rbn <branch-name> [options] \n" +
            "\n" +
            "Parameters: \n" +
            "       -d,--destination-directory	The directory where the generated site will be placed\n" +
    		"       -od,--original-directory		This directory contains the original source code, unchanged. This should be the project directory containing the main pom.xml.\n" +
            "       -rd,--revised-directory		This directory contains the revised source code, this directory will be compared to the original directory. This should be the project directory containing the main pom.xml.\n" +
            "       -ru,--repository-url		Instead of providing a directories containing the versions, you can also give a git url. Operias will clone the repository into it's own temporary repository directory.\n" +
            "       -oru,--original-repository-url	The git url used for the original version of the source code.\n" +
            "       -oc,--original-commit-id		Operias will checkout to this commit to use as original source code.\n" +
            "       -obn,--original-branch-name	Operias will checkout to this branch to use as original source code.\n" +
            "       -rru,--revised-repository-url	The git url used for the revised version of the source code.\n" +
            "       -rc,--revised-commit-id		Operias will checkout to this commit to use as revised source code.\n"+
            "       -rbn,--revised-branch-name	Operias will checkout to this branch to use as revised source code.\n"+
            "\n" +
            "Options: \n" +
            "       -td,--temp-directory	If Operias fails to execute, it is possible it does not have the rights to create a temporary folder, use this parameter to set your own temporary directory for Operias. Be aware, it will delete all the contents of the temporary folder when the execution has completed.\n" +
            "       -v,--verbose		Provide this parameter enable the output of errors, warnings and info messages.\n";    

	/**
	 * Static class, so private constructor
	 */
	private Configuration() {
		
	}
	
	/**
	 * Source directory, containing the source files which needs to be compared to the branch in the repository directory
	 */
	private static String revisedDirectory = null;
	
	/**
	 * Repository directory, containing the sources files which are used as a base for the comparison
	 */
	private static String originalDirectory = null;
	

	
	/**
	 * The original commit identifier, need to checkout to this commit to get the original directory
	 */
	private static String originalCommitID = null;
	
	/**
	 * The revised commit identifier, need to checkout to this commit to get the revised directory
	 */
	private static String revisedCommitID = null;

	/**
	 * The original branch name, need to checkout to this branch to get the original directory
	 */
	private static String originalBranchName = null;
	
	/**
	 * The revised branch name, need to checkout to this branch to get the revised directory
	 */
	private static String revisedBranchName = null;
	
	/**
	 * The destination directory for the generated website
	 */
	private static String destinationDirectory = (new File("site")).getAbsolutePath();
	
	/**
	 * The original repository url
	 */
	private static String originalRepositoryURL = null;
	
	/**
	 * The revised repository url
	 */
	private static String revisedRepositoryURL = null;
	
	/**
	 * A temporary directory in which the git cloned/checkout directory will be stored in
	 */
	private static String temporaryDirectory = (new File("temp")).getAbsolutePath();
	
	/**
	 * By default, output is disabled
	 */
	private static boolean outputEnabled = false;
	
	/**
	 * Parse the arguments passed by the command line
	 * @param args
	 */
	public static void parseArguments(String[] args) {
		try {
			int i = 0;
			if (args.length == 0) {
				printInstructions();
			}
			while(i < args.length) {
				if (args[i].equals("-d") || args[i].equals("--destination-directory")) {
					// Destination folder
					Configuration.setDestinationDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rd") || args[i].equals("--revised-directory")) {
					Configuration.setRevisedDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-od") || args[i].equals("--original-directory")) {
					Configuration.setOriginalDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-ru") || args[i].equals("--repository-url")) {
					Configuration.setOriginalRepositoryURL(args[i + 1]);
					Configuration.setRevisedRepositoryURL(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-oru") || args[i].equals("--original-repository-url")) {
					Configuration.setOriginalRepositoryURL(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rru") || args[i].equals("--revised-repository-url")) {
					Configuration.setRevisedRepositoryURL(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-oc") || args[i].equals("--original-commit-id")) {
					Configuration.setOriginalCommitID(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rc") || args[i].equals("--revised-commit-id")) {
					Configuration.setRevisedCommitID(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-obn") || args[i].equals("--original-branch-name")) {
					Configuration.setOriginalBranchName(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-rbn") || args[i].equals("--revised-branch-name")) {
					Configuration.setRevisedBranchName(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-td") || args[i].equals("--temp-directory")) {
					Configuration.setTemporaryDirectory(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-v") || args[i].equals("--verbose")) {
					Configuration.setOutputEnabled(true);
					i++;
				} else {
					Main.printLine("[Error] Unknown option \"" + args[i] + "\"");
					printInstructions();
					System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
				}
			}
		} catch (Exception e) {
			printInstructions();
			System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
		}

	}
	
	
	/**
	 * Sets and checks the source directory, will throw an exception if it is an invalid directory
	 * A valid directory must also contain a pom.xml file to ensure that it is a maven project.
	 * @param revisedDirectory Valid local source directory.
	 */
	public static void setRevisedDirectory(String revisedDirectory) {
		if (checkValidDirectory(revisedDirectory)) {
			Configuration.revisedDirectory = revisedDirectory;
		} else {
			throw new InvalidParameterException("Error: '" + revisedDirectory + "' is not a valid directory for Operias");
		}
	}
		
	/**
	 * Sets and checks the repository directory, will throw an exception if it is an invalid directory or does not contain a git repo
	 * A valid directory must also contain a pom.xml file to ensure that it is a maven project.
	 * @param originalDirectory
	 */
	public static void setOriginalDirectory(String originalDirectory) {
		if (checkValidDirectory(originalDirectory)) {
			Configuration.originalDirectory = originalDirectory;
		} else {
			throw new InvalidParameterException("Error: '" + originalDirectory + "' is not a valid directory for Operias");
		}
	}
	
	/**
	 * Check if the directory is valid, should contain a pom.xml file
	 * @param directory Directory
	 * @return True if it is a valid directory, false otherwise
	 */
	private static boolean checkValidDirectory(String directory) {
		File file = null;
		
		try {
			file = new File(directory);
		} catch (NullPointerException e) {
			return false;
		}
		
		if (!file.isDirectory()){
			// invalid source directory
			return false;
		}
		
		File pomXML = new File(file.getAbsolutePath() +  "/pom.xml");
		
		
		return pomXML.exists();
	}
		
	/**
	 * Get the source directory name
	 * @return Source directory
	 */
	public static String getRevisedDirectory(){
		return revisedDirectory;
	}
	
	/**
	 * Get the repository directory name
	 * @return Repository directory name
	 */
	public static String getOriginalDirectory() {
		return originalDirectory;
	}
	
	/**
	 * Reset the configuration, used for testing mostly
	 */
	public static void resetConfiguration() {
		destinationDirectory = new File("site").getAbsolutePath();
		temporaryDirectory = new File("temp").getAbsolutePath();
		revisedBranchName = null;
		revisedCommitID = null;
		revisedDirectory = null;
		revisedRepositoryURL = null;
		originalBranchName = null;
		originalCommitID = null;
		originalDirectory = null;
		originalRepositoryURL = null;
		outputEnabled = false;
	}

	
	/**
	 * Set up directory through git. Return the repository directory, based on the repository url, branch name and commit id
	 * @param repositoryURL
	 * @param branchName
	 * @param commitID
	 * @return
	 * @throws Exception
	 */
	public static String setUpDirectoriesThroughGit(String repositoryURL, String branchName, String commitID) throws Exception {
		
		String repositoryDirectory = null;
		
		if (repositoryURL != null) {

			repositoryDirectory = Git.clone(repositoryURL);
		
			if (branchName != null) {
				Git.checkout(repositoryDirectory, branchName);
			}
			
			if (commitID != null) {
				Git.checkout(repositoryDirectory, commitID);
			}
		}
		
		return repositoryDirectory;
	}
	/**
	 * Set up the original and/or revised directories according to the provided arguments
	 * @throws Exception 
	 */
	public static void setUpDirectoriesThroughGit()  {
		try {
			if (Configuration.getOriginalDirectory() == null) {
				Configuration.setOriginalDirectory(setUpDirectoriesThroughGit(Configuration.getOriginalRepositoryURL(), Configuration.getOriginalBranchName(), Configuration.getOriginalCommitID()));
			}
			
			if (Configuration.getRevisedDirectory() == null) {
				Configuration.setRevisedDirectory(setUpDirectoriesThroughGit(Configuration.getRevisedRepositoryURL(), Configuration.getRevisedBranchName(), Configuration.getRevisedCommitID()));
			}
		} catch(Exception e) {
			e.printStackTrace();
			Main.printLine("[Error] Error setting up directory through git");
			System.exit(OperiasStatus.INVALID_ARGUMENTS.ordinal());
		}
	}
	
	/**
	 * @return the destinationDirectory
	 */
	public static String getDestinationDirectory() {
		return destinationDirectory;
	}

	/**
	 * @param destinationDirectory the destinationDirectory to set
	 */
	public static void setDestinationDirectory(String destinationDirectory) {
		Configuration.destinationDirectory = destinationDirectory;
	}

	

	/**
	 * @return the originalCommitID
	 */
	public static String getOriginalCommitID() {
		return originalCommitID;
	}

	/**
	 * @param originalCommitID the originalCommitID to set
	 */
	public static void setOriginalCommitID(String originalCommitID) {
		Configuration.originalCommitID = originalCommitID;
	}

	/**
	 * @return the revisedCommitID
	 */
	public static String getRevisedCommitID() {
		return revisedCommitID;
	}

	/**
	 * @param revisedCommitID the revisedCommitID to set
	 */
	public static void setRevisedCommitID(String revisedCommitID) {
		Configuration.revisedCommitID = revisedCommitID;
	}

	

	

	/**
	 * @return the temporaryDirectory
	 */
	public static String getTemporaryDirectory() {
		return temporaryDirectory;
	}

	/**
	 * @param temporaryDirectory the temporaryDirectory to set
	 */
	public static void setTemporaryDirectory(String temporaryDirectory) {
		Configuration.temporaryDirectory = temporaryDirectory;
	}

	/**
	 * @return the originalBranchName
	 */
	public static String getOriginalBranchName() {
		return originalBranchName;
	}

	/**
	 * @param originalBranchName the originalBranchName to set
	 */
	public static void setOriginalBranchName(String originalBranchName) {
		Configuration.originalBranchName = originalBranchName;
	}

	/**
	 * @return the revisedBranchName
	 */
	public static String getRevisedBranchName() {
		return revisedBranchName;
	}

	/**
	 * @param revisedBranchName the revisedBranchName to set
	 */
	public static void setRevisedBranchName(String revisedBranchName) {
		Configuration.revisedBranchName = revisedBranchName;
	}

	/**
	 * @return the originalRepositoryURL
	 */
	public static String getOriginalRepositoryURL() {
		return originalRepositoryURL;
	}

	/**
	 * @param originalRepositoryURL the originalRepositoryURL to set
	 */
	public static void setOriginalRepositoryURL(String originalRepositoryURL) {
		Configuration.originalRepositoryURL = originalRepositoryURL;
	}

	/**
	 * @return the revisedRepositoryURL
	 */
	public static String getRevisedRepositoryURL() {
		return revisedRepositoryURL;
	}

	/**
	 * @param revisedRepositoryURL the revisedRepositoryURL to set
	 */
	public static void setRevisedRepositoryURL(String revisedRepositoryURL) {
		Configuration.revisedRepositoryURL = revisedRepositoryURL;
	}

	/**
	 * @return the outputEnabled
	 */
	public static boolean isOutputEnabled() {
		return outputEnabled;
	}

	/**
	 * @param outputEnabled the outputEnabled to set
	 */
	public static void setOutputEnabled(boolean outputEnabled) {
		Configuration.outputEnabled = outputEnabled;
	}


	private static void printInstructions() {
		System.out.println(USAGE_INSTRUCTIONS);
	}
}
