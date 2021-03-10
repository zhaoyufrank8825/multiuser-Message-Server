/*
 *  zhaoyu
 *  1001450700
*/

//    https://www.youtube.com/watch?v=cRfsUrU3RjE&t=1211s
//    https://github.com/abhi195/Chat-Server/tree/master/src


package multiClient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Timer;
import java.util.TimerTask;

/*
 * The class is to listen from server, and show in the client GUI,
 * include a timer to send the message in every 5s,
 * print out the message from server to the client GUI 
 */
public class ListenFromServer extends Thread {
	
	// define input from server, the message pane to print out the related message
	private ObjectInputStream inPut;	
	private MessagePane messagePane;
	// client to manipulate some value in client
	private Client client;
	// timer to schedule sending message to other clients
	Timer t = new Timer();
	
	public ListenFromServer(ObjectInputStream inPut, MessagePane messagePane, Client client) {
		this.inPut = inPut;
		this.messagePane = messagePane;
		this.client = client;
	}
	
/*
 * while loop to keep receiving message from other client
 * and fetch the vector clock information with the message
 * and sending the message to other clients
 * and append the vector clock to the out message
 */
	public void run() {
		while(true) {
			try {
				// reading input receiving message, and print out to the console, 
				// and ready to fetch the information in the receiving message
				String msg = (String) inPut.readObject();
				System.out.println(msg);
				System.out.print("> ");
				
				// fetch the vector clock coming with the receiving message, if the message doesn't include the vector clock
				// just don't update vector and print out the information
				int k = msg.indexOf('<');
				if( k>0 ) {
					
					// get the local client information, the related column increase one by receiving this message
					// store the column value to update the local vector clock
					char username = client.getUsername().charAt(0);
					int local = client.vectorClock[username-'A']+1;
					
					// if the message has vector clock with it, split the vector clock in to three parts
					String[] tokens = msg.substring(k+1).split(",");
					tokens[2] = tokens[2].substring(0,tokens[2].length()-1);
					
					// update the vector clock with related column, so cover all three value first
					for(int i=0; i<3; i++) {
						client.vectorClock[i]=Integer.valueOf(tokens[i]);
					}
					
					// set the local client vector clock value to the one stored before
					// and then the new array is the correct vector clock now
					client.vectorClock[username-'A']=local;
					String vector = "<"+client.vectorClock[0]+","+client.vectorClock[1]+","+client.vectorClock[2]+">";
					
					// print out the receiving message, and updated vector clock to GUI
					messagePane.textArea.append(msg+"\nYour Vector Clock: "+vector+"\n");
				}else {
					
					// if the receiving message doesn't have vector clock, just print out the original message
					messagePane.textArea.append(msg+"\n");
				}
				
				// receive the message from server, if there are three clients online
				// and then start the auto sending function to test lab3
				if(msg.equalsIgnoreCase("Three clients, Test is ready to go.")) {
					
					// set the local vector clock to 0 0 0, and print out to the GUI
					client.vectorClock = new int[] {0,0,0};
					messagePane.textArea.append("====================================\n");
					messagePane.textArea.append("your Vector Clock: <0,0,0>\n");
					int count = 0;
					
					
					// schedule call the auto test function in the client, send message 
					// to other clients in every 5 seconds
					t.schedule(new TimerTask() {
					    @Override
					    public void run() {
					    	String s = client.autoTest();
					    	
					    	// print out the send out message in the GUI
					    	messagePane.textArea.append("your -> "+ s+"\n");
					    }
					}, 0, 3000);
					if( count++ == 200 ) t.cancel();
				}
			}
			
			// handle the error message, and show the message in the console
			catch (ClassNotFoundException | IOException e) {
				System.out.println("Server has closed the connection: " + e );
				break;
			} 
		}
	}
}

