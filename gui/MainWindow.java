
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import persistencia.xmlConfig;


import logica.Graficador;
import logica.Monitor;
import logica.Semaforo;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private ArrayList<Monitor> mon = new ArrayList<Monitor>();
	
	private String TITULO = "DB Monitor";
	
	private String CERRAR = "Exit";
	private String ACEPTAR  = "Start monitoring";
	private String PAUSA 	= "Pause";
	private String REANUDAR = "Restart";
	
	private boolean pausa	= false;
	private boolean monitorizando = false;
	
	public static void main(String[] args) {
		new MainWindow();
	}
	
	/**
	 * Constructor
	 */
	public MainWindow() 
	{
		this.setTitle(TITULO);
		JLabel label = new JLabel();
		label.setBorder(new LineBorder(Color.black, 3));
				
		getContentPane().add(crearBotones(), BorderLayout.SOUTH);
		addWindowListener(new java.awt.event.WindowAdapter() 
		{
            public void windowClosing(java.awt.event.WindowEvent evt) 
            {
                salir();
            }
        });
		
		this.setSize(300,70);
		this.setLocation(150,200);
		
		this.setVisible(true);
	}
	
	/**
	 * @return JPanel with Run and Quit button
	 */
	private JPanel crearBotones()
	{
		JPanel panelBotones  = new JPanel(new GridBagLayout());
		final JButton botonAceptar  = new JButton(ACEPTAR);
		final JButton botonCerrar = new JButton(CERRAR);
		
		botonCerrar.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				salir();
			}
		});
		
		botonAceptar.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if (!monitorizando) 
				{
					botonAceptar.setText(PAUSA);
					aceptar();
				}
				else 
				{
					if (!pausa) 
					{
						botonAceptar.setText(REANUDAR);
						pausa();
					} 
					else 
					{
						botonAceptar.setText(PAUSA);
						reanudar();
					}
				}
			}
		});	
		
		panelBotones.add(botonAceptar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 0, 2, 5), 0, 0));
					
		panelBotones.add(botonCerrar, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 5, 2, 0), 0, 0));

		return panelBotones;
	}

	/**
	 * Tenemos por un lado el monitor de la BD, quien pone los 
	 * datos en un buffer, por otro lado tenemos el consumidor 
	 * de datos, quien actualiza la grafica y persiste los datos.
	 * 
	 * Para la sincronizacion se utiliza un semáforo.
	 * El monitor y el Graficador corren en hilos distintos.
	 * 
	 */
	private void aceptar() 
	{
		if (monitorizando) 
		{
			return;
		}
			
		monitorizando = true;
				
		xmlConfig xml = new xmlConfig();
		
		// get screen resolution
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int screenWidth = gd.getDisplayMode().getWidth();
		int screenHeight = gd.getDisplayMode().getHeight();
		
		try 
		{
			ArrayList<String> noms = xml.getNombresDeGraficadores();
			
			int initialX = 500;
			int x = initialX;
			
			int initialY = 0;
			int deltaY = 20;
			int y = initialY;
			
			for(int i=0 ; i<noms.size() ; i++) 
			{
				//sout("graficador leido de archivo de conf:" + noms.get(i));
				String timeDescanso = xml.getTimeDescanso(noms.get(i));
				String consulta = xml.getConsulta(noms.get(i));
				String tipoGrafico = xml.getTipoGrafico(noms.get(i));
				int cntConsultas = xml.getCntConsultas(noms.get(i));
				String params = xml.getParamDefinition(noms.get(i));
				int columIndex = xml.getxAxisColumnId(noms.get(i));
				String skipColumnsInChart = xml.getSkipColumnsInChart(noms.get(i));
				int chartBufferSize = xml.getChartBufferSize(noms.get(i));

				// creo los semaforos y el buffer para cada par monitor - graficador
				Semaforo s = new Semaforo(0);
				Semaforo mutex = new Semaforo(1);
				ArrayList<ArrayList<Object>> buffer = new ArrayList<ArrayList<Object>>();

				int timeD = new Integer(timeDescanso).intValue();
				Monitor m = new Monitor(s, mutex, buffer, timeD, noms.get(i), consulta, params, columIndex, skipColumnsInChart); 
				Graficador gr = new Graficador(s, mutex, buffer, noms.get(i), cntConsultas, tipoGrafico, chartBufferSize, x, y);

				mon.add(m);
				gr.Graficar();
				
				x += Grafico.WindowsWidth;
				if (x + Grafico.WindowsWidth > screenWidth) {
					x = initialX;
					y += Grafico.WindowsHeight;
					if (y + Grafico.WindowsHeight > screenHeight) {
						y = initialY + deltaY;
						initialY += deltaY; 
					}
				}
			}

			// lanzo todos los monitores
			for (int i=0 ; i<mon.size() ; i++) 
			{
				sout("lanzo monitor: "+mon.get(i).getNombre());
				mon.get(i).Monitorizar();	
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,	e.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void sout(Object o) 
	{
		System.out.println(o);
	}

	/**
	 * Cierra la ventana
	 *
	 */
	private void salir()
	{
		dispose();		
		System.exit(0);
	}	
	
	/**
	 * Pausa los monitores
	 *
	 */
	private void pausa() 
	{
		pausa = true;
		for (int i=0 ; i<mon.size() ; i++) 
		{
			mon.get(i).pausa();			
		}
	}
	
	/**
	 * Reanuda los monitores
	 *
	 */
	private void reanudar()
	{
		pausa = false;
		for (int i=0 ; i<mon.size() ; i++) 
		{
			mon.get(i).reanudar();			
		}
	}	
}
