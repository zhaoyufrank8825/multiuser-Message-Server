/*
 *  zhaoyu
 *  1001450700
*/

//    https://www.youtube.com/watch?v=cRfsUrU3RjE&t=1211s
//    https://github.com/abhi195/Chat-Server/tree/master/src


package multiClient;
import javax.swing.*;

/*
 * GUI for the server to store and show the message, online user and all user
 */

public class ServerMessagePane extends JFrame {
	
	// define the related elements for the server GUI
	private static final long serialVersionUID = 1L;
	private JScrollPane scroll, scroll1, scroll2;
	
	// those elements need to update frequently, set visible by the outside
	public JTextArea textArea, onlineArea, allArea;
	
	public ServerMessagePane( ) {
		initialize();
	}
	
/*
 * Initialize the contents of the frame.
 */
	private void initialize() {
		// set the total area related values, boundary, and so on
		setTitle("Server");
		setBounds(100, 100, 500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		// set the text area related values, boundary, editable, and so on
		textArea = new JTextArea();
		textArea.setEditable(false);
		scroll = new JScrollPane(textArea);
		scroll.setBounds(10, 44, 324, 406);
		getContentPane().add(scroll);
		
		// set the text area related values, boundary, editable, and so on
		onlineArea = new JTextArea();
		onlineArea.setEditable(false);
		scroll1 = new JScrollPane(onlineArea);
		scroll1.setBounds(344, 44, 130, 123);
		getContentPane().add(scroll1);
		
		// set the text area related values, boundary, editable, and so on
		allArea = new JTextArea();
		allArea.setEditable(false);
		scroll2 = new JScrollPane(allArea);
		scroll2.setBounds(344, 211, 131, 239);
		getContentPane().add(scroll2);
		
		// set the label area related values, boundary, editable, and so on
		JLabel serverArea = new JLabel("Server Information");
		serverArea.setBounds(10, 11, 110, 22);
		getContentPane().add(serverArea);
		
		// set the label area related values, boundary, editable, and so on
		JLabel OnlineUser = new JLabel("Online User");
		OnlineUser.setBounds(344, 11, 110, 22);
		getContentPane().add(OnlineUser);
		
		// set the label area related values, boundary, editable, and so on
		JLabel AllUser = new JLabel("All User");
		AllUser.setBounds(343, 178, 110, 22);
		getContentPane().add(AllUser);
		
	}
}
