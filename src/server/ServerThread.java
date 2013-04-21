/*
 * author: vietvd
 * ServerThread class for Vchat
 */

package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ServerThread extends Thread {

	/*
	 * Sockets
	 */
	//reference to server
	private Server server;
	
	//socket to client
	private Socket socket;
	
	/*
	 * Constructor
	 * @param: reference to server and socket to client
	 */
	public ServerThread(Server server, Socket socket){
		
		//assign
		this.server = server;
		this.socket = socket;
		
		//start
		start();
	}//end Constructor
	
	/*
	 * run, automatically called when start() is called in Constructor
	 */
	public void run(){
		
		try {
			while(true){
				//input stream to receive data from client
				//client use DataOutputStream to send to this
				DataInputStream din = new DataInputStream(socket.getInputStream());
				
				//next message
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
						server.sendToAll(message);
						break;
					
					//change name request	
					case "CHANGENAME":
						server.changeName(msName);
						break;
						
					//default
					default:
						break;
				}
			}
		} 
		catch(SocketException se){
			se.printStackTrace();
		}
		catch(EOFException eof){
			eof.printStackTrace();
		}
		catch (IOException ie) {
			ie.printStackTrace();
		}
		finally{
			//connection close for some reasons
			//close it
			server.removeConnection(socket);
		}
		
	}//end run()
	
}//end class ServerThread
