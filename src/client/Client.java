/*
 * author: vietvd
 * Client class for Vchat
 */

package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements Runnable{

	/*
	 * socket to servet
	 */
	private Socket socket;
	
	/*
	 * Streams for Input and Output
	 */
	private DataInputStream din;
	private DataOutputStream dout;
	
	/*
	 * onlineName
	 */
	private String onlineName;
	
	/*
	 * GUI attributes
	 */
	private JTextField tfTop;
	private JTextArea taCenter;
	private JTextArea taWest;
	private JTextField tfBottom;
	
	/*
	 * Constructor
	 * @param: host IP and port
	 */
	public Client(String host, int port){
		
		try {
			
			//get online name
			onlineName = JOptionPane.showInputDialog("What is your name?");
			
			//create connection to server
			this.socket = new Socket(host, port);
			
			//promt
			System.out.println("Connected to " + socket);
			
			//streams
			din = new DataInputStream(socket.getInputStream());
			dout = new DataOutputStream(socket.getOutputStream());
			
			//send name to server
			dout.writeUTF(onlineName);
			
		}
		catch( UnknownHostException uhe){
			uhe.printStackTrace();		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * Init GUI
		 */
		//set frame's name
		setTitle("Vchat - " + onlineName);
		
		
		//layout
		setLayout(new BorderLayout() );
		
		//set size
		setSize(640, 480);
		
		//move window to center of window
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		setLocation(x,y);
		
		//set resizable
		setResizable(false);
		
		//set default close operation
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		/*
		 * add components to frame
		 */
		
		//top, online name
		tfTop = new JTextField("onlineName::" + onlineName);
		tfTop.setEditable(false);
		add("North", tfTop);
		
		//center, chat content
		taCenter = new JTextArea();
		taCenter.setEditable(false);
		add("Center", taCenter);
		
		//left, online list
		taWest = new JTextArea();
		add("West", taWest);
		taWest.setEditable(false);
		taWest.setSize(120, 480);
		
		//bottom, input
		tfBottom = new JTextField();
		add("South", tfBottom);
		tfBottom.requestFocus();
		
		tfBottom.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				processMessage(ae.getActionCommand());
			}
		});
		
		//add menu bar
		JMenuBar menu = new JMenuBar();
		setJMenuBar(menu);
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");
		menu.add(file);
		JMenuItem changeName = new JMenuItem("Change online name");
		file.add(changeName);
		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);
		JMenuItem about = new JMenuItem("About");
		help.add(about);
		
		exit.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				System.exit(0);		
			}
		});
		
		changeName.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				changeName();
			}
		});
		
		
		
		//All done, set visible
		repaint();
		revalidate();
		setVisible(true);
		
		//all GUI and connection is ready
		//start Thread for receiving message
		new Thread(this).start();
		
		
	}//end Constructor

	/*
	 * run
	 * automatically called when start() is called
	 * process incoming message, one by one
	 */
	public void run() {
		//receive message
		try {
			while(true){
				
				//receive
				String message = din.readUTF();
				
				String[] result = message.split(";");
				
				String msName = result[0];
				String msType = result[1];
				String msContent = result[2];
				
				/*
				 * process cases 
				 */
				switch(msType){
					
					//normal chat message
					case "CHAT":
						//show on windows
						taCenter.append(msName + "::" + msContent);
						break;
					
					//change name request	
					case "ONLINELIST":
						taWest.setText(msContent);
						break;
						
					//default
					default:
						break;
				}
				
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * processMessage
	 * prepare content and send to server
	 * @param: message to be processed
	 */
	public void processMessage(String message){
		
		//result String to be send to server
		String result = null;
		
		//add online name
		result = onlineName + ";CHAT;" + message + "\n";
		
		//send to server
		try {
			dout.writeUTF(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//reset input field
		tfBottom.setText("");
	}
	
	/*
	 * main
	 * @param: host and port
	 * usage: java Client <host> <port>
	 */
	public static void main(String[] args){
		
		//tale command line arguments
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		//create new instance
		new Client(host, port);
		
	}
	
	/*
	 * changeName
	 * send request to server to change current name
	 * @param: name to be change
	 */
	private void changeName(){
		//get name to be changed
		String name = JOptionPane.showInputDialog("Type in the name you want to change");
		
		//re-confirm
		int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to\nchange your name from \"" 
						+ onlineName + "\" to \"" + name + "\"");
		if( confirm == 1){
			return;
		}
		else{
			//update window title and top field
			onlineName = name;
			tfTop.setText("onlineName:: " + onlineName);
			setTitle("Vchat - " + onlineName);
			this.repaint();
			this.revalidate();
			
			//promt
			System.out.println("Changing name");
			
			//prepare and send message to server
			String message = onlineName + ";CHANGENAME;" + name;
			try {
				dout.writeUTF(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}// end changeName()
	
}//end class Client
