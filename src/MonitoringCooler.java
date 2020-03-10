package handin;

import java.util.Collection;
import java.util.Iterator;

public class MonitoringCooler implements Cooler, Runnable {
    private Collection<Machine> machines;
    private int coolingFactor;
    private boolean isStop;
    private boolean isConnected;
//    private Thread t;
    
    public MonitoringCooler(Collection<Machine> c, int coolingFactor)
    {
        machines = c;
        this.coolingFactor = coolingFactor;
        isStop = false; //control variable to stop the thread
        isConnected = false; //control variable to check if cooler is connected to any machine
    }

    //method to start the cooler in a thread
    public void startCooler()
    {
        Thread t = new Thread(this);
        t.start();
        isStop = false;
//        System.out.println("Cooler is operating. " + t.getName());
    }

    //method to stop a thread
    public void requestStop()
    {
        isStop = true;
        isConnected = false;
//        System.out.println("Cooler stops operating. " + t.getName());
    }

    @Override
    public void run()
    {
        Iterator<Machine> it = machines.iterator();
        Machine connectMachine = null;
        
        while (!isStop)
        {
            if (!isConnectedToMachine())
            {
                //if no cooler is connected, either grab a new iterator if it runs out or monitor the temperature of all machines
                if (!it.hasNext()) //if the iterator runs out, take it again
                    it = machines.iterator();
                else
                {
                    Machine m = it.next();
                    if (m.isRunning() && !m.isCoolerConnected())
                    {
                        if ((m.getMaxTemp() - DANGER_ZONE) < m.getCurrentTemp()) //if machine m current temperature is in hot danger zone, connect cooler
                        {
                            isConnected = m.connectCooler(this);
                            connectMachine = m; //save the machine reference
//                            System.out.println("Cooler connected. " + t.getName());
                        }
                    }
                }
            }
            else
            {
                //if cooler is connected to a machine check the temperature of the machine only
                if ((connectMachine.getMinTemp() + DANGER_ZONE) > connectMachine.getCurrentTemp()) //if machine connectMachine is in cold danger zone, disconnect cooler
                {
                    connectMachine.disconnectCooler();
                    isConnected = false;
//                    System.out.println("Cooler disconnected. " + t.getName());
                } 
            }
            
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException ex) {}
        }
    }

    @Override
    public int getCoolingFactor()
    {
        return coolingFactor;
    }

    @Override
    public boolean isConnectedToMachine()
    {
        return isConnected;
    }
}
