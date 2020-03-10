package handin;

import java.util.Random;

public class Machine implements Runnable{
    private boolean isRunning;
    private int minTemp, maxTemp;
    private int currentTemp;
    private Cooler connectedCooler;
//    private Thread t;

    public Machine(int minTemp, int maxTemp)
    {
        isRunning = false; //control variable to check if machine is running
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        currentTemp = 25; //room temp
        connectedCooler = null;
    }

    //method to start a machine in a thread
    public void startMachine()
    {
        if(!isRunning)
        {
            Thread t = new Thread(this);
//            t = new Thread(this);
            t.start();
            isRunning = true;
            currentTemp = 25;
//            System.out.println("Machine starts. " + t.getName());
        }
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    //method to stop the machine from running and disconnect cooler
    public void stopMachine()
    {
        if (isRunning)
            isRunning = false;
        disconnectCooler();
    }

    public synchronized int getCurrentTemp()
    {
        return currentTemp;
    }

    public int getMinTemp()
    {
        return minTemp;
    }

    public int getMaxTemp()
    {
        return maxTemp;
    }

    //method to connect machine to a cooler
    public synchronized boolean connectCooler(Cooler cooler)
    {
        if (!isCoolerConnected())
        {
            connectedCooler = cooler;
            return true;
        }
        return false;
    }

    public boolean isCoolerConnected()
    {
        return connectedCooler != null;
    }

    public void disconnectCooler()
    {
        if (isCoolerConnected())
            connectedCooler = null;
    }

    @Override
    public void run()
    {
        Random rand = new Random();

        while (isRunning)
        {
//            System.out.println("Temperature @ " + getCurrentTemp() + " " + t.getName());
            int random = rand.nextInt(6);
            
            synchronized(this)
            {
                if (!isCoolerConnected())
                    currentTemp += random; //increase current temperature if cooler is not connected
                else
                    currentTemp -= connectedCooler.getCoolingFactor(); //decrease current temperature if cooler is connected
            }
            
            try
            {
                //stop the machine if current temperate is overheated or overcooled
                if (getCurrentTemp() > getMaxTemp() || getCurrentTemp() < getMinTemp())
                {
                    stopMachine();
//                    System.out.println("Machine died. " + t.getName());
                    throw new MachineTemparatureException("Machine overheats or overcools. Machine died.");
                }
            }
            catch (MachineTemparatureException e)
            {
                System.out.println(e.toString());
            }

            try
            {
                Thread.sleep(200);
            }
            catch (InterruptedException ex){}
        }
    }
}
