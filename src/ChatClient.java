import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame implements ActionListener, Runnable{
	JTextField t;
	JTextArea ta;
	JButton button1,button2,button3;
	DataObject1 d;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Socket socket;
	String name;

	
	public ChatClient(){
		
		String stringName = (String)JOptionPane.showInputDialog(
                    this,
                    "You can Enter your name here:",
                    "Username",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
		System.out.println(stringName);

		name = stringName;
		setTitle(name);
		setSize(500,500);
		addWindowListener(new WindowAdapter(){

			public void windowClosing(WindowEvent e){
				DataObject1 tempObject = new DataObject1();
				tempObject.setName(name);
				tempObject.setMessage("Has Exited.");
				try{	
					oos.writeObject(tempObject);
				}catch(IOException ioe){}
				System.exit(0);
			}
		});
		
		t = new JTextField();
		t.addActionListener(this);
		ta = new JTextArea();
		JPanel p = new JPanel();
		add(p,BorderLayout.SOUTH);
		getContentPane().add(ta, BorderLayout.CENTER);
		getContentPane().add(t, BorderLayout.NORTH);
		add(p,BorderLayout.SOUTH);
		p.setLayout(new GridLayout(1,3));
		button1=new JButton("theme3");
		button1.addActionListener(this);
		button2=new JButton("theme2");
		button2.addActionListener(this);
		button3=new JButton("theme1");
		button3.addActionListener(this);
		p.add(button1);
		p.add(button2);
		p.add(button3);
		try{
			socket = new Socket("localhost", 4444);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			DataObject1 tempObject = new DataObject1();
			tempObject.setName(name);
			tempObject.setMessage("Has Entered.");	
			oos.writeObject(tempObject);
		}catch(UnknownHostException uhe){System.out.println("Bad URL");
		}catch(IOException ioe){ System.out.println("IO Exception");
		}		
		Thread thread = new Thread(this, "");
		thread.start();	


		setVisible(true);
		t.requestFocus();
	}

	public void run(){
		
		boolean done = false;
		while(!done){

			try{
				DataObject1 inObject = (DataObject1)ois.readObject();
				ta.append(inObject.getName()+": "+inObject.getMessage()+ "\n");
			}catch(IOException ioe){
				done = true;
				System.exit(0);
			}catch(ClassNotFoundException cnfe){
				System.out.println("You forgot to copy DataObject here");
			}

		}

	}
	
	public void actionPerformed(ActionEvent ae){
			Object source=ae.getSource();
		try{
			//ta.append(t.getText());
			d = new DataObject1();
			d.setMessage(t.getText().trim());
			d.setName(name);
			if(d.getMessage().equals("bye")){
				DataObject1 tempObject = new DataObject1();
				tempObject.setName(name);
				tempObject.setMessage(" quit");	
				oos.writeObject(tempObject);
				System.exit(0);
			}
			if(d.getMessage().equals(":)")){
				DataObject1 tempObject = new DataObject1();
				tempObject.setName(name);
				tempObject.setMessage("I smiled!");	
				oos.writeObject(tempObject);
			}
			oos.writeObject(d);
			//oos.reset();
			t.setText("");
		}catch(IOException ioe){
			System.out.println("Problem sending a message to server");	}
			String plaf="";
			if(source==button1)
			{
			plaf="javax.swing.plaf.metal.MetalLookAndFeel";
			}
			else if(source==button2)
			{
			plaf="com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			}
			else if(source==button3)
			{
			plaf="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			}

			try
			{
			UIManager.setLookAndFeel(plaf);
			SwingUtilities.updateComponentTreeUI(this);
			}
			catch(Exception e){}
	}

		

	public static void main(String[] args){
		
			ChatClient c = new ChatClient();
		

	}
}
