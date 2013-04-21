/*
 * author: vietvd
 * Server class for chatroom software
 * run with command line paramater: port to run
 * 
 */

package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	/*
	 * Socket
	 */
	private ServerSocket svSocket;
	
	/*
	 * list of live Socket
	 */
	ArrayList<User> userList;
	
	/*
	 * Constructor
	 * @param: port
	 */
	public Server(int port) throws IOException{
		//listen on this port
		svSocket = new ServerSocket(port);
		
		//promt
		System.out.println("Listening on " + svSocket);
		
		//init sockets list
		userList = new ArrayList<User>();
		
		//listen for connections
		listen();
	}
	
	/*
	 * listen for incoming connections
	 */
	public void listen() throws IOException{
		
		//listen for all incoming connections
		while(true){
			//grab the incoming connections
			Socket socket = svSocket.accept();
			
			//get name from client
			DataInputStream din = new DataInputStream(socket.getInputStream());
			String name = din.readUTF();
			
			//promt
			System.out.println("Connection from " + socket);
			
			//save connection
			userList.add(new User(name, socket));
			
			//prepare and send message to all client
			String message = "TOALL;ONLINELIST;Online list:\n";
			for(User temp : userList){
				message += " + " + temp.getName() + "\n";
			}
			sendToAll(message);
			
			//create new thread
			new ServerThread(this, socket);
			
		}
	}//end listen()
	
	/*
	 * sendToAll
	 * send message to all clients
	 * @param: message to send
	 */
	public void sendToAll(String message){
		
		//synchronize for avoid race between sendToAll() and removeConnection()
		synchronized(userList){
			for(User temp: userList){
				//new output stream
				DataOutputStream dout;
				try {
					dout = new DataOutputStream(temp.getOutputStream());
					dout.writeUTF(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}//end sendToAll()
	
	/*
	 * removeConnection
	 * remove a connection
	 * @param: socket of the connection to be removed
	 */
	public void removeConnection(Socket socket){
		
		//synchronize to avoid race between removeConnection() and sendToAll()
		synchronized (userList) {
			
			//promt
			System.out.println("\nClose connection: " + socket);
			
			int i = 0;
			for(i = 0; i < userList.size(); i++){
				if (userList.get(i).getSocket().equals(socket)){
					break;
				}
			}
			userList.remove(i);
			
			try{
				socket.close();
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}//end remove connection
	
	/*
	 * changeName
	 * change name of one user client
	 * @param: name to be changed
	 */
	public void changeName(String name){
		//pos of user to be changed
		int pos = 0;
		for( pos = 0; pos < userList.size(); pos ++){
			if( userList.get(pos).getName().equals(name))break;
		}
		
		//change
		userList.get(pos).setName(name);
		
		//prepare and send message to all client
		String message = "TOALL;ONLINELIST;Online list:";
		for(User temp : userList){
			message += " + " + temp.getName() + "\n";
		}
		sendToAll(message);
		
	}//end changeName()
	/*
	 * main
	 * @param: command line paramater String -> port
	 * usage: java Server <port>
	 */
	public static void main(String[] args){
		
		//take command line argument
		int port = Integer.parseInt(args[0]);
		
		//create new Server
		try {
			new Server(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//end main
}//end class Server
