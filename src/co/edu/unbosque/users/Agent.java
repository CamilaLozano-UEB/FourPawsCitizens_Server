package co.edu.unbosque.users;

import co.edu.unbosque.users.view.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Agent {

	private Scanner in;
	private PrintWriter out;
	private View view;

	/**
	 * Instances the hearing and gives it the corresponding title. Assign a
	 * actionListener that listens every time the user types something and sends it
	 */
	public Agent() {
		view = new View("Four Pawns Citizens - Agent");
		view.getInputTextField().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(view.getInputTextField().getText());
				view.getInputTextField().setText("");
			}
		});
	}

	/**
	 * Creates the connection with the server on the established port 59001, sends
	 * the agent type to the server and keeps receiving messages
	 * 
	 * @throws IOException
	 */

	private void run() throws IOException {
		try {
			@SuppressWarnings("resource")
			var socket = new Socket("127.0.0.1", 59001);
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println("Agent");
			while (in.hasNextLine()) {
				view.showMessages(in.nextLine());
			}
		} finally {
			view.setVisible(false);
			view.dispose();
		}
	}

	/**
	 * Runs the Agent class
	 * 
	 * @param args
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {
		Agent agent = new Agent();
		agent.run();
	}

}
