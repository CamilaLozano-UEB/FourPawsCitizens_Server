package co.edu.unbosque.agent;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Agent {

	Scanner in;
	PrintWriter out;
	JFrame frame = new JFrame("Chatter");
	JTextField textField = new JTextField(50);
	JTextArea messageArea = new JTextArea(16, 50);

	public Agent() {
		messageArea.setEditable(false);
		frame.getContentPane().add(textField, BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
		frame.pack();

		// Send on enter then clear to prepare for next message
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(textField.getText());
				textField.setText("");
			}
		});
	}

	private void run() throws IOException {
		try {
			var socket = new Socket("127.0.0.1", 59001);
			in = new Scanner(socket.getInputStream());
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println("Agent");
			while (in.hasNextLine()) {
				var line = in.nextLine();
				messageArea.append(line + "\n");
			}
		} finally {
			frame.setVisible(false);
			frame.dispose();
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Agent agent = new Agent();
		agent.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		agent.frame.setLocationRelativeTo(null);
		agent.frame.setVisible(true);
		agent.run();

	}

	public Scanner getIn() {
		return in;
	}

	public void setIn(Scanner in) {
		this.in = in;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

}
