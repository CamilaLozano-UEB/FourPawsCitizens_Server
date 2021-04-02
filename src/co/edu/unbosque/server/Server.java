package co.edu.unbosque.server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import co.edu.unbosque.persistence.Persistence;

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
					out.println("Bienvenido a Ciudadanos de 4 patas");
					while (true) {
						userIndex = clientWriters.size();
						clientWriters.add(out);
						out.println(
								" Ingrese el numero que desea" + " \n1. Crear un caso" + "\n2. Hablar con un agente");
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
					}

				} else {
					userIndex = agentesDisponibles.size();
					agentesDisponibles.add(-1);
					agentWriters.add(out);
					out.println("Conectado como agente");
					line = in.nextLine();
					boolean chat = true;
					while (chat) {
						if (line.equalsIgnoreCase("Si") || line.equalsIgnoreCase("Sí")) {
							clientWriters.get(agentesDisponibles.get(userIndex)).println("Conexión establecida");
							while (line != "exit") {
								line = in.nextLine();
								clientWriters.get(agentesDisponibles.get(userIndex)).println("Agente: " + line);
								out.println("Me: " + line);
							}
						} else {
							if ((userIndex + 1) == agentesDisponibles.size()) {
								clientWriters.get(agentesDisponibles.get(userIndex))
										.println("No hay agentes disponibles");
								chat = false;
								line = "exit";
							} else {
								clientWriters.get(agentesDisponibles.get(userIndex)).println("El agente "
										+ (userIndex + 1)
										+ " ha denegado su solicitud,\n escriba *esperar* si desea esperar de lo contrario escriba *exit*");
								agentesDisponibles.set(userIndex, -1);
								line = "exit";
							}

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
							if (input.toLowerCase().equals("exit") || input.toLowerCase().equals("esperar")) {
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
			Persistence per = new Persistence();
			// TODO Auto-generated method stub
			out.println("Crear caso");
			out.println("¿Que va a reportar? ingrese el numero\n" + "(1) Pérdida\n" + "(2) Robo\n" + "(3) Abandono\n"
					+ "(4) Animal peligroso\n" + "(5) Manejo indebido en vía pública. ");
			String op2 = (in.nextLine());
			out.println("Ingrese la especie");
			String specie = in.nextLine();
			out.println("Ingrese la Tamaño");
			String size = in.nextLine();
			out.println("Ingrese la Localidad");
			String neighborhood = in.nextLine();
			out.println("Ingrese la Dirección");
			String address = in.nextLine();
			out.println("Ingrese su nombre");
			String name = in.nextLine();
			out.println("Ingrese su Telefono");
			String phone = in.nextLine();
			out.println("Ingrese su correo");
			String email = in.nextLine();
			out.println("Ingrese su comentarios");
			String comment = in.nextLine();
			String event;
			switch (op2) {
			case "1":
				event = "Pérdida";
				break;
			case "2":
				event = "Robo";
				break;
			case "3":
				event = "Abandono";
				break;
			case "4":
				event = "Animal peligroso";
				break;
			case "5":
				event = "Manejo indebido en vía pública";
				break;

			default:
				event = "No se especifico";
				break;
			}
			String report = event + ";" + specie + ";" + size + ";" + neighborhood + ";" + address + ";" + name + ";"
					+ phone + ";" + email + ";" + comment;
			out.println("El caso ha sido creado.");
			per.writeCSV(report);

		}
	}

}
