package co.edu.unbosque.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;

public class Persistence {

	private String rute = "./Data/pets-case.csv";

	/**
	 * 
	 * @param report, parameter that the report has, it comes from server
	 * 
	 *                Method that creates an attribute of type File and called
	 *                pets-case, the file is a .csv. After creating the file, open
	 *                it and write in the current date and the report of the case
	 *                that is brought from server, a new line is placed and the file
	 *                is closed.
	 * 
	 * @return returns a 0 or a -1
	 */
	public int writeCSV(String report) {
		File dir = new File("./Data");
		File f = new File(this.rute);
		if (!dir.exists()) {
			dir.mkdirs();
			try {
				BufferedWriter bw = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(f, true), "ISO-8859-1"));
				bw.write(
						"Fecha;Tipo de reporte;Especie;Tamaño;Localidad;Dirección;Nombre;Teléfono;Correo;Comentarios\n");
				bw.close();
			} catch (IOException e) {
				return -1;
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), "ISO-8859-1"));
			bw.write(LocalDateTime.now() + ";" + report + "\n");
			bw.close();
		} catch (IOException e) {
			return -1;
		}
		return 0;
	}

}
