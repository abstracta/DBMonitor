package persistencia.postProcesors;

public class MaxOfColumn extends PostProcesor 
{
	private int _columnId;
	private int _maxValue;

	public MaxOfColumn(int columnId, int defaultValue) 
	{
		this._columnId = columnId;
		this._maxValue = defaultValue;
	}
	
	public int getMaxValue() 
	{
		return this._maxValue;
	}
	
	public int getColId() 
	{
		return this._columnId;
	}

	@Override
	public void postProcessValue(int columnId, String value) 
	{
		if (this._columnId == columnId) 
		{
			int ival = Integer.parseInt(value.trim());
			if (ival > _maxValue) 
			{
				_maxValue = ival;
			}
		}
	}
}
