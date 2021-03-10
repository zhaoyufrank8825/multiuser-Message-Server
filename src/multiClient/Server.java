/*
 *  zhaoyu
 *  1001450700
*/

//    https://www.youtube.com/watch?v=cRfsUrU3RjE&t=1211s
//    https://github.com/abhi195/Chat-Server/tree/master/src


package multiClient;
import java.io.*;
import java.net.*;
import java.util.*;

/*
 * the server class to handle all the connection from client, deal with the input and output, 
 * get message from input, and send to the related client
 */

public class Server {
	// This is the GUI for show all the log in the server
	private ArrayList<ClientThread> clientThreads;
	// this two array lists are used to store the name of online user and all the user name used before
	private ArrayList<String> allUserName;
	private ArrayList<String> onlineUserName;
	// the port number and the server GUI
	private int port;
	private ServerMessagePane logwin;
	// Every clients need an ID, which can not be over write
	private static int UID;
	private ServerSocket serverSocket;

	public Server(int port) {
		this.port = port;
		clientThreads = new ArrayList<ClientThread>();
		allUserName = new ArrayList<>();
		onlineUserName = new ArrayList<>();
	}
	
/*
 * Start the server, when server is on, keeping it always work, until close it by force.
 * Accept connecting request from client, and give every client a thread.
 * Open the server GUI to store all the log in the server.
 */
	public void start() {
		logwin = new ServerMessagePane();
		logwin.setVisible(true);
		
//		showMsg("Instruction:");
//		showMsg("1. Type '@username message' for Unicast");
//		showMsg("2. Type '#username,username message' for Muticast");
//		showMsg("3. Type 'message' for Broadcast");
//		showMsg("4. Type 'WHOISIN' to list of active clients");
//		showMsg("5. Type 'LOGOUT' to logoff from server");
//		showMsg("6. Type 'ALLUSER' to list all names used");
//		showMsg("============================================");
		
		try {
			
			//create the server socket to receive request from client
			serverSocket = new ServerSocket(port);
			while(true) {
				showMsg("Server waiting for Clients on port " + port + ".");
				
				// connecting to the client socket, and store the socket thread 
				Socket socket = serverSocket.accept();
				ClientThread t = new ClientThread(socket);
				clientThreads.add(t);
				t.start();

				// if the online user is three, sending notify message to all client, then start the test.
				if(onlineUserName.size() == 3 ) {
					onlineUserName.clear();
					onlineUserName.add("A");
					onlineUserName.add("B");
					onlineUserName.add("C");
					logwin.onlineArea.setText("");
					for(String name: onlineUserName) {
						logwin.onlineArea.append(name+"\n");
					}
					sendMessage("Three clients, Test is ready to go.");
				}
			}  
		}
		
		// handling the error message, show it in the server GUI
		catch (IOException e) { showMsg("Opening new ServerSocket failed: " + e); }
	}

/*
 * Show all the information to the GUI, and show in the console
 */
	private void showMsg(String msg) {
		logwin.textArea.append(msg+"\n");
		System.out.println(msg);
	}

/*
 * This function is used to send message to the client, all three cases all handle in here
 * # for multiply cast for loop to send to all related clients, @ for unique cast send to unique clients, 
 * the multiply cast just repeat the unique cast, and the broadcast and multiply logic is the same.
 */
	private boolean sendMessage(String message) {
		String[] tokens = message.split(" ",3);
		if(tokens.length >= 2 ) {
			
			// multiply cast, split users into a set, and then do the Unicast for every user
			// iterate through the whole online user, check if the name is the same with receiver
			// if the same, send the message, else ignore
			if(tokens[1].charAt(0)=='#') {
				
				//split the message to get the receiver name, and store in a set
				String[] tochecks=tokens[1].substring(1, tokens[1].length()).split(",");
				HashSet<String> set = new HashSet<>(Arrays.asList(tochecks));
				String newMsg = tokens[0]+tokens[2];
				
				//iterate the online user to get the correct receiver, get the user name
				for(int i=0; i<clientThreads.size(); i++){
					ClientThread thread=clientThreads.get(i);
					String check=thread.username;
					if(set.contains(check)) {
						if(!thread.writeMsg(newMsg)) {
							
							// if write message is fail, move the thread, and send notice message to every one
							clientThreads.remove(i);
							onlineUserName.remove(thread.username);
							showMsg("Disconnected Client " + thread.username + " removed from list.");
						}
						
						// just show the message and then print the message to the server GUI
						showMsg(newMsg);
					}
				}
			} 
			// Unicast, split the message to get the user name
			// iterate through the whole online user, check if the name is the same with receiver
			// if the same, send the message, else ignore
			else if(tokens[1].charAt(0)=='@') {
				String tocheck=tokens[1].substring(1, tokens[1].length());
				String newMsg = tokens[0]+" -> "+tocheck+" "+tokens[2];
				boolean found=false;
				
				//iterate through the online user, find the thread match with the user name, and send the message
				for(int i=0; i<clientThreads.size(); i++) {
					ClientThread thread=clientThreads.get(i);
					String check=thread.username;
					if(check.equals(tocheck)) {
						if(!thread.writeMsg(newMsg)) {
							
							// if write message is fail, move the thread, and send notice message to every one
							clientThreads.remove(i);
							onlineUserName.remove(thread.username);
							showMsg("Disconnected Client " + thread.username + " removed from list.");
						}
						
						// just show the message and then print the message to the server GUI
						showMsg(newMsg);
						found=true; break;
					}
				}
				if(found!=true)  return false; 
			} 
			//broad cast, send message to everyone, including himself
			//broad cast don't have the name to match, so, we need to handle the split message carefully
			//just iterate the whole online user list, and send message to them
			else {
				showMsg(message);
				
				//iterate through the online user, and send message to every one
				for(int i=0; i<clientThreads.size(); i++) {
					ClientThread thread = clientThreads.get(i);
					if(!thread.writeMsg(message) ) {
						
						// if write message is fail, move the thread, and send notice message to every one
						clientThreads.remove(i);
						onlineUserName.remove(thread.username);
						showMsg("Disconnected Client " + thread.username + " removed from list.");
					}
				}
			}
		}
		
		//broad cast, send message to everyone, including himself
		//broad cast don't have the name to match, so, we need to handle the split message carefully
		//just iterate the whole online user list, and send message to them
		else{
			showMsg(message);
			//iterate through the online user, and send message to every one
			for(int i=0; i<clientThreads.size(); i++) {
				ClientThread thread = clientThreads.get(i);
				if(!thread.writeMsg(message) ) {
					
					// if write message is fail, move the thread, and send notice message to every one
					clientThreads.remove(i);
					onlineUserName.remove(thread.username);
					showMsg("Disconnected Client " + thread.username + " removed from list.");
				}
			}
		}

		return true;
	}

/* 
 * disconnect the client with server. Find the client by the unique ID
 */
	public void disconnect(int id) {
		
		//iterate the online user list, to get the unique ID match with the parameter id
		for(int i = 0; i < clientThreads.size(); ++i) {
			ClientThread thread = clientThreads.get(i);
			if(thread.id == id) {
	
				// remove the client thread from the server
				clientThreads.remove(i);
				
				// remove the thread information from online list, and iterate new online list
				// to show the new online list name
				onlineUserName.remove(thread.username);
				logwin.onlineArea.setText("");
				for(String name: onlineUserName) {
					logwin.onlineArea.append(name+"\n");
				}
				break;
			}
		}
		
		// broad cast the notice information to every one online
		// sendMessage( discon + " has left the chat room.");
	}
	
/*
 * main function, connect to the server, and then start to receiver request from user
 */
	public static void main(String[] args) {
		Server server = new Server(8188);	
		server.start();
	}

