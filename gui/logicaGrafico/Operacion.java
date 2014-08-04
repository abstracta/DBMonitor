package gui.logicaGrafico;

public abstract class Operacion {

	public static Operacion getOperacion(String nombre, Long[] list) 
	{
		Operacion op = null;
		if (nombre.equals("Normal")) 
		{
			op = new Normal(list);
		} 
		else if (nombre.equals("Diferencias")) 
		{
			op = new Diferencia(list);
		}
		return op;
	}
	
	public abstract boolean hasNext();
	
	public abstract Long next();
}
