package persistencia.postProcesors;

public abstract class PostProcesor 
{
	public abstract void postProcessValue(int columnId, String value);
}