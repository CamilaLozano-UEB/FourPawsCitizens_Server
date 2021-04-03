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

	private static ArrayList<Boolean> agentesDisponiblesIndex = new ArrayList<Boolean>();
	private static ArrayList<PrintWriter> agentesDisponibles = new ArrayList<PrintWriter>();
	private static ArrayList<PrintWriter> agentWriters = new ArrayList<PrintWriter>();
	private static int numOfClients = 0;

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
		private String role;
		private PrintWriter connectionWriter;
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
					role = "Client";
					out.println("Bienvenido a Ciudadanos de 4 patas");
					userIndex = numOfClients;
					numOfClients++;
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
					userIndex = agentesDisponibles.size();
					agentesDisponibles.add(null);
					agentesDisponiblesIndex.add(false);
					agentWriters.add(out);
					role = "Agent";
					out.println("Conectado como agente");
					while (true) {
						line = in.nextLine();
						if (agentWriters.indexOf(out) != userIndex) {
							userIndex = agentWriters.indexOf(out);
						}

						if (agentesDisponibles.get(userIndex) != null) {
							if (line.equalsIgnoreCase("Si") || line.equalsIgnoreCase("Sí")) {
								out.println("Se ha establecido conexión");
								connectionWriter = agentesDisponibles.get(userIndex);
								agentesDisponiblesIndex.set(userIndex, true);
								connectionWriter.println("Conexión establecida");
								while (true) {
									line = in.nextLine();
									if (agentesDisponibles.indexOf(connectionWriter) == -1) {
										connectionWriter = null;
										break;
									}
									connectionWriter.println("Agente: " + line);
									out.println("Me: " + line);
								}
							} else if (line.equalsIgnoreCase("No")) {
								out.println("Solicitud rechazada");
								agentesDisponibles.get(userIndex).println(
										"El agente ha denegado su solicitud,\n escriba *esperar* si desea enviar la solicitud a otro agente");
								agentesDisponibles.set(userIndex, null);
								agentesDisponiblesIndex.set(userIndex, false);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (role.equals("Client") && out != null) {
					connectionWriter
							.println("El cliente se ha desconectado, presione Enter antes de enviar otro mensaje");
					agentesDisponiblesIndex.set(agentesDisponibles.indexOf(out), false);
					agentesDisponibles.set(agentesDisponibles.indexOf(out), null);
				} else if (role.equals("Agent") && out != null) {
					connectionWriter
							.println("El agente se ha desconectado, presione Enter antes de enviar otro mensaje");
					agentWriters.remove(out);
					agentesDisponiblesIndex.remove(userIndex);
					agentesDisponibles.remove(userIndex);
				}
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}

		private void agentConnection() {
			// TODO Auto-generated method stub
			out.println("Solicitud enviada");
			if (agentesDisponibles.size() == 0) {
				out.println("No hay agentes disponibles");
				return;
			}
			for (int i = 0; i < agentesDisponibles.size(); i++) {
				if (agentesDisponiblesIndex.get(i) && i == agentesDisponibles.size() - 1) {
					out.println("No hay agentes disponibles");
				} else if (agentesDisponibles.get(i) == null && !agentesDisponiblesIndex.get(i)) {
					connectionWriter = agentWriters.get(i);
					connectionWriter.println("¿Acepta solicitud?, responda Si o No");
					agentesDisponibles.set(i, out);
					while (true) {
						String input = in.nextLine();

						try {
							if (agentesDisponibles.get(agentWriters.indexOf(connectionWriter)) == null) {
								connectionWriter = null;
								break;
							}
							if (agentWriters.indexOf(connectionWriter) != i) {
								i = agentWriters.indexOf(connectionWriter);
								System.out.println(i);
							}
							if (i == agentesDisponibles.size() - 1 && !agentesDisponiblesIndex.get(i)
									&& agentesDisponibles.get(i) == null) {
								out.println("No hay más agentes disponibles");
								return;
							}

							System.out.println("meau");
							if (agentesDisponiblesIndex.get(agentWriters.indexOf(connectionWriter))) {
								System.out.println("lola");
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