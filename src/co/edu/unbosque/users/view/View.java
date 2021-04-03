package co.edu.unbosque.users.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class View extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField inputTextField;
	private JTextArea outputMessageArea;

	public View(String title) {

		this.inputTextField = new JTextField(50);
		this.outputMessageArea = new JTextArea(16, 50);
		this.setComponentCharacteristics();
		this.getContentPane().add(inputTextField, BorderLayout.SOUTH);
		this.getContentPane().add(new JScrollPane(outputMessageArea), BorderLayout.CENTER);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(title);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void setComponentCharacteristics() {
		this.outputMessageArea.setEditable(false);
		this.outputMessageArea.setBackground(new Color(96, 163, 188));
		this.outputMessageArea.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		
		this.inputTextField.setBackground(new Color(130, 204, 221));
		this.inputTextField.setFont(new Font("Times New Roman", Font.PLAIN, 14));

	}

	public void showMessages(String newLine) {
		outputMessageArea.append(newLine + "\n");
	}

	public JTextField getInputTextField() {
		return inputTextField;
	}

	public void setInputTextField(JTextField inputTextField) {
		this.inputTextField = inputTextField;
	}

	public JTextArea getOutputMessageArea() {
		return outputMessageArea;
	}

	public void setOutputMessageArea(JTextArea outputMessageArea) {
		this.outputMessageArea = outputMessageArea;
	}

}
