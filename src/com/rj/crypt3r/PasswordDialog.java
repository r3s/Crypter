package com.rj.crypt3r;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class PasswordDialog extends JDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ok=0;
	private String passwd;
	
	/*Constructor*/
	public PasswordDialog() {
		ok=0;
		passwd=null;
		initUI();
	}
	
	/*Functions to get OK (if user pressed ok) and to get the password */
	public int getOK(){
		return this.ok;
	}
	
	public String getPass(){
		return this.passwd;
	}
	
	/*Initialise UI*/
	public void initUI(){
		setLayout(null);
		JLabel passwdText = new JLabel("Password :");
		final JPasswordField passwdField = new JPasswordField();
		JButton passwdOkButton = new JButton("OK");
		JButton passwdCancelButton = new JButton("Cancel");
		
		passwdText.setBounds(2,10,80,30);
		passwdField.setBounds(85,10,150,30);
		passwdOkButton.setBounds(240,10,80,30);
		passwdCancelButton.setBounds(325,10,100,30);
		
		/*Action listeners for the buttons and also when pressing enter in the password field*/
		passwdOkButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				/*If user pressed ok, then set ok=1, and password=text from textfield*/
				ok=1;	
				passwd= new String(passwdField.getPassword());
				
				dispose();
			}
		});
		
		passwdCancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				/*If user pressed cancel, then dispose the dialog, setting ok=0*/
				ok=0;
				dispose();
				
			}
		});
		
		passwdField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				/*If user pressed ok, then set ok=1, and password=text from textfield*/
				ok=1;	
				passwd= new String(passwdField.getPassword());
				
				dispose();
				
			}
		});
		
		
		/*Add all components to UI and pack the dialog*/
		add(passwdText);
		add(passwdField);
		add(passwdOkButton);
		add(passwdCancelButton);
		pack();
		setModalityType(ModalityType.APPLICATION_MODAL);

        setTitle("Enter Password");
        setResizable(false);
        
       // setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //setLocationRelativeTo(Crypter.jp);
        setSize(430, 50);
	}

}
