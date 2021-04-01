package co.edu.unbosque.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class persistence {
	private String rute = "./Data/pets-case.csv";

	public int writeCSV(String dato) {

		File f = new File(this.rute); 

		try {
			FileWriter fw = new FileWriter(f); 
			PrintWriter pw = new PrintWriter(fw); 

			pw.println(dato); 

			fw.close();

		} catch (IOException e) {
			return -1;
		}

		return 0;
	}
}
