package persistencia;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class AccesoProperties {
	private String ruta;
	private String archivo;
	
	public AccesoProperties(String ruta, String archivo) {
		this.ruta = ruta;
		this.archivo = archivo;
	}
	
	public void reescribirBuffer(ArrayList<ArrayList<Object>> lineas) throws IOException {
		if (lineas.size() == 0) {
			return;
		}
		
		FileWriter fw = new FileWriter(getArch(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);

		for (int i=0; i < lineas.size(); i++) 
		{
			ArrayList<Object> tmp = lineas.get(i);
			int lineasSize = tmp.size();
			String aux = tmp.get(0).toString();
			
			for (int j=1; j < lineasSize; j++)
			{
				aux += "," + tmp.get(j);
			}
			
			pw.println(aux);
		}
		
		pw.close();
		bw.close();
		fw.close();
	}
	
	public String getArch() {
		return (ruta.endsWith("/"))?ruta+archivo : ruta+"/"+archivo;
	}
}
