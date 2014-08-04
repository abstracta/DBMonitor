package gui;

import gui.logicaGrafico.Operacion;

import java.awt.Dimension;
import java.util.ArrayList;

import logica.DataDatos;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class Grafico extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	private ArrayList <String> series;

	private String chartTitle; 
	
	private String tipoGrafico;
	
	private ChartPanel chartPanel;
		
	public static int WindowsWidth = 500;
	
	public static int WindowsHeight = 270;
	
	public Grafico(final String title, DataDatos buff, ArrayList <String> Series, String tipoGrafico, int xPosition, int yPosition) 
	{
		super(title);

		this.chartTitle = title;
		this.series = Series;
		this.tipoGrafico = tipoGrafico;
		
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle,
				"Tiempo", "Milisegunds", createDataset(buff), true, true, false);

		final XYPlot plot = chart.getXYPlot();
		plot.getDomainAxis().setVisible(true);
		plot.getRangeAxis().setVisible(true);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(WindowsWidth, WindowsHeight));
		
		setContentPane(chartPanel);
		setLocation(xPosition, yPosition);
	}
	
	public void refrescarGrafico(DataDatos buff) 
	{
		final XYDataset dataset = createDataset(buff);
		
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle,
				"Tiempo", "Unidades", dataset, true, true, false);

		chartPanel.setChart(chart);
	}
		
	public XYDataset createDataset(DataDatos buff) 
	{
		final XYSeriesCollection dataset = new XYSeriesCollection();
		Long[][] matr = (Long[][])buff.getDataList();
/*
		sout("-------------------------------");
		sout("-- Matr size: " + matr.size() + " --");
		sout("-------------------------------");
//*/
		for (int i = 1; i < matr.length; i++) 
		{
			dataset.addSeries(createXYSeries(this.series.get(i), matr[i], matr[0]));
		}
		
		return dataset;
	}

	public XYSeries createXYSeries(final String series, final Long[] dats, final Long[] ejex) 
	{
		// XYSeries(java.lang.Comparable key, boolean autoSort, boolean allowDuplicateXValues) 
		final XYSeries result = new XYSeries(series, false, true);
		Operacion op = Operacion.getOperacion(this.tipoGrafico, dats);

		for (int i = 0 ; (i < dats.length) && (ejex[i] != null); i++) 
		{
			Long x = ejex[i];
			Long y = op.next();
			
			result.add(x,y);
		}

		return result;
	}
	
	public void sout(Object o) 
	{
		System.out.println(o);
	}
}
