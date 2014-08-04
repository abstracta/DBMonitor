package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import persistencia.postProcesors.MaxOfColumn;
import persistencia.postProcesors.PostProcesor;

public class ExecuteQuery {
	private static final String MaxOfColumnFunctionName = "maxOfColumn";
	
	private static final String NowFunctionName = "now";
	
	private int _cntRes;

	private Statement _st;

	private ArrayList<PostProcesor> _pps = null;

	private String _connectorName;

	private String _params;
	
	private int _columnIndex;
	
	private int[] _skipColumns;

	private String[] _functionsWithPostExecution = new String[] { MaxOfColumnFunctionName };

	public ExecuteQuery(String connectorName, String params, int columnIndex, String skipColumnInChart) {
		this._st = null;
		this._cntRes = -1;
		this._connectorName = connectorName;
		this._params = params;
		this._pps = new ArrayList<PostProcesor>();
		this._columnIndex = columnIndex;

		String[] columnsToSkip;
		if (skipColumnInChart != null) {
			columnsToSkip = skipColumnInChart.split(",");
		} else { 
			columnsToSkip = new String[0];
		}
		
		int skipColumnsSize = columnsToSkip.length + (columnIndex > 0 ? 1 : 0);
		this._skipColumns = new int[skipColumnsSize];
		
		for(int i=0; i < columnsToSkip.length; i++) {
			try {
				this._skipColumns[i] = new Integer(columnsToSkip[i].trim());
			} catch (Exception e) {
				this._skipColumns[i] = -1;
			}
		}
		
		if (columnIndex > 0) {
			_skipColumns[columnsToSkip.length] = columnIndex;
		}
				
		if (params != null && params != "") {
			// params = "LAST_ID = maxOfColumn(columnIndex, 0), MAX_RECEIVED_DATE = now()"
			for (String functionName : _functionsWithPostExecution) {
				int flen = functionName.length() + 1;
	
				int functionParamsStart = params.indexOf(functionName + "(");
	
				while (functionParamsStart > -1) {
					functionParamsStart += flen;
					
					int functionParamsEnd = params.indexOf(")", functionParamsStart);
					String functionParams = params.substring(functionParamsStart, functionParamsEnd);
	
					switch (functionName) {
					case MaxOfColumnFunctionName:
						String[] tmp = functionParams.split(",");
						int columnId = Integer.parseInt(tmp[0].trim());
						int defValue = Integer.parseInt(tmp[1].trim());
	
						this._pps.add(new MaxOfColumn(columnId, defValue));
						break;
	
					default:
						break;
					}
	
					functionParamsStart = params.indexOf(functionName + "(", functionParamsEnd);
				}
			}
		}
	}

	/*
	 * Connects to the DBMS server
	 */
	public void connectServer() throws Exception {
		if (_st == null) {
			xmlConfig xml = new xmlConfig();

			Connection conection;

			String classForName = xml.getClassforname(_connectorName);
			Class.forName(classForName);

			String url = xml.getUrlJDBC(_connectorName);

			if (url != null && url != "") {
				conection = DriverManager.getConnection(url);
			} else {
				String userName = xml.getUserName(_connectorName);
				String password = xml.getPassword(_connectorName);
				String port = xml.getPuerto(_connectorName);

				String ip = xml.getIP(_connectorName);
				String nomBD = xml.getNombreBD(_connectorName);
				String urlJDBC = xml.getUrlJDBC(_connectorName);

				String separador = xml.getSeparador(_connectorName);

				url = urlJDBC + ip + ":" + port + separador + nomBD;

				Class.forName(classForName);
				conection = DriverManager
						.getConnection(url, userName, password);
			}

			this._st = conection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
		}
	}

	public ArrayList<ArrayList<Object>> executeQueryFirstTime(String query)
			throws Exception {
		// if query has parameters, replace them with the correct values
		
		query = preProcessQuery(query);
		
		ArrayList<ArrayList<Object>> res = new ArrayList<ArrayList<Object>>();

		if (this._st == null) {
			throw new Exception("No hay una conexión establecida.");
		}

		ResultSet rs = this._st.executeQuery(query);

		// Get result set meta data
		ResultSetMetaData rsmd = rs.getMetaData();
		int numColumns = rsmd.getColumnCount();

		// Get the column names; column indices start from 1
		ArrayList<Object> aux = new ArrayList<Object>();
		aux.add("TimeStamp");
		for (int i = 1; i <= numColumns; i++) {
			if (!ListContains(i, _skipColumns))
			{
				aux.add(rsmd.getColumnName(i));
			}
		}

		this._cntRes = numColumns;

		res.add(aux);
		res.addAll(processResultSet(rs));

		return res;
	}

