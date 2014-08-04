package logica;

import java.sql.Timestamp;
import java.util.ArrayList;

public class DataDatos 
{
	private long maxX, maxY, minX, minY;
	
	// first array contains columns. Each column contains rows.
	private Object[][] buff;
	
	private ArrayList<String> Series = null;
	
	public long getMaxX() { return this.maxX; }
	public long getMaxY() { return this.maxY; }
	public long getMinX() { return this.minX; }
	public long getMinY() { return this.minY; }
	
	private int _firstIndex, _lastIndex, _rowsCount, _columnsCount;

	public DataDatos(ArrayList<ArrayList<Object>> originData, int columnsCount, int rowsCount) throws Exception
	{
		/*
		 * originData hast the following format:
		 * "Timestamp", "<col1>", "<col2>", .....
		 * 10000000,105,203, .....
		 * 10000010,106,243, .....
		 * .....
		 * .....
		 * 
		 * We need to parse the information to generate the chart. 
		 * We need also to know min and max of each axes of the chart.
		 * 
		 * For the 'x' axes we'll use Timestamp.
		 * For the 'y' axes we'll use max and min of all rest of data.
		 */
		
		this._rowsCount = rowsCount;
		this._columnsCount = columnsCount;
		this._firstIndex = this._lastIndex = 0;
		
		this.maxX = Integer.MIN_VALUE;
		this.minX = Integer.MAX_VALUE;
		
		this.maxY = Integer.MIN_VALUE;
		this.minY = Integer.MAX_VALUE;

		// initialize matrix
		this.buff = new Object[columnsCount][];
		for (int i = 0; i < columnsCount; i++) 
		{
			this.buff[i] = new Object[rowsCount];
		}
		
		if (originData.size() == 0) 
		{
			return;
		}

		// j==0, is column 0, there is timestamp 
		// i==0, is row 0, it may contain series of chart 	
		ArrayList<Object> firstLine = originData.get(0);
		
		int ini = 0;
		
		// if first line contains Strings, then fist line contais the Series of the chart
		if (firstLine.get(0).getClass() == String.class) 
		{
			this.Series = getSeries(firstLine);
			ini = 1;
		}
		
		Long difference = new Long(22089888)*10*10*10*10*10 - 13484000;
		
		// for each row that came in the data
		for (int i=ini; i < originData.size(); i++) 
		{
			ArrayList<Object> aux = originData.get(i);
			Object[] newRow = new Object[_columnsCount];
			
			// transform and cast data in columns to create a new row to insert in my matrix
			for (int j=0; j < aux.size(); j++) 
			{
				String val = (String)aux.get(j);
				Long value;
				
				// "1900-01-01 00:00:00.79"
				if (val.startsWith("1900-")) 
				{
					Timestamp ts = Timestamp.valueOf(val);
					value = ts.getTime() + difference;
				}
				else if (val.startsWith("1970-")) 
				{
					Timestamp ts = Timestamp.valueOf(val);
					value = ts.getTime();
				}
				else 
				{
					value = new Long(val);
				}
				
				newRow[j] = value;
				
				// update when column isn't the TimeStamp column (column 0)
				if (j != 0) 
				{
					if (value < this.minY) this.minY = value;
					if (value > this.maxY) this.maxY = value;
				}
			}
			
			insertInArray(newRow);
		}
		
		// save the smallest and bigest timestamps
		if (buff.length > 0) 
		{
			this.minX = (Long)buff[0][0];
			this.maxX = (Long)buff[0][getLastIndex()];
		}
	}
		
	// return sorted list
	public Long[][] getDataList() 
	{ 
		// create result
		Long[][] result = new Long[_columnsCount][];
		for (int i = 0; i < _columnsCount; i++) 
		{
			result[i] = new Long[_rowsCount];
		}
		
		// fill result with the values
		for (int i1 =_firstIndex, i2 = 0; i1 != _lastIndex; i1 = inc(i1), i2++) 
		{
			for (int j = 0; j < _columnsCount; j++) 
			{
				result[j][i2] = (Long)buff[j][i1];
			}
		}
		
		return result; 
	}
	
	public int getNumberOfRows() {
		return (_lastIndex < _firstIndex) ? _rowsCount : _lastIndex;
	}
	
	public boolean isEmpty() {
		return getNumberOfRows() == 0;
	}
	
	public Object[] getRow(int index) {
		Object[] result = new Object[_columnsCount];
		
		int row = (_firstIndex + index) % _rowsCount;
		for (int i = 0; i < _columnsCount; i++) {
			result[i] = buff[i][row];
		}
		
		return result;
	}
	
	public ArrayList<String> getSeries() 
	{
		return this.Series;
	}
		
	
	public void add(DataDatos aux) throws Exception
	{
		if (aux.isEmpty()) 
		{
			return;
		}
		
		if (this.maxX < aux.getMaxX()) this.maxX = aux.getMaxX();
		if (this.maxY < aux.getMaxY()) this.maxY = aux.getMaxY();
		
		if (this.minX > aux.getMinX()) this.minX = aux.getMinX();
		if (this.minY > aux.getMinY()) this.minY = aux.getMinY();

		for (int i = 0; i < aux.getNumberOfRows(); i++) {
			Object[] rowToAdd = aux.getRow(i);
			insertInArray(rowToAdd);
		}
	}
	
	
	public void insertInArray(ArrayList<Object> values) throws Exception
	{
		insertInArray(values.toArray());
	}
	
	public void insertInArray(Object[] values) throws Exception
	{
		if (values.length != _columnsCount) {
			throw new Exception("You tried to insert incorrect number of data in a cell of circular array");
		}

		for (int j = 0; j < _columnsCount; j++) 
		{
			buff[j][_lastIndex] = values[j];
		}
		
		_lastIndex = (_lastIndex + 1) % _rowsCount;
		if (_lastIndex == _firstIndex) {
			_firstIndex = (_firstIndex + 1) % _rowsCount;
		}
	}
	
	private int getLastIndex() {
		return (_lastIndex - 1) % _rowsCount;
	}

	private int inc(int i) {
		return (i + 1) % _rowsCount;
	}

	private ArrayList<String> getSeries(ArrayList<Object> primLinea) 
	{
		ArrayList<String> res = new ArrayList<String>();
		
		for (int i=0 ; i<primLinea.size() ; i++) 
		{
			res.add((String)primLinea.get(i));
		}
		
		return res;
	}
		
	public String toString() {
		String res = "";
		
		for (int i=0; i < this.buff.length; i++) {
			Object[] tmp = buff[i]; 
			for (int j=0; j < tmp.length ; j++) {
				res = res + " - " + tmp[j].toString();
			}
			res = res + "\n";
		}
		
		return res;
	}
}
