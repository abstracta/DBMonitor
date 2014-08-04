package logica;

import gui.Grafico;

import java.util.ArrayList;

import persistencia.AccesoProperties;
import persistencia.CurrentDir;

public class Graficador extends Thread {

	// semaforos de comunicacion con el monitor
	private Semaforo s, mutex;

	// memoria compartida con el monitor
	private ArrayList<ArrayList<Object>> buffer;

	// buffer parseado para pasarle al grafico
	private DataDatos buff;

	// arraylist con los nombres de las columnas
	private ArrayList<String> series;

	// es el nombre del grafico y del archivo
	private String nombre;
	
	// son la cantidad de v() realizados desde el monitor para graficar
	// la nueva informacion
	private int consultas_x_grafico;
	
	private int bufferSize;
	
	// es el tipo de gráfico, normal o de diferencias
	private String tipoGrafico;
	
	private int xWindowPosition, yWindowPosition;

	public Graficador(Semaforo s2, Semaforo mutex2, ArrayList<ArrayList<Object>> buffer2,
			String nombre, int cntConsultas, String tipoGrafico, int bufferSize, int xWindowPosition, int yWindowPosition) {
		this.s = s2;
		this.buffer = buffer2;
		this.mutex = mutex2;
		this.buff = null;
		this.nombre = nombre;
		this.consultas_x_grafico = cntConsultas;
		this.tipoGrafico = tipoGrafico;
		this.bufferSize = bufferSize;
		this.xWindowPosition = xWindowPosition;
		this.yWindowPosition = yWindowPosition;
	}

	public void Graficar() {
		this.start();
	}

	public void run() {

		AccesoProperties ap = new AccesoProperties(CurrentDir.getCurrentDir(), this.nombre + ".log");
		Grafico g = null;

		while (true) 
		{
			// espero a que el monitor me mande información
			// espero a q actualice el buffer al menos 'consultas_x_grafico' veces
			// al grafico debo pasarle al menos dos datos
			for (int h = 0; h < this.consultas_x_grafico; h++)
				this.s.p();

			/*
			 * for (int i=0 ; i < buffer.size() ; i++) {
			 * System.out.println("buffer en Graficador "+this.nombre+": " +
			 * buffer.get(i)); } //
			 */

			// persisto el buffer a disco
			this.mutex.p();
			try 
			{
				ap.reescribirBuffer(this.buffer);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			// agrego datos recibidos a la matriz de datos
			try {
				actualizarBuff();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// limpio el buffer
			buffer.removeAll(this.buffer);
			this.mutex.v();

			// System.out.println(buff.toString());

			// grafico datos desde 0 
			if (g == null) 
			{
				g = new Grafico(nombre, buff, series, tipoGrafico, xWindowPosition, yWindowPosition);

				g.pack();
				// RefineryUtilities.centerFrameOnScreen(g);
				g.setVisible(true);

			// re-grafico datos 
			} 
			else 
			{
				g.refrescarGrafico(this.buff);
			}

		}
	}

	private void actualizarBuff() throws Exception 
	{
		// creo un data datos con el buffer
		// lo agrego al data datos que ya tengo

		// la primera vez obtengo los nombres de las series
		if (this.buff == null) 
		{
			this.buff = new DataDatos(this.buffer, buffer.get(0).size(), bufferSize);
			this.series = this.buff.getSeries();
		} 
		else 
		{
			DataDatos aux = new DataDatos(this.buffer, series.size(), bufferSize);
			this.buff.add(aux);
		}
	}
}
