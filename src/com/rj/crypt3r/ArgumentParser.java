package com.rj.crypt3r;

import jargs.gnu.CmdLineParser;

public class ArgumentParser {
	
	/*No need for constructor*/
	
	
	/*parseArguments function to parse the argument list */
	public void parseArguments(String args[]){
		/*Initialise action=0 (1=enc, 2=dec) , and the parser */
		int action=0;
		CmdLineParser parser=new CmdLineParser();
		
		/*Add options*/
		CmdLineParser.Option fileEncrypt=parser.addBooleanOption('e',"encrypt");
		CmdLineParser.Option fileDecrypt=parser.addBooleanOption('d',"decrypt");
		CmdLineParser.Option password=parser.addStringOption('p',"password");
		
		/*try-catch block to parse arguments*/
		try{
			parser.parse(args);
		}
		catch(CmdLineParser.OptionException e){
			System.out.println(e.getMessage());
			System.exit(2);
		}
		
		/*Get the parsed values*/
		Boolean encrypt=(Boolean)parser.getOptionValue(fileEncrypt);
		Boolean decrypt=(Boolean)parser.getOptionValue(fileDecrypt);
		String pass=(String)parser.getOptionValue(password);
		
		/*Check for options and if every required argument is present*/
		if(encrypt==null&&decrypt==null){
			System.out.println("\nPlease specify an action: -e for encryption, -d for decryption.\n");
			System.exit(2);
		}
		
		if(((encrypt==null)&& (decrypt==true))){
			action=2;
		}
		else if((( decrypt==null) && (encrypt==true))){
			action=1;
		}
		
		else if(encrypt==true && decrypt==true){
			System.out.println("\nPlease specify either encryption or decryption. \n");
			System.exit(2);
		}
		
		if(pass==null){
			System.out.println("\nPlease enter a password using the -p option.\n");
			System.exit(2);
		}
		
		
		
		try{
			/*Finally, get the list of filenames*/
			String[] fileList = parser.getRemainingArgs();
			
			/*If the file list is  empty, print error message and exit*/
			if(fileList==null||fileList.length==0){
				System.out.println("\nPlease specify file names\n");
				System.exit(2);
			}
			
				/*If file list is not empty, create a crypter object and start process*/
				Crypter c=new Crypter();
				c.cmdlineProcess(fileList, pass, action);
			
		}
		catch(Exception e){
			System.out.println("\nSome error occured\n");
			System.exit(2);
		}
	}

}
