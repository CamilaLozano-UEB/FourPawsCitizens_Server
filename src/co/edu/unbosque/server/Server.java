package co.edu.unbosque.server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class Server {
	private static ArrayList<Integer> agentesDisponibles = new ArrayList<Integer>();
	private static ArrayList<PrintWriter> agentWriters = new ArrayList<PrintWriter>();
	private static ArrayList<PrintWriter> clientWriters = new ArrayList<PrintWriter>();

	public static void main(String[] args) throws Exception {
		System.out.println("The chat server is running...");

		var pool = Executors.newFixedThreadPool(500);
		try (var listener = new ServerSocket(59001)) {
			while (true) {
				pool.execute(new Handler(listener.accept()));
			}
		}
	}

	private static class Handler implements Runnable {
		private Socket socket;
		private int userIndex;
		private Scanner in;
		private PrintWriter out;

		public Handler(Socket socket) {
			this.socket = socket;
		}

		/**
		 * Services this thread's client by repeatedly requesting a screen name until a
		 * unique one has been submitted, then acknowledges the name and registers the
		 * output stream for the client in a global set, then repeatedly gets inputs and
		 * broadcasts them.
		 */
		public void run() {
			try {
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);
				// Keep requesting a name until we get a unique one.
				String line = in.nextLine();
				if (line.equals("Client")) {
					userIndex = clientWriters.size();
					clientWriters.add(out);
					out.println("Bienvenido a Ciudadanos de 4 patas" + "\n Ingrese el numero que desea"
							+ " \n1. Crear un caso" + "\n2. Hablar con un agente");
					line = in.nextLine();
					switch (line) {
					case "1":
						this.createCase();
						break;
					case "2":
						this.agentConnection();
						break;
					default:
						out.println("Seleccione la opción correcta");
						break;
					}
				} else {
					userIndex = agentesDisponibles.size();
					agentesDisponibles.add(-1);
					agentWriters.add(out);
					out.println("Conectado como agente");
					line = in.nextLine();
					System.out.println(line);
					while (true) {
						if (line.equalsIgnoreCase("Si") || line.equalsIgnoreCase("Sí")) {
							clientWriters.get(agentesDisponibles.get(userIndex)).println(line);
							while (line != "exit") {
								line = in.nextLine();
								clientWriters.get(agentesDisponibles.get(userIndex)).println("Agente: " + line);
								out.println("Me: " + line);
							}
						} else {
							agentesDisponibles.set(userIndex, -1);
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		private void agentConnection() {
			// TODO Auto-generated method stub

			for (int i = 0; i < agentesDisponibles.size(); i++) {
				if (agentesDisponibles.get(i) == -1) {
					agentWriters.get(i).println("¿Acepta solicitud?, responda Si o No");
					agentesDisponibles.set(i, userIndex);
					if (agentesDisponibles.get(i) != -1) {
						while (true) {
							String input = in.nextLine();
							if (input.toLowerCase().equals("exit")) {
								break;
							}
							out.println("Me: " + input);
							agentWriters.get(i).println("Client: " + input);
						}
					}
				}
			}

		}

		private void createCase() {
			// TODO Auto-generated method stub
			out.println("Crear caso");

		}
	}

}
