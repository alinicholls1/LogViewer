/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package codexlogviewer;

import java.util.ArrayList;
import java.util.Calendar;
/**
 * Each LogEntry represents a line in a log file.
 * 
 * @author alastairnicholls
 */
public class LogEntry {
    
    ArrayList<Object> logEntry;
    
    String cal;
    long time;
    String category;
    String content;
    String sourceLog;
    String notes;
    
    public LogEntry() 
    {
        // Initialise empty logEntry
        logEntry = new ArrayList<>();
        
        // Initialise variables for logEntry
        cal = null;
        time = 0;
        category = null;
        content = null;
        sourceLog = null;
        // Set notes by default as None.
        notes = "None.";
        
        // Populate logEntry
        logEntry.add(cal);
        logEntry.add(time);
        logEntry.add(category);
        logEntry.add(content);
        logEntry.add(sourceLog);
        logEntry.add(notes);
        
    }
    
    public void setCal(String cal)
    {
        this.cal = cal;
    }
    
    public void setTime(long time)
    {
        this.time = time;
    }
    
    public void setCategory(String category)
    {
        this.category = category;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public void setSourceLog(String sourceLog)
    {
        this.sourceLog = sourceLog;
    }
    
    public void setNotes(String notes)
    {
        this.notes = notes;
    }
    
    public ArrayList<Object> getLogEntries()
    {
        return logEntry;
    }
    
    public Object getCal() 
    {
        return cal;
    }
    
    public Object getTime() 
    {
        return time;
    }
    
    public long getTimeLong()
    {
        return time;
    }
    
    public Object getCategory() 
    {
        return category;
    }
    
    public Object getContent() 
    {
        return content;
    }
    
    public String getContentAsString() 
    {
        return content;
    }
    
    public Object getSourceLog() 
    {
        return sourceLog;
    }
    
    public String getSourceLogAsString() 
    {
        return sourceLog;
    }
    
    public Object getNotes() 
    {
        return notes;
    }
    
    public String getLogEntryAsString()
    {
        String comma = ",";
        String formattedline;
        formattedline = getTime().toString() +
                        comma +
                        getCal() +
                        comma +
                        getContent().toString() +
                        comma +
                        getNotes().toString() +
                        comma +
                        getSourceLog().toString() +
                        comma +
                        getCategory().toString();
                        
        
        return formattedline;
        
    }
    
}
