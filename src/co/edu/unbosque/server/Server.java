package co.edu.unbosque.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import co.edu.unbosque.persistence.Persistence;

public class Server {

	// Stores the state of the agent in the position of this
	private static ArrayList<Boolean> agentStatus = new ArrayList<Boolean>();
	// Stores the outputStream of the client you are talking to
	private static ArrayList<PrintWriter> clientConnection = new ArrayList<PrintWriter>();
	// Stores all the outputStream of the agents in their respective position
	private static ArrayList<PrintWriter> agentWriters = new ArrayList<PrintWriter>();
	// Stores all the outputStream of the clients in their respective position
	private static ArrayList<PrintWriter> clientWriters = new ArrayList<PrintWriter>();

	/**
	 * Runs the server with a maximum of 500 threads on the port 59001, and every
	 * time a user connects, it starts a thread and handles it
	 * 
	 * @param args
	 * @throws Exception
	 * 
	 */

	public static void main(String[] args) throws Exception {
		System.out.println("The chat server is running...");

		var pool = Executors.newFixedThreadPool(500);
		try (var listener = new ServerSocket(59001)) {
			while (true) {
				pool.execute(new FourPawsCitizensHandler(listener.accept()));
			}
		}
	}

	private static class FourPawsCitizensHandler implements Runnable {
		private Socket socket;
		private String role;
		private PrintWriter connectionWriter;
		private int userIndex;
		private Scanner in;
		private PrintWriter out;

		public FourPawsCitizensHandler(Socket socket) {
			this.socket = socket;
		}

		/**
		 * 
		 * It receives the type of user, if you are a client it shows you the option to
		 * register a case or contact an agent. If it is an agent it keeps it on hold
		 * until you receive a connection request from a client; you can accept it and
		 * communicate with him or deny it and keep waiting
		 */
		public void run() {
			try {
				in = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream(), true);
				// Receive the type of user
				String line = in.nextLine();
				if (line.equals("Client")) {
					// Assign the role, add the client's OutputStream to the
					// global arraylist and gives the main menu interaction options
					role = "Client";
					out.println("Bienvenido a Ciudadanos de 4 patas");
					userIndex = clientWriters.size();
					clientWriters.add(out);
					while (true) {
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
					// Assign the position to the client, add a null position to the
					// clientConnection, add a false (available) state, add the outputStream to
					// agentWriters and assign a role
					userIndex = clientConnection.size();
					clientConnection.add(null);
					agentStatus.add(false);
					agentWriters.add(out);
					role = "Agent";
					out.println("Conectado como agente");
					// Wait for the agent to receive a notification, when he responds and verifies
					// that your user number is correct.
					while (true) {
						line = in.nextLine();
						if (agentWriters.indexOf(out) != userIndex) {
							userIndex = agentWriters.indexOf(out);
						}
						// Establish the connection, change its state to true, save the outputStream
						// from the client in connectionWriter and engage the conversation until the
						// user exit
						if (clientConnection.get(userIndex) != null) {
							if (line.equalsIgnoreCase("Si") || line.equalsIgnoreCase("Sí")) {
								out.println("Se ha establecido conexión");
								connectionWriter = clientConnection.get(userIndex);
								agentStatus.set(userIndex, true);
								connectionWriter.println("Conexión establecida");
								while (true) {
									line = in.nextLine();
									if (clientConnection.indexOf(connectionWriter) == -1) {
										connectionWriter = null;
										break;
									}
									connectionWriter.println("Agente: " + line);
									out.println("Me: " + line);
								}
								// In such case the connection is denied, it will try to check if there are more
								// agents, if they do not exist, you will be notified of the lack of
								// customer availability. Reset the values to their initial state
							} else if (line.equalsIgnoreCase("No")) {
								out.println("Solicitud rechazada");
								if (userIndex == clientConnection.size() - 1) {
									connectionWriter = clientConnection.get(userIndex);
									connectionWriter.println("No hay agentes disponibles, presione ENTER");

								} else {
									clientConnection.get(userIndex).println(
											"El agente ha denegado su solicitud,\n escriba *esperar* si desea enviar la solicitud a otro agente");
								}
								connectionWriter = null;
								clientConnection.set(userIndex, null);
								agentStatus.set(userIndex, false);
							}
						}
					}
				}

			} catch (Exception e) {
				// Notify that the client or agent has disconnected and resets the values of the
				// other type of user
			} finally {
				if (role.equals("Client") && out != null) {
					if (connectionWriter != null) {
						connectionWriter
								.println("El cliente se ha desconectado, presione Enter antes de enviar otro mensaje");
					}
					if (-1 != clientConnection.indexOf(out)) {
						agentStatus.set(clientConnection.indexOf(out), false);
						clientConnection.set(clientConnection.indexOf(out), null);
					}

					clientWriters.remove(out);

				} else if (role.equals("Agent") && out != null) {
					if (agentWriters.indexOf(out) != userIndex)
						userIndex = agentWriters.indexOf(out);

					if (connectionWriter != null)
						connectionWriter
								.println("El agente se ha desconectado, presione Enter antes de enviar otro mensaje");
					agentWriters.remove(out);
					agentStatus.remove(userIndex);
					clientConnection.remove(userIndex);
				}
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}

		/**
		 * Checks that agents are available and establishes communication between a
		 * client and an agent
		 */

		private void agentConnection() {
			// TODO Auto-generated method stub
			out.println("Solicitud enviada");
			if (clientConnection.size() == 0) {
				out.println("No hay agentes disponibles");
				return;
			}
			// Look for an agent with whom to establish communication and verify that the
			// agent is available or does not have communication with another client
			for (int i = 0; i < clientConnection.size(); i++) {
				if (agentStatus.get(i) && i == clientConnection.size() - 1) {
					out.println("No hay agentes disponibles ");
				} else if (clientConnection.get(i) != null && i == clientConnection.size() - 1) {
					out.println("No hay más agentes disponibles ");
				} else if (clientConnection.get(i) == null && !agentStatus.get(i)) {

					connectionWriter = agentWriters.get(i);
					connectionWriter.println("¿Acepta solicitud?, responda Si o No");
					clientConnection.set(i, out);
					// Establish communication until you receive the notification that the agent
					// disconnected or unavailable
					while (true) {
						String input = in.nextLine();

						try {
							if (agentWriters.indexOf(connectionWriter) != i) {
								if (agentWriters.indexOf(connectionWriter) == -1)
									return;
								else
									i = agentWriters.indexOf(connectionWriter);
							}
							if (i == clientConnection.size() - 1 && !agentStatus.get(i)
									&& clientConnection.get(i) == null) {
								out.println("No hay más agentes disponibles");
								connectionWriter = null;
								return;
							}

							if (clientConnection.get(agentWriters.indexOf(connectionWriter)) == null) {
								connectionWriter = null;
								break;
							}

							if (agentStatus.get(agentWriters.indexOf(connectionWriter))) {
								out.println("Me: " + input);
								agentWriters.get(i).println("Client: " + input);
							}
						} catch (IndexOutOfBoundsException e) {
							connectionWriter = null;
							break;
						}

					}
				}

			}

		}

		/**
		 * Method that requests the necessary data from the client to assemble the
		 * report of the case. Create a string report that builds the case. Instance the
		 * persistence class and send it the string report
		 */

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