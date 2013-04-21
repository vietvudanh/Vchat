/*
 * author: vietvd
 * Class User for Vchat
 * on server side, store clients information
 */

package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class User {
	
	/*
	 * online name 
	 */
	private String name;
	
	/*
	 * socket
	 */
	private Socket socket;
	
	/*
	 * output stream
	 */
	private DataOutputStream dout;
	
	/*
	 * Constructor
	 * @param: name & socket
	 */
	public User(String name, Socket socket){
		this.socket = socket;
		this.name = name;
		try {
			dout = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//end Constructor
	
	/*
	 * accessors and mutators
	 */
	public void setName(String name){
		this.name = name;
	}
	
	public void setSocket(Socket socket){
		this.socket = socket;
		try {
			dout = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getName(){
		return name;
	}
	
	public Socket getSocket(){
		return socket;
	}
	
	public DataOutputStream getOutputStream(){
		return dout;
	}

}//end class User
