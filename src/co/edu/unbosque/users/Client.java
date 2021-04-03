package co.edu.unbosque.users;

import co.edu.unbosque.users.view.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private Scanner in;
	private PrintWriter out;
	private View view;

	public Client() {

		view = new View("Four Pawns Citizens - Agent");
		// Send on enter then clear to prepare for next message
		view.getInputTextField().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(view.getInputTextField().getText());
				view.getInputTextField().setText("");
			}
		});

	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		var client = new Client();
		client.run();
	}

	private void run() throws IOException {
		try {
			@SuppressWarnings("resource")
			var socket = new Socket("127.0.0.1", 59001);
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println("Client");
			String line = "";
			while (in.hasNextLine()) {
				line = in.nextLine();
				view.showMessages(line);
			}
		} finally {
			view.setVisible(false);
			view.dispose();
		}
	}
}
