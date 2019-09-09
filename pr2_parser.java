/** Programmed by Kwing
 * Programmed on February 27, 2015
 * This application parses messages on Platform Racing 2 into a
 * more readable format. It loads a .TXT and exports a decoded
 * version of the messages. */

/* javac URLDecode.java
   jar cfm URLDecode.jar manifest.txt URLDecode.class
   java -jar URLDecode.jar */

//http://jiggmin.com/threads/127382-Parsing-Program

// Access your raw input file here:
// http://pr2hub.com/get_messages.php?start=0&count=9999999999

package day1;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class URLDecode {
	
	public static void main (String[] args) throws Exception {
		
		final String resourcePath = ClassLoader.getSystemResource("").getPath();
		
		String fileName = "";
		
		//Creates a file chooser for users to select the encoded messages TXT
	    JFileChooser chooser = new JFileChooser(resourcePath);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "TXT Files", "txt");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       fileName = chooser.getSelectedFile().getAbsolutePath();
	    }else{
	    	System.exit(1);
	    }
		
	    //Initialize the files to be loaded and saved
		java.io.File file = new java.io.File(fileName);
		java.io.File newFile = new java.io.File(resourcePath + "MOD" + file.getName());
		PrintWriter output = new PrintWriter(newFile);
		Scanner input = new Scanner(file);
		
		/* Because the program parses between = and & signs, an & sign
		 * is concatinated to the end of the encoded message to make the last
		 * variable readable by the parsing program. */
		String encoded = input.next();
		encoded = encoded + "&";
		
		/* atIndex remembers the index of the last substring taken
		 * so that the next substring taken will be parsed correctly,
		 * one after the other. */
		int atIndex = 0;
		
		/* Finds the last index of &message_id and first = afterward, the
		* resulting String will be the number of messages in the inbox. */
		int numOfMessages = Integer.parseInt(encoded.substring(encoded.lastIndexOf("&message_id") + 11,
				encoded.indexOf('=', encoded.lastIndexOf("&message_id") + 1))) + 1;

		//Each message contains six pieces of information
		String[] msgData = new String[numOfMessages * 6];
		
		for(int i = 0; i < numOfMessages * 6; i++){
			//(message_id, name, group, message, time, user_id)
			switch(i % 6){
			
			//message_id
			case 0:
				msgData[i] = encoded.substring(encoded.indexOf('=', atIndex) + 1,
						encoded.indexOf('&', atIndex + 1));
				break;
			
			//name
			case 1:
				msgData[i] = URLDecoder.decode(encoded.substring(encoded.indexOf('=', atIndex) + 1,
						encoded.indexOf('&', atIndex + 1)), "UTF-8");
				break;
				
			//group
			case 2:
				msgData[i] = getGroup(encoded.substring(encoded.indexOf('=', atIndex) + 1,
					encoded.indexOf('&', atIndex + 1)));
				break;
				
			//message
			case 3:
				msgData[i] = URLDecoder.decode(encoded.substring(encoded.indexOf('=', atIndex) + 1,
					encoded.indexOf('&', atIndex + 1)), "UTF-8");
				break;
				
			//time
			case 4:
				msgData[i] = getTime(encoded.substring(encoded.indexOf('=', atIndex) + 1,
					encoded.indexOf('&', atIndex + 1)));
				break;
				
			//message_id
			case 5:
				msgData[i] = encoded.substring(encoded.indexOf('=', atIndex) + 1,
						encoded.indexOf('&', atIndex + 1));
				break;
				
			}
			// Move atIndex forward to prevent duplicate reading.
			atIndex = encoded.indexOf('&', atIndex + 1);
		}
		
		// Add inbox data to output file.
		output.println("Platform Racing 2 Private Message Decoder by Kwing");
		
		for(int i = 0; i < msgData.length; i+=6){
			output.println("\nMessage ID: " + msgData[i] + ", Sender ID: " + msgData[i + 5]);
			output.println("Sent by " + msgData[i + 2] + " " + msgData[i + 1] + " at " + msgData[i + 4]);
			output.println("\n" + msgData[i + 3]);
		}
		
		JOptionPane.showMessageDialog(new JLabel(""), "Saved to " + newFile.getAbsoluteFile());
		
		input.close();
		output.close();
		
	}
	
	/* Converts group variable to corresponding group
	 * of the sender and returns it as a String. */
	public static String getGroup(String str){
		if(str.equals("1")){
			return "Member";
		}
		if(str.equals("2")){
			return "Moderator";
		}
		if(str.equals("3")){
			return "Admin";
		}
		return "???";
	}
	
	/* Parses a String into a long which counts UNIX in
	 * seconds, then multiplies by 1000 to equal time in
	 * milliseconds. format will return a String showing
	 * the timestamp. */
	public static String getTime(String str){
		SimpleDateFormat timeStamp = new SimpleDateFormat();
		return timeStamp.format(Long.parseLong(str) * 1000);
	}
	
}