	private boolean ListContains(int value, int[] list) {
		for (int i=0; i<list.length; i++) {
			if (list[i] == value) {
				return true;
			}
		}
		
		return false;
	}

	public ArrayList<ArrayList<Object>> executeQuery(String query) throws Exception {
		query = preProcessQuery(query);

		if (this._st == null) {
			throw new Exception("No hay una conexión establecida.");
		}

		ResultSet rs = this._st.executeQuery(query);

		return processResultSet(rs);
	}

	private String preProcessQuery(String query) {

		// System.out.println("Execute query: " + query);
		
		// "SELECT MESSAGE_ID, RECEIVED_DATE, SEND_DATE, RESPONSE_DATE FROM [mirthdb].[dbo].[D_MM1] WHERE MESSAGE_ID > ${LAST_ID} AND RECEIVED_DATE < ${MAX_RECEIVED_DATE};"
		// "LAST_ID = maxOfColumn(1, 0) ; MAX_RECEIVED_DATE = now()"
		while (containsAParam(query)) {
			String param = getFirstParam(query);
			String result = calculateParameterValue(param);

			if (result != null) {
				query = query.replace("${" + param + "}", result);
			}
		}

		System.out.println("Procesed query: " + query);

		return query;
	}

	private String calculateParameterValue(String param) {
		// params:
		// "LAST_ID = maxOfColumn(MESSAGE_ID, 0) ; MAX_RECEIVED_DATE = now()"

		int functionStart = _params.indexOf(param + " = ") + param.length() + 2;
		int functionEnd = _params.indexOf("(", functionStart);
		String functionName = _params.substring(functionStart + 1, functionEnd);

		String replaceVar = null;

		switch (functionName) {
		case MaxOfColumnFunctionName:
			int arg1Start = functionEnd + 1;
			int arg1End = _params.indexOf(",", functionEnd + 1);
			int arg1 = Integer.parseInt(_params.substring(arg1Start, arg1End));

			int arg2Start = arg1End + 2;
			int arg2End = _params.indexOf(")", arg2Start);
			int arg2 = Integer.parseInt(_params.substring(arg2Start, arg2End));

			replaceVar = String.valueOf(maxOfColumn(arg1, arg2));
			break;

		case NowFunctionName:
			replaceVar = "'" + new Date().toString() + "'";
			break;

		default:
			break;
		}

		return replaceVar;
	}

	private String getFirstParam(String query) {
		int principioVar = query.indexOf("${");
		int finVar = query.indexOf("}", principioVar);

		return query.substring(principioVar + 2, finVar);
	}

	private boolean containsAParam(String query) {
		return query.indexOf("${") > 0;
	}

	private int maxOfColumn(int columndId, int defaultValue) {
		for (PostProcesor pp : _pps) {
			if (pp.getClass() == MaxOfColumn.class) {
				
				MaxOfColumn moc = (MaxOfColumn) pp;
				if (moc.getColId() == columndId) {
					return moc.getMaxValue();
				}
			}
		}

		System.out.println("Return def value: " + defaultValue);

		return defaultValue;
	}

	private ArrayList<ArrayList<Object>> processResultSet(ResultSet rs) throws SQLException {
		ArrayList<ArrayList<Object>> res = new ArrayList<ArrayList<Object>>();

		if (rs.first()) {
			do {
				ArrayList<Object> aux = new ArrayList<Object>(); 
				
				long d;
				if (_columnIndex > 0) {
					d = rs.getTimestamp(_columnIndex).getTime();
				}
				else 
				{
					d = new Date().getTime();
				}
				
				aux.add("" + d);
				for (int i = 1; i < _cntRes + 1; i++) {
					String rsValue = rs.getString(i);
					
					if (!ListContains(i, _skipColumns))
					{
						aux.add(rsValue);
					}
					
					for (PostProcesor pp : _pps) {
						pp.postProcessValue(i, rsValue);
					}
	
				}

				res.add(aux);
			} while (rs.next());
		}

		return res;
	}

	public int getCntRes() {
		return _cntRes;
	}
}