 /*
 * Every client will have a thread working with it. 
 * Input and output stream to communicate with server and client.
 * For some special case, the message is some action.
 * we need to handle it separately.
 */
	class ClientThread extends Thread {
		Socket socket;
		// the input and output to handle to information between the server and client
		ObjectInputStream inPut;
		ObjectOutputStream outPut;
		String username;
		// handle the message, because we have may special cases
		ChatMessage chatMsg;
		int id;

		public ClientThread(Socket socket) {
			// Every clients need an ID, which can not be over write
			id = UID++;
			this.socket = socket;
			try {
				
				// create the input and output for every client, and read the user name from the first input
				outPut = new ObjectOutputStream(socket.getOutputStream());
				inPut  = new ObjectInputStream(socket.getInputStream());
				username = (String) inPut.readObject();
				
				// take care of online user name conflicting case, 
				// if the conflict is happen, send message to client, and stop the connection
				if( onlineUserName.contains(username) ) {
					showMsg("User name conflict error, Input a new name.");
					outPut.writeObject("Error");
					return;
				}
				
				// if login success, collect the name to all user and online user, 
				// check if the client name has been used before, if so, no need renew the all user name list
				// else renew the all user name list
				if( !allUserName.contains(username) ) {
					allUserName.add(username);
					logwin.allArea.append(username+"\n");
				}
				
				// store the name to online user, show the name on the server GUI
				// and broad cast the notice information to every online client
				onlineUserName.add(username);
				logwin.onlineArea.append(username+"\n");
				outPut.writeObject("Login Success");
				sendMessage(username + " has joined the chat room.");
			}
			// handle the try/catch error information
			catch (IOException | ClassNotFoundException e) {
				showMsg("Creating new Input/output Streams error: " + e);
				return;
			}
		}

/*
 * run the thread, and take care of special case
 * and the send the message to the related client
 */
		public void run() {
			boolean serverWorking = true;
			while(serverWorking) {
				
				// read the message from input, and send error message if reading action is wrong
				try { chatMsg = (ChatMessage) inPut.readObject(); }
				catch (IOException | ClassNotFoundException e) { break; }
				
				String message = chatMsg.getMessage();
				switch(chatMsg.getType()) {
				
					// case 1: normal message, put the user name in front of message, make sure the receiver 
					// know where the message is from
					case ChatMessage.MESSAGE:
						boolean confirmation =  sendMessage(username + " " + message);
						if(confirmation==false){ writeMsg("Sorry. No such user exists."); }
						break;
						
					// case 2: logout action, disconnect with the server, and stop the working
					// broad cast the notice to every online user
					case ChatMessage.LOGOUT:
						sendMessage(username + " disconnected with a LOGOUT message.");
						serverWorking = false;
						break;
						
					// case 3: who is in action, check which user is online, and send the information to 
					// the client who want to know this
					case ChatMessage.WHOISIN:
						writeMsg("List of the users connected at: ");
						for(int i = 0; i < clientThreads.size(); ++i) 
							writeMsg((i+1) + ") " + clientThreads.get(i).username );
						break;
						
					// case 4: all user action, check which all user name used before, and send the information to 
					// the client who want to know this
					case ChatMessage.ALLUSER:
						writeMsg("List of the used name: ");
						for(int i = 0; i < allUserName.size(); ++i) 
							writeMsg((i+1) + ") " + allUserName.get(i) );
						break;
				}
			}
			disconnect(id);  
		}

/*
 * The function through socket to send message to the client
 */
		private boolean writeMsg(String msg) {
			
			// check if the socket is connected correctly
			if(!socket.isConnected()) return false; 
			
			// send out the message to client, if sending action is wrong, handle the error 
			// by print out the error message
			try { outPut.writeObject(msg); }
			catch(IOException e) { showMsg("Error sending message to " + username + e);}
			return true;
		}
	}
}

