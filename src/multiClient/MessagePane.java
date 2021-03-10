/*
 *  zhaoyu
 *  1001450700
*/

//    https://www.youtube.com/watch?v=cRfsUrU3RjE&t=1211s
//    https://github.com/abhi195/Chat-Server/tree/master/src


package multiClient;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/*
 * Every client has a related GUI, GUI is setting in the initialize function
 * input field is listening and sending message, the sendMSG function
 * will separate special case and normal case
 */

public class MessagePane extends JFrame {
	
	// define the related elements in the GUI
	private static final long serialVersionUID = 1L;
	private JScrollPane scroll;
	
	// define a client to use some information in the client
	private Client client;
	// this two text elements need to be public for the client to change outside
	public JTextArea textArea;
	public JTextField inputField;
	
/*
 * Create the application.
 */
	public MessagePane(Client client ) {
		this.client = client;
		initialize();
		
		// input text field listener to send message to server
		// and print out the message to the client GUI
		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = inputField.getText();
				sendMsg(text);
				textArea.append("You: "+text+"\n");
				
				// after sending the message, set the text field to empty
				inputField.setText("");
			}
		});
	}
	
/*
 * Initialize the contents of the frame.
 */
	private void initialize() {
		// set the total area related values, boundary, and so on
		setTitle(client.getUsername());
		setBounds(300, 300, 349, 394);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		// set the text area related values, boundary, editable, and so on
		textArea = new JTextArea();
		textArea.setEditable(false);
		scroll = new JScrollPane(textArea);
		scroll.setBounds(10, 11, 313, 293);
		getContentPane().add(scroll);
		
		// set the label area related values, boundary, editable, and so on
		JLabel MSGLabel = new JLabel("Input:");
		MSGLabel.setBounds(10, 322, 46, 14);
		getContentPane().add(MSGLabel);
		
		// set the input text area related values, boundary, editable, and so on
		inputField = new JTextField();
		inputField.setBounds(66, 315, 257, 29);
		getContentPane().add(inputField);
		inputField.setColumns(10);
	}
	
	/*
	 * send message function, take care of "logout, whoisin, alluser and check" which 
	 * are the special case, we need handle first, and then the last else take care of the 
	 * regular sending message case
	 */
	
	public void sendMsg(String msg){
		if(msg.equalsIgnoreCase("LOGOUT")) {
			
		// case 1: logout action, disconnect with the server, and stop the working
		// broad cast the notice to every online user
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			client.clientMSG.setVisible(false);
			return;
		}else if(msg.equalsIgnoreCase("WHOISIN")) {
			
		// case 2: who is in action, check which user is online, and send the information to 
		// the client who want to know this
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
		}else if(msg.equalsIgnoreCase("ALLUSER")){
			
		// case 3: all user action, check which all user name used before, and send the information to 
		// the client who want to know this
			client.sendMessage(new ChatMessage(ChatMessage.ALLUSER, ""));
		}else {
			
		// case 4: normal message, put the user name in front of message, make sure the receiver 
		// know where the message is from
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
		}
	}
}