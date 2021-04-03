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

	public Agent() {
		view = new View("Four Pawns Citizens - Agent");

		view.getInputTextField().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(view.getInputTextField().getText());
				view.getInputTextField().setText("");
			}
		});
	}

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

	public static void main(String[] args) throws Exception {
		Agent agent = new Agent();
		agent.run();
	}

}
