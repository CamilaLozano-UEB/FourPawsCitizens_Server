package co.edu.unbosque.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class Persistence {
	
	private String rute = "./Data/pets-case.csv";
	
	public int writeCSV(String report) {
		File f = new File(this.rute); 
		try {
			FileWriter fw = new FileWriter(f); 
			PrintWriter pw = new PrintWriter(fw); 
			pw.println(LocalDateTime.now()+ ";" + report + "\n"); 
			fw.close();
		} catch (IOException e) {
			return -1;
		}
		return 0;
	}
}
