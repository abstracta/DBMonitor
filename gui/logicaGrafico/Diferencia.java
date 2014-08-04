package gui.logicaGrafico;

public class Diferencia extends Operacion 
{
	private Long[] d;
	private int pos;

	private long total = 0;
	
	public Diferencia(Long[] buffer) 
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
		long tmp = d[pos]; 
		long res = tmp - total;
		total += res;
		if (hasNext()) 
		{
			pos++;
		}
		
		return res;
	}
}
