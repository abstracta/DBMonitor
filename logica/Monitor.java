package logica;

import java.util.ArrayList;

import persistencia.ExecuteQuery;

public class Monitor extends Thread {

	/**
	 * Este semaforo permite pausar el monitor
	 */
	private Semaforo pausa;

	/**
	 * Es el semaforo q despierta al graficador para q consuma el buffer
	 */
	private Semaforo s;

	/**
	 * Mutex permite mutuexcluir el buffer
	 */
	private Semaforo mutex;

	/**
	 * Es el buffer compartido con el graficador
	 */
	private ArrayList<ArrayList<Object>> buffer;

	/**
	 * La primera vez q consultamos la BD queremos obtener los nombres de las
	 * columnas para utilizarlas como series en el gráfico. Este boolean
	 * distingue la primer consulta a la BD del resto.
	 */
	private boolean primera;

	/**
	 * Es el intervalo de tiempo que transcurre entre consultas a la BD. Durante
	 * este tiempo el hilo permanece dormido.
	 */
	private int tmp_descanso;
	
	
	private int columIndex;

	/**
	 * Es el nombre del conector, lo necesitamos para obtener los datos de
	 * configuración para realizar la conexión.
	 */
	private String nombreConector;

	/**
	 * Es la consulta obtenida desde el archivo de configuración
	 */
	private String consulta;
	
	private String paramDefinition;
	
	private String skipColumnsInChart;

	/**
	 * 
	 * @param s:
	 *            es el semaforo que despierta al graficador para que obtenga
	 *            datos del buffer
	 * @param mutex:
	 *            es el semaforo que mutuexluye el buffer para evitar conflictos
	 * @param buffer:
	 *            aqui se depositan los datos para que el graficador los
	 *            grafique
	 * @param tmp_descanso:
	 *            intervalo de tiempo en el el monitor esta durmiendo
	 * @param nombreConector:
	 *            necesario para obtener los datos de configuracion para
	 *            establecer la conexion con la BD
	 * @param Consulta:
	 *            es la consulta que realizaremos a la BD
	 */
	public Monitor(Semaforo s, Semaforo mutex, ArrayList<ArrayList<Object>> buffer,
			int tmp_descanso, String nombreConector, String Consulta, String ParamDefinition, int columIndex, String skipColumnsInChart) 
	{
		this.s = s;
		this.buffer = buffer;
		this.mutex = mutex;
		this.primera = true;
		this.pausa = new Semaforo(1);
		this.tmp_descanso = tmp_descanso;
		this.nombreConector = nombreConector;
		this.consulta = Consulta;
		this.paramDefinition = ParamDefinition;
		this.columIndex = columIndex;
		this.skipColumnsInChart = skipColumnsInChart;
	}

	/**
	 * Se realiza la consulta a la BD cada milis/2000 segundos. Y se guardan los
	 * resultados en el archivo ./resultado_cons.log
	 * 
	 */
	public void Monitorizar() 
	{
		this.start();
	}

	public void pausa() 
	{
		this.pausa.p();
	}

	public void reanudar()
	{
		this.pausa.v();
	}

	public String getNombre() 
	{
		return this.nombreConector;
	}
	
	public void run()
	{
		try 
		{
			// aca obtengo la conexion a la BD
			ExecuteQuery bd = new ExecuteQuery(nombreConector, paramDefinition, columIndex, skipColumnsInChart); 
			bd.connectServer();

			while (true) 
			{
				// si me pausan me quedo trancado acá, hasta que termine la
				// pausa
				pausa.p();

				ArrayList<ArrayList<Object>> res;

				// consulto la BD
				if (primera) 
				{
					primera = false;
					res = bd.executeQueryFirstTime(consulta);
				} else {
					res = bd.executeQuery(consulta);
				}

				// accedo el recurso compartido: buffer
				mutex.p();
				buffer.addAll(res);

				/*
				  for (int i=0 ; i < buffer.size() ; i++) { 
					  sout("buffer en monitor "+this.nombreConector+": " + buffer.get(i)); }
				  sout("----------------------------------------------------------------"); //
				 //*/

				// libero el recurso compartido: buffer
				
				mutex.v();

				// despierto al graficador para q obtenga los datos del buffer, solo si hay datos para actualizar
				if (res.size() > 0) {
					s.v();
				}

				// libero el semaforo para la siguiente iteracion
				pausa.v();

				// me duermo unos segundos
				Monitor.sleep(this.tmp_descanso);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void sout(Object s) 
	{
		System.out.println(s);
	}
}
