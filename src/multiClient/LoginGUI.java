/*
 *  zhaoyu
 *  1001450700
*/

//    https://www.youtube.com/watch?v=cRfsUrU3RjE&t=1211s
//    https://github.com/abhi195/Chat-Server/tree/master/src

/*
 * This is GUI to login
 */

package multiClient;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/*
 * login GUI for the client to connect with the server
 * the text field is using to input the user name
 */
public class LoginGUI extends JFrame{
	private static final long serialVersionUID = 1L;
	
	// the text field is using to input the name, this is public for fetching from outside
	public JTextField name;
	
	public LoginGUI( ) {
		initialize();
	}
	
/*
 * Initialize the contents of the frame.
 */
	private void initialize() {
		// set the total area related values, boundary, and so on
		setTitle("Login");
		setBounds(300, 300, 294, 151);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		// set the test area related values, boundary, and so on
		name = new JTextField();
		name.setBounds(35, 62, 219, 27);
		getContentPane().add(name);
		name.setColumns(10);
		
		// set the label area related values, boundary, and so on
		JLabel OnlineUser = new JLabel("Select name: A or B or C");
		OnlineUser.setBounds(35, 31, 186, 20);
		getContentPane().add(OnlineUser);
	}
}
