package co.edu.unbosque.persistence;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Persistence {
	
	private String rute = "./Data/pets-case.csv";
	
	public int writeCSV(String report) {
		File f = new File(this.rute); 
		try {
			FileWriter fw = new FileWriter(f.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(LocalDateTime.now()+ ";" + report + "\n");
			bw.close(); 
			fw.close();
		} catch (IOException e) {
			return -1;
		}
		return 0;
	}
	

}
