package logica;

public class Semaforo 
{
    private int count;
    
    public Semaforo(int n) 
    {
        count = n;
    }

    public synchronized void WAIT() 
    {
        while(count == 0) 
        {
            try 
            {
                wait();
            } 
            catch (InterruptedException e) 
            {
                // keep trying
            }
        }
        
        count--;
    }
    
    public synchronized void SIGNAL() 
    {
        count++;
        notify();
    }
    
    public void p() 
    {
    	this.WAIT();
    }

    public void v() 
    {
    	this.SIGNAL();
    }
}