package gui.logicaGrafico;

public class Normal extends Operacion 
{
	private Long[] d;
	private int pos;

	public Normal(Long[] buffer) 
	{
		this.d = buffer;
		this.pos = 0;
	}
	
	public boolean hasNext() 
	{
		return d.length > pos;
	}
	
	public Long next() 
	{
		if (hasNext()) 
		{
			pos++;
		}
		
		return (d[pos-1]);
	}
}
