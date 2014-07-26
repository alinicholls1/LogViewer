/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package codexlogviewer;

import java.util.ArrayList;

/**
 * A list in which to store the LogEntry objects.
 * 
 * @author alastairnicholls
 */
public class LogList {
    
    /**
     * Different log lists should be made dependent on category;
     * e.g user may want to view list without system messages,
     * or uncategorised messages.
     */
    
    ArrayList<LogEntry> loglist = null;
    
    public void LogList()
    {
        loglist = new ArrayList();      
    }
    
    public void addLogToList(LogEntry logentry)
    {
        loglist.add(logentry);
    }
    
    public void getLogEntriesAsStrings()
    {
        for(LogEntry logentry : loglist)
        {
            logentry.getLogEntryAsString();
        }
        
    }
    
}
