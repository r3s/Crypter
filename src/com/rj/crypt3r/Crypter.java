package com.rj.crypt3r;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;



public class Crypter extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* Some variables, constants */
	final static JPanel jp= new JPanel();
	JLabel info=new JLabel();
	String helpmsg="1.Choose an option , Encrypt or Decrypt.(default is encrypt)\n\r2.Drag and drop files\n\r3.Enter Password when Prompted\n\r4.Check the folder to see a new encrypted file.\n\r5.Choose File->Exit to exit the app";
	String aboutmsg="Created by res";
	int op=1;
	static int MAX_SIZE=20971520;
	
	/*Basic Constructor*/
	Crypter(){
	}
	
	/*Constructor when used using console(terminal) */
	public void cmdlineProcess(String[] files,String pass,int action){
		
		/*Check if action is valid (1=enc, 2=dec)*/
		if(action==1||action==2){
			
			/*Loop through filenames */
			for(int i=0;i<files.length;i++){
				
				/*Try each filename and check if the file is valid*/
				File f=new File(files[i]);
				if(f!=null && f.exists()){
					
					/*Since it's valid, set operation as action and call encrptFileProcess*/
					setop(action);
					int res=encryptFileProcess(f, pass);
					
					/*Check if any error occured and print error message if required*/
					if(res!=0)
						errorMessageConsole("Process failed for "+files[i]+"\n");
					else
						errorMessageConsole("Finished processing : "+files[i]+"\n");
				}
				else
					errorMessageConsole("\nUnable to open "+files[i]+"\n");
			}
		}
		else{
			errorMessageConsole("\nWrong parameters.\n");
			System.exit(2);
		}
		
	}
	

	/*Functions to show error dialogues */
	public static void errorMessage(String msg){
		JOptionPane.showMessageDialog(jp, "Message: "+msg);
		
	}
	
	public static void errorMessageConsole(String msg){
		System.out.println("Message: "+msg);
	}
	
	/* Functions used to display debug messages. Not used in release */
	public static void debugMsg(String msg){
		JOptionPane.showMessageDialog(jp,msg );
	}
	public static void debugMsgConsole(String msg){
		System.out.println("Debug: "+msg);
	}
	
	/*Function to mix strings together*/
	public String mixStrings(String a, String b){
		return (a+b);
	}
	
	
	/* 2 functions, to set the option and get it */
	public void setop(int val){
		op=val;
	}
	public int getop(){
		return op;
	}
	
	/*AES encryption of byte array */
	byte[] doAES(byte[] ba,String pass) throws Exception, NoSuchPaddingException{
		
		/* A simple password option with randoma salt. Permanent salts are not good */
		byte[] key;
		String random="ASDF#$%^&*GHKASDftuaygushdn1234568";
		String mixed=mixStrings(pass, random);
		
		/*If for some reason, mixed is null, use just the random string as pass*/
		if(mixed!=null)
			 key = mixed.getBytes();
		else
			 key = random.getBytes();
		/*Get 128 byte key from SHA-1 hash */
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16);
		
		/*Initialise AES */
		Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec k = new SecretKeySpec(key, "AES");
		
		/* Check whether to do encryption/decrytpion. 1=encryption, 2=decryption*/
		if(getop()==1)
			c.init(Cipher.ENCRYPT_MODE, k);
		else if(getop()==2)
			c.init(Cipher.DECRYPT_MODE, k);
		
		/*get the (en/de)crypted array */
		byte[] crypted = c.doFinal(ba);

		/*return the byte array */
		return crypted;
	}
	
	
	/*Function to read file */
	public byte[] getFile(String fname) throws Exception{
		
		/*FileInputStream and FileChannel for performance. */
		FileInputStream in = new FileInputStream( fname );
		FileChannel ch = in.getChannel( );
		MappedByteBuffer mb = ch.map( FileChannel.MapMode.READ_ONLY,
		    0L, ch.size( ) );
		long l= (new File(fname)).length();
		
		/*Currently, supported max size is 20MB*/
		if(l>MAX_SIZE){
			//errorMessage("File size too large. Max file size allowed is"+(Integer.MAX_VALUE/1000)+"KB");
			return null;
		}
		
		byte[] barray = new byte[(int) l];
		int nGet;
		/*Read the file in to barray*/
		while( mb.hasRemaining( ) )
		{
		    nGet = Math.min( mb.remaining( ),Integer.MAX_VALUE );
		    mb.get( barray, 0, nGet );
		
		}
		if(in!=null)
			in.close();
		
		/*Return barray*/
		return barray;
	}
	
	/*Function to write to file*/
	public void writeFile(String fname,byte[] barray) throws Exception{
		
		/*Writing directly using Fileoutputstream*/
		FileOutputStream out=new FileOutputStream(fname);
		out.write(barray); 
		if(out!=null)
			out.close();
	}	
	
	/*Function to choose filename and call getfile/writefile resp.*/
	public int cryptFile(String filename,String pass,long length) throws Exception{
		
			String encname = null;
			
			byte[] barray=null;
			try{
			barray=getFile(filename);
			
			/*If barray is null, return 3, for fun? */
			if(barray==null){
				return 3;
			}
			}
			catch(Exception e){
				e.printStackTrace();
				errorMessage(" while reading file");
				return -1;
			}
			
			/*If option=1, do encryption*/
			if(getop()==1){
				encname=filename+".enc";
			}
			/*If option=2, do decryption after recreating the filename*/
			else if(getop()==2){
				int start = filename.indexOf(".");
				int end = start+4;
				String ext = filename.substring(start,end);
				filename = filename.substring(0,start);
				encname=filename+ext;
				//If the recreated filename already exists, change the name
				File f=new File(encname);
				if(f.exists())
					encname = filename+"_decrypted"+ext;
			}
			try{barray=doAES(barray,pass);}
			catch(Exception e){e.printStackTrace();}
			
			/*Finally write the file back*/
			try{
			writeFile(encname,barray);
			}
			catch(Exception e){
				errorMessage(" while writing file");
				e.printStackTrace();
				return -1;
			}
			
			return 0;
	}
	
	/*Un-needed function. Going to remove it in the future*/
	public int encryptFileProcess(File file, String pass){

			
            long length=file.length();
            try {
            	
				int res=cryptFile(file.getAbsolutePath(),pass,length);
				if(res!=0 && res!=3){
					errorMessage(" with the cryptfile function");
					return -1;
				}
				
				if(res==3)
					return 3;
				
				
			} catch (Exception e) {
				e.printStackTrace();
				errorMessage(" with the cryptfile function");
				return -1;
			}
            
        	return 0;
	}
	
	/*Initialise UI*/
	public void initUI(){
		getContentPane().add(jp);
		jp.setLayout(null);
		
		/*A menu bar- with File ->Help,Exit*/
		JMenuBar menubar=new JMenuBar();
		JMenu file= new JMenu("File");
		JMenuItem exit=new JMenuItem("Exit");
		JMenuItem help=new JMenuItem("Help");
		
		/*Ctrl+F for file, Ctrl+X for exit, Ctrl+H for help*/
		file.setMnemonic(KeyEvent.VK_F);
		exit.setMnemonic(KeyEvent.VK_X);
		help.setMnemonic(KeyEvent.VK_H);
		exit.setToolTipText("Exit application");
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
            ActionEvent.CTRL_MASK));
        help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,ActionEvent.CTRL_MASK));
        
        /*ActionListeners for exit and help*/
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }

        });
        
        help.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(jp,helpmsg);
				
			}
        	
        	
        });
		/*Add exit and help to file*/
        file.add(help);
		file.add(exit);
		/*Add file to menubar*/
		menubar.add(file);
		/*Set the menubar*/
		setJMenuBar(menubar);
		
		/*Add radiobuttons to choose encryption and decryption*/
		JRadioButton encrypt=new JRadioButton("Encrypt");
		encrypt.setBounds(2, 2,100, 30);
		
		JRadioButton decrypt=new JRadioButton("Decrypt");
		decrypt.setBounds(135,2,100,30);
		
		/*Actionlisteners. If encrypt is selected, set op=1, if decrypt is selected, set op=2*/
		encrypt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				setop(1);
				
			}
		});
		decrypt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setop(2);
			}
		});
		/*Add encrypt and decrypt to a buttongroup.*/
		ButtonGroup group=new ButtonGroup();
		group.add(encrypt);
		group.add(decrypt);
		
		/*Add the radiobuttons to the panel*/
		jp.add(encrypt);
		jp.add(decrypt);
		/*Set the default selected as encrypted*/
		encrypt.setSelected(true);
		
		/*File drop listener*/
		new FileDrop(jp,new FileDrop.Listener()
        {   
			
			public void filesDropped( java.io.File[] files )
			{   
				setAlwaysOnTop(false);
        		setEnabled(false);
        		PasswordDialog pwdDlg=new PasswordDialog();
        		pwdDlg.setLocationRelativeTo(jp);
        		pwdDlg.setVisible(true);
        		pwdDlg.setAlwaysOnTop(true);
        		setEnabled(true);
        		setAlwaysOnTop(true);
        		
			for( int i = 0; i < files.length; i++ )
            { 
        	
        		
        		if(pwdDlg.getOK()==1){
    			
        			/*Start the process by calling encryptFileProccess function*/
        			int res=encryptFileProcess(files[i],pwdDlg.getPass());
        			info.setText("Working  on files...");
        			if(res==0){
        					if(getop()==1)
        						info.setText((i+1)+" File(s) encrypted");
        					else if(getop()==2)
        						info.setText((i+1)+" File(s) Decrypted");
        			}
        			else if(res==3){
        					info.setText("Too big to handle!");
        			}
    			
        		}
        		
        		
            	}   
        	}   
        }); 

		/*Info, A label to display status*/
		info.setBounds(80,80, 180, 30);
		info.setText("Drag and drop files here");
		jp.add(info);
		
		/*Set the window to be non-resizeable and always on top, and other features*/
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("CryptIt");
		setSize(320,240);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/*MAIN*/
	public static void main(String[] args){
		
		/*Check for commandline arguments*/
		if(args.length>1){
			
			ArgumentParser parser=new ArgumentParser();
			parser.parseArguments(args);
		}
		else{

			/*Set the UI to GTK if available */
			try {
		        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");                
		
		        } catch (Exception e) {
		        System.out.println("Error creating GTK UI");
		        }
		        
			/*Run the app*/
			SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	Crypter cr=new Crypter();
	            	cr.initUI();
	            	cr.setVisible(true);
	            	}
				});
		}
	}
}