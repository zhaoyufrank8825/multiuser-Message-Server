/*
 *  zhaoyu
 *  1001450700
*/

//    https://www.youtube.com/watch?v=cRfsUrU3RjE&t=1211s
//    https://github.com/abhi195/Chat-Server/tree/master/src


package multiClient;
import java.io.Serializable;


/*
 * The is for the special case of message
 */
public class ChatMessage implements Serializable {

	// set the special case number, type, and message
	private static final long serialVersionUID = 1L;
	public static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, ALLUSER=3;
	private int type;
	private String message;
	
	ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	// getter and setter methods, here is getter methods to get the related values
	int getType() {
		return type;
	}

	String getMessage() {
		return message;
	}
}
