package persistencia;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class xmlConfig {
	
	/*
	 * Nombre del archivo de configuración
	 * Ubicado en el directorio raíz de la aplicación
	 * */
	static private String configXML = "config.xml";
	
	private Document getXMLConfig() throws Exception {
		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder db =  df.newDocumentBuilder();
		
		String ruta = CurrentDir.getCurrentDir();
				
		File f = new File(ruta + "\\" + configXML);
		
		return db.parse(f);
	}

	public ArrayList<String> getOperaciones() throws Exception {
		ArrayList<String> res = new ArrayList<String>();
		
		Document d = getXMLConfig();
		
		NodeList conectList = d.getElementsByTagName("query");
		
		for (int i = 0; i < conectList.getLength(); i++){
	    	Node con = conectList.item(i);
	    	NamedNodeMap map = con.getAttributes();
	    	Node nodeCon = map.getNamedItem("name");		
			String nomValue = nodeCon.getNodeValue();
			res.add(nomValue);
		}
		
		return res;
	}
	
	/*
	 * retorna el atributo que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	private String getAtributoDelConector(String conector, String atributo) throws Exception {
				
		Document xmlPointer = getXMLConfig();
		NodeList conectList = xmlPointer.getElementsByTagName("connector");
	
	    for (int i = 0; i < conectList.getLength(); i++){
	    	Node con = conectList.item(i);
	    	// recorro todos los conectores que hay en el xml
	    	// y me fijo cual es el elegido
	    	
	    	NamedNodeMap map = con.getAttributes();
	    	Node nodeCon = map.getNamedItem("name");
	    	    	
	    	if (nodeCon.getNodeValue().equals(conector)) {	     	                   
	            return map.getNamedItem(atributo).getNodeValue();	           	
	    	}
	    }
	    
	    throw new Exception("No se encontraron datos de conexión en conf.xml para el manejador elegido.");
	}
	
	/*
	 * retorna el atributo que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	private String getAtributoDelGraficador(String graficador, String atributo) throws Exception {
				
		Document xmlPointer = getXMLConfig();
		NodeList conectList = xmlPointer.getElementsByTagName("chart");
	
	    for (int i = 0; i < conectList.getLength(); i++){
	    	Node con = conectList.item(i);
	    	// recorro todos los conectores que hay en el xml
	    	// y me fijo cual es el elegido
	    	
	    	NamedNodeMap map = con.getAttributes();
	    	Node nodeCon = map.getNamedItem("name");
	    	    	
	    	if (nodeCon.getNodeValue().equals(graficador)) {
	    		Node tmp = map.getNamedItem(atributo);
	    		if (tmp != null)
	    			return tmp.getNodeValue();
	    		else
	    			return null;
	    	}
	    }
	    
	    throw new Exception("No se encontraron datos de conexión en conf.xml para el manejador elegido.");
	}
	
	/*
	 * retorna el nombre de usuario que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	public String getConnectionString(String conector) throws Exception {
		return getAtributoDelConector(conector,"connectionString");
	}
	
	/*
	 * retorna el nombre de usuario que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	public String getUserName(String conector) throws Exception {
		return getAtributoDelConector(conector,"user");
	}
	
	/*
	 * retorna el Password que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	public String getPassword(String conector) throws Exception {
		return getAtributoDelConector(conector,"password");
	}

	/*
	 * retorna el Puerto que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	public String getPuerto(String conector) throws Exception {
		return getAtributoDelConector(conector,"port");
	}
	
	/*
	 * retorna el IP que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	public String getIP(String conector) throws Exception {
		return getAtributoDelConector(conector,"ip");
	}	

	/*
	 * retorna el nombre de la Base de Datos que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	public String getNombreBD(String conector) throws Exception {
		return getAtributoDelConector(conector,"DataBaseName");
	}

	/*
	 * retorna el nombre de la Base de Datos que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	public String getUrlJDBC(String conector) throws Exception {
		return getAtributoDelConector(conector,"connectionString");
	}
	
	/*
	 * retorna el nombre de la Base de Datos que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	public String getClassforname(String conector) throws Exception {
		return getAtributoDelConector(conector,"classforname");
	}
	
	/*
	 * retorna el separador ('/' o ':') que corresponde al conector pasado por parámetro
	 * según el config.xml
	 * */
	public String getSeparador(String conector) throws Exception {
		return getAtributoDelConector(conector,"separador");
	}
	
	/*
	 * Dada una operación devuelve el archivo con la consulta SQL relacionada
	 * */
	public String getArchivoConsultaSQL(String operacion) throws Exception {

		Document xmlPointer = getXMLConfig();
		NodeList conectList = xmlPointer.getElementsByTagName("query");

	    for (int i = 0; i < conectList.getLength(); i++){
	    	Node con = conectList.item(i);
	    	// recorro todas las consultas que hay en el xml
	    	// y me fijo cual fue el que seleccionaron
	    	
	    	NamedNodeMap map = con.getAttributes();
	    	Node nodeCon = map.getNamedItem("name");
	    	    	
	    	if (nodeCon.getNodeValue().equals(operacion)) {
	     	    
	            return map.getNamedItem("sql").getNodeValue();
	    	}
	    }
	    
	    throw new Exception("No se encontraron los datos de la operación elegida.");
	}
	
	
	public ArrayList<String> getNombresDeGraficadores() throws Exception {
		
		ArrayList<String> res = new ArrayList<String>();
		Document d = getXMLConfig();
		NodeList conectList = d.getElementsByTagName("chart");
		
		for (int i = 0; i < conectList.getLength(); i++){
	    	Node con = conectList.item(i);
	    	NamedNodeMap map = con.getAttributes();
	    	Node nodeCon = map.getNamedItem("name");		
			String nom = nodeCon.getNodeValue();
			res.add(nom);
		}
		
		return res;
	}

	public String getTimeDescanso(String graficador) throws Exception {
		return getAtributoDelGraficador(graficador,"intervalBetweenQuery");
	}
	
	public String getConsulta(String graficador) throws Exception {
		return getAtributoDelGraficador(graficador,"query");	
	}
	
	public String getParamDefinition(String graficador) throws Exception {
		return getAtributoDelGraficador(graficador,"paramDefinition");	
	}

	public int getCntConsultas(String graficador) throws Exception {
		String aux = getAtributoDelGraficador(graficador,"chartUpdateNumber");
		return (new Integer(aux)).intValue();
	}

	public String getTipoGrafico(String graficador) throws Exception {
		return getAtributoDelGraficador(graficador,"chartType");	
	}

	public int getxAxisColumnId(String graficador) throws Exception {
		String aux = getAtributoDelGraficador(graficador,"xAxisColumnId");
		if (aux == null || aux == "") {
				return - 1;
		}
		
		return (new Integer(aux)).intValue();
	}

	public String getSkipColumnsInChart(String graficador) throws Exception {
		return getAtributoDelGraficador(graficador,"skipColumnsInChart");
	}

	public int getChartBufferSize(String graficador) throws Exception {
		String aux = getAtributoDelGraficador(graficador, "chartBufferSize");
		if (aux == null)
				return 10000;
		
		return (new Integer(aux)).intValue();
	}
}
