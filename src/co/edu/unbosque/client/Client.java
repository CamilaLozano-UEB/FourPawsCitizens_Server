package co.edu.unbosque.client;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("127.0.0.1", 9999);
			System.out.println("Connected: " + socket);
			System.out.println("Bienvenido a Ciudadanos de 4 patas" + "\n Ingrese el numero que desea"
					+ " \n1. Crear un caso" + "\n2. Hablar con un agente");

			var scanner = new Scanner(System.in);
			var in = new Scanner(socket.getInputStream());
			var out = new PrintWriter(socket.getOutputStream(), true);

			while (scanner.hasNextLine()) {
				out.println(scanner.nextLine());
				int op = Integer.parseInt(in.nextLine());
				if (op == 1) {
					System.out.println("¿Que va a reportar?\n" + "(1) Pérdida,\n" + "(2) Robo,\n" + "(3) Abandono,\n"
							+ "(4) Animal peligroso, o\n" + "(5) Manejo indebido en vía pública. ");
					out.println(scanner.nextLine());
					int op2 = Integer.parseInt(in.nextLine());
					System.out.println("Ingrese la especie");
					out.println(scanner.nextLine());
					String specie = in.nextLine();
					System.out.println("Ingrese la Tamaño");
					out.println(scanner.nextLine());
					String size = in.nextLine();
					System.out.println("Ingrese la Localidad");
					out.println(scanner.nextLine());
					String neighborhood = in.nextLine();
					System.out.println("Ingrese la Dirección");
					out.println(scanner.nextLine());
					String address = in.nextLine();
					System.out.println("Ingrese su nombre");
					out.println(scanner.nextLine());
					String name = in.nextLine();
					System.out.println("Ingrese su Telefono");
					out.println(scanner.nextLine());
					String phone = in.nextLine();
					System.out.println("Ingrese su correo");
					out.println(scanner.nextLine());
					String email = in.nextLine();
					System.out.println("Ingrese su comentarios");
					out.println(scanner.nextLine());
					String comment = in.nextLine();

					String report = op2 + ";" + specie + ";" + size + ";" + neighborhood + ";" + address + ";" + phone
							+ ";" + email + ";" + comment;
					System.out.println("El caso ha sido creado.");
					System.out.println("Bienvenido a Ciudadanos de 4 patas" + "\n Ingrese el numero que desea"
							+ " \n1. Crear un caso" + "\n2. Hablar con un agente");

				} else {

					System.out.println("No se que hace ");
					System.out.println("Bienvenido a Ciudadanos de 4 patas" + "\n Ingrese el numero que desea"
							+ " \n1. Crear un caso" + "\n2. Hablar con un agente");
				}

			}

		} catch (

		Exception e) {

		}
	}

}
