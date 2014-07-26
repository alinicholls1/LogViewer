/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package codexlogviewer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author alastairnicholls
 */
public class OnStartStop {
        
    Calendar start;
    Calendar stop;
    int on;
    ArrayList<Object> onstartstop;
    
    public OnStartStop()
    {
        onstartstop = new ArrayList();
        start = null;
        stop = null;
        on = 0;
    }
    
    public void fillOnStartStop(Calendar start, Calendar stop, int on)
    {
        this.start = start;
        this.stop = stop;
        this.on = on;
    }
    
    public void addStart(Calendar start)
    {
        this.start = start;
    }
    
    public void addStop(Calendar stop)
    {
        this.stop = stop;
    }
    
    public void addOn(int on)
    {
        this.on = on;
    }
    
}
