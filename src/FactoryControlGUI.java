/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

/**
 *
 * @author jmm4115
 */
public class FactoryControlGUI extends JPanel implements ActionListener {
    private static final int NUM_MACHINE = 50;
    private static final int NUM_COOLER = 20;
    private static final int MIN_TEMP = 0;
    private static final int MAX_TEMP = 250;
    private static final int COOLING_FACTOR = 25;
    private DrawPanel drawPanel;
    private JRadioButton start, stop;
    private Collection<Machine> machines;
    private Collection<MonitoringCooler> coolers;
    private Timer timer;
    
    public FactoryControlGUI(Collection<Machine> machines, Collection<MonitoringCooler> coolers)
    {
        super(new BorderLayout());
        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
        
        start = new JRadioButton("Start");
        stop = new JRadioButton("Stop", true);
        start.addActionListener(this);
        stop.addActionListener(this);
        ButtonGroup buttons = new ButtonGroup();
        buttons.add(start);
        buttons.add(stop);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(start);
        buttonPanel.add(stop);
        add(buttonPanel, BorderLayout.NORTH);
        
        this.machines = machines;
        this.coolers = coolers;
        timer = new Timer(20, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == start) //start all machines, all coolers and timer
        {
            for (Machine m: machines)
                m.startMachine();
            
            for (MonitoringCooler mc: coolers)
                mc.startCooler();
            
            timer.start();
        }
        else if (source == stop) //stop all machines, all coolers and timer
        {
            for (Machine m: machines)
                m.stopMachine();
            
            for (MonitoringCooler mc: coolers)
                mc.requestStop();
            
            timer.stop();
        }
        drawPanel.repaint();
    }
    
    private class DrawPanel extends JPanel
    {
        private final int WIDTH = 550;
        private final int HEIGHT = 550;
        
        public DrawPanel()
        {
            super();
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(Color.WHITE);
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawString("GRAPH OF MACHINES TEMPERATURE IN C", (WIDTH / 2) - 120, 15);
            final int LINE_X2 = WIDTH - 25; //space from the right for string
            final int STRING_X = LINE_X2 + 2; //x: starting point of string
            final int LINE_Y = 40; //250 line
            final int FACTOR = (HEIGHT - 50) / 250; //scaling temperature to match the screen's height. temperature:height = 1:2 in this case
            
            g.drawLine(0, LINE_Y, LINE_X2, LINE_Y); //250 line
            g.drawString("250", STRING_X, LINE_Y);
            
            int lineYPosition = (250 - 125) * FACTOR + LINE_Y; //height of line from the top
            g.drawLine(0, lineYPosition, LINE_X2, lineYPosition); //125 line
            g.drawString("125", STRING_X, lineYPosition);
            
            lineYPosition = (250 - 0) * FACTOR + LINE_Y;
            g.drawLine(0, lineYPosition, LINE_X2, lineYPosition); //0 line
            g.drawString("0", STRING_X, lineYPosition);
            
            lineYPosition = (250 - 200) * FACTOR + LINE_Y;
            g.setColor(Color.RED);
            g.drawLine(0, lineYPosition, LINE_X2, lineYPosition); //200 line
            g.drawString("200", STRING_X, lineYPosition);
            
            lineYPosition = (250 - 50) * FACTOR + LINE_Y;
            g.setColor(Color.BLUE);
            g.drawLine(0, lineYPosition, LINE_X2, lineYPosition); //50 line
            g.drawString("50", STRING_X, lineYPosition);
            
            int width = (WIDTH - 25) / machines.size() - 2; //the width of the bar for each machine
            drawMachine(g, 0, width, FACTOR, LINE_Y);
        }
        
        private void drawMachine(Graphics g, int startX, int width, int factor, int line_y)
        {
            for (Machine m: machines)
            {
                int temperature = m.getCurrentTemp();
                //line_y is the 250 line. 250 is the highest temp. Each temp is multiplied by factor to get y coord from 250 line.
                int y = ((250 - temperature) * factor) + line_y; //height of temperature from the top (y = 0);
                
                //set colour of brush depending on the temperature
                if (temperature < 50)
                    g.setColor(Color.BLUE);
                else if (temperature < 200)
                    g.setColor(Color.YELLOW);
                else
                    g.setColor(Color.RED);
                
                g.fillRect(startX, y, width, 540 - y); //540 is the height from top to 0 line.
                
                g.setColor(Color.BLACK);
                String isCoolerConnected = "-";
                if (m.isCoolerConnected())
                    isCoolerConnected = "+";
                g.drawString(isCoolerConnected, startX + (width / 2) - 2, 550);
                
                startX += width + 2; //create a small space between rectangle.
            }
        }
    }
    
    public static void main(String[] args)
    { 
        ArrayList<Machine> machines = new ArrayList<Machine>();
        for (int k = 0; k < NUM_MACHINE; k++)
            machines.add(new Machine(MIN_TEMP, MAX_TEMP));
        ArrayList<MonitoringCooler> coolers = new ArrayList<MonitoringCooler>();
        for (int k = 0; k < NUM_COOLER; k++)
            coolers.add(new MonitoringCooler(machines, COOLING_FACTOR));
        
        JFrame frame = new JFrame("GRAPH OF MACHINES BEING COOLED");
        // kill all threads when frame closes
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new FactoryControlGUI(machines, coolers));
        frame.pack();
        // position the frame in the middle of the screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenDimension = tk.getScreenSize();
        Dimension frameDimension = frame.getSize();
        frame.setLocation((screenDimension.width-frameDimension.width)/2,
           (screenDimension.height-frameDimension.height)/2);
        frame.setVisible(true);
    }
}
