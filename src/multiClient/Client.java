/*
 *  zhaoyu
 *  1001450700
*/

//    https://www.youtube.com/watch?v=cRfsUrU3RjE&t=1211s
//    https://github.com/abhi195/Chat-Server/tree/master/src


package multiClient;
import java.net.*;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.JOptionPane;

/*
 * client class handles all the functions, connecting to server, sending and receiver message from server
 * auto send message to other user, count vector clock.
 * show all the message on the client GUI
 */

public class Client  {

	// client GUI to show message and send message
	public MessagePane clientMSG;
	// input and output to exchange message from server and client
	private ObjectInputStream inPut;		
	private ObjectOutputStream output;		
	// related information about socket, server, user, and port
	private Socket socket;					
	private String server, username;	
	private int port;
	// vector clock to store the value of every client and every event
	public int[] vectorClock;
	
	public Client(String server, int port) {
		this.server = server;
		this.port = port;
	}
	
/*
 * Client start to connect to server, open the client GUI
 * create input and output, create listen to get message from server
 * and list all the information for this client from server
 * Send message to the server is working inside the MessagePane.java class
 */
	public boolean start() throws Exception {
		try {
			
			// connect to the server, and create input and output to communicate with server
			socket = new Socket(server, port);
			inPut  = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
			
			// sending user name to the server, if conflict, then get the error message, 
			// stop the connect with server, input the new user name again
			output.writeObject(username);
			String msg = (String) inPut.readObject();
			if( msg.equalsIgnoreCase("Error") ) {
				return false;
			}
			
			// create the client GUI, and create the listener for listening form server
			// if every run correctly, show the GUI, and start listening 
			clientMSG = new MessagePane(this);
			ListenFromServer listen = new ListenFromServer( inPut, clientMSG, this );			
			clientMSG.setVisible(true);
			listen.start();
		}
		
		// handling the error, and return false to notify connection is failed
		catch (IOException e) {
			System.out.println("Login failed: " + e);
			return false;
		}
		
		// if every thing is working correctly, return true meaning OK
		return true;
	}
	
/*
 * get the client user name as an identify
 */

	public String getUsername() {
		return username;
	}

/*
 * This is send message to the server, is handling in the GUI side
 * event listen for hit "Enter" for the JTextFild
 */
	void sendMessage(ChatMessage msg) {
		try { output.writeObject(msg); }
		catch(IOException e) { System.out.println("Writing to server error: " + e); }
	}

/*
 * Start the client side, using the input name.
 * If the name is conflicting, input a new name again.
 * pop up client messageGUI to talk with other client
 */
	public static void main(String[] args) {

		// start login GUI, set visible, and then connect to the server
		// prepare everything to the check the user name and to start client 
		LoginGUI login = new LoginGUI();
		login.setVisible(true);
		Client client = new Client("localhost", 8188);
		
		// input text field listener to get the text as user name
		// send to server to check if is being used by online user
		login.name.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	client.username = login.name.getText();
            	
            	if( client.username.equals("A") || client.username.equals("B") || client.username.equals("C") ) {
            		try {
            			
            			// if client start successfully, invisible the login GUI
    					if( client.start() ) {
    						login.setVisible(false);
    					}else {
    						
    					// if having conflict with other user, show the error message, and input the name again
    						JOptionPane.showMessageDialog(null, "User name conflict error, Input a new name.");
    						login.name.setText("");
    					}
    				} catch (Exception e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
            	}else {
            		
            		// if user name is not correct, show the error message, and input the name again
					JOptionPane.showMessageDialog(null, "Error! User name have to be A or B or C.");
					login.name.setText("");
            	}
            }
        });
	}
	
/*
 * auto sending message function, send message to other user randomly,
 * and update the vector clock, the message is sending by Unicast
 */
	public String autoTest() {
		
		// set string array to handle the users to send
		// set @ as the symbol for Unicast, call the build in random function
		String[] users;
		String msg="@";
		Random rand = new Random();
		
		// make sure the user array, don't include the sending user itself
		// update the vector clock, after sending the message
		if(username.equals("A")) {
			users = new String[] {"B","C"};
			vectorClock[0]++;
		}else if(username.equals("B")) {
			users = new String[] {"A","C"};
			vectorClock[1]++;
		}else{
			users = new String[] {"A","B"};
			vectorClock[2]++;
		}
		
		// pick the client to send randomly
		int key = rand.nextInt(2);
		String vector = "<"+vectorClock[0]+","+vectorClock[1]+","+vectorClock[2]+">";
		
		// send message to other user, append with the related vector clock 
		msg += (users[key]+"    "+username+" local vector clock is "+vector);
		clientMSG.sendMsg(msg);
		
		// return the string for client GUI to show
		return users[key]+"     your vector clock "+vector+"\nYour Vector Clock: "+vector;
	}
}


