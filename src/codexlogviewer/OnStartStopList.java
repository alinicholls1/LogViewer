/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package codexlogviewer;

import java.util.ArrayList;

/**
 *
 * @author alastairnicholls
 */
public class OnStartStopList {
    
    ArrayList<OnStartStop> onstartstoplist;
    
    public OnStartStopList()
    {
        onstartstoplist = new ArrayList();
    }
    
    public void add(OnStartStop onstartstop)
    {
        onstartstoplist.add(onstartstop);
    }
    
    public void printList()
    {
        for(OnStartStop onstartstop : onstartstoplist)
            System.out.println(onstartstop);
    }
    
    
}
