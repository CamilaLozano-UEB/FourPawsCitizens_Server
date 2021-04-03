package co.edu.unbosque.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
		File f = new File(this.rute);
		if(!f.exists()) {
			try {
				f.mkdirs();
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		try {
			FileWriter fw = new FileWriter(f.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(LocalDateTime.now() + ";" + report + "\n");
			bw.close();
			fw.close();
		} catch (IOException e) {
			return -1;
		}
		return 0;
	}

}
