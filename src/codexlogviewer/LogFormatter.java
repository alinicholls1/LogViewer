package codexlogviewer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Reads each line of the specified log files and turns 
 * it into a LogEntry object (calling DateFormatter, 
 * and LogCategoriser)
 * 
 * @author alastairnicholls
 */
public class LogFormatter
{
    Calendar lastgoodcalendar = null;
    ArrayList<Path> fullfilelist;
    Path dir = null;
    
    public LogFormatter() throws FileNotFoundException, IOException, ParseException
    {
        
    }
    
    public void format() throws FileNotFoundException, IOException, ParseException
    {
        
        ArrayList<LogEntry> loglist;
        loglist = new ArrayList();
        LogHelper helper = new LogHelper();
        int startups = 0;
        int shutdowns = 0;
        int crashes = 0;

        // Add all the files in the log folder to a list
        fullfilelist = new ArrayList<>();
        buildFileList();
        
        // Make an empty list for just the files we want to check
        ArrayList<String> filelist;
        filelist = new ArrayList<>();
  
        // Check which files match log files of interest and add to a list
        for(Path file : fullfilelist)
        {
            for(String match : logsOfInterest())
            {
                if(file.endsWith(match))
                {
                    filelist.add(file.toString());
                }
            }
        }

        // Set up variables for the formatting
        BufferedReader br;
        String line;
        String linenocommas;
        String linenoquotes;
        Calendar cal;
        Calendar calincrement;
        Calendar startup;
        Calendar shutdown;
        int startupmillis = 0;
        int shutdownmillis = 0;
        long millis;
        DateFormatter dateformatter = new DateFormatter();
        DateFormatter calformatter = new DateFormatter();
        String calstring;
        long millisincrement;
        Calendar now = Calendar.getInstance();
        setLastGoodCalendar(now);
        Calendar checkfrom = Calendar.getInstance();
        boolean checkfromset = false;
        
        // If there is a period of interest, set the checkfrom time
        if(periodOfInterest() > 0)
        {
            long nowmillis = now.getTimeInMillis();
            long startmillis = nowmillis - periodOfInterest();
            checkfrom.setTimeInMillis(startmillis);
            checkfromset = true;
        }
        
        // Perform the formatting
        for(String file : filelist)
        {
        br = new BufferedReader(new FileReader(file));
        line = br.readLine();
        while ((line = br.readLine()) != null)
        {
            // Create log entry
            LogEntry logentry = new LogEntry();
            // Change any colons to semicolons to make a sensible CSV file
            linenocommas = line.replace(',', ';');
            // Change any quotes to semicolons as they confuse Excel
            linenoquotes = linenocommas.replace('"', ';');
            // Set the Content
            logentry.setContent(linenoquotes);                        
            // Go through the checklist of things to watch out for
            for(String key : helper.getKeys() )
            {                
                // If the line contains a match, add the relevant note
                if(line.contains(key))
                {                                               
                    String notes = helper.getNotes(key);
                    logentry.setNotes(notes);
                }
            }
            // Check for startup events
            if(logentry.getContent().toString().contains("drserver starting"))
            {
                startups++;

            }
            // Check for shutdown events
            if(logentry.getContent().toString().contains("drserver stopping"))
            {
                shutdowns++;

            } 
            // Check for crash events 
            if(logentry.getContent().toString().contains("Trace/breakpoint"))
            {
                crashes++;

            } 
            // Remove the directory structure to get just the file name
            int lastdirectory = file.lastIndexOf('/');
            file = file.substring(lastdirectory + 1);
            logentry.setSourceLog(file);
            
            // Check the content to find the correct category and then set it
            String category = helper.findCategory(logentry.getContentAsString());            
            logentry.setCategory(category);           
            
            // Check if this is a codex.log or vaultui.log type file
            if(file.contains("codex.log") || file.contains("vaultui.log"))               
                // Check if there's a valid year at the start of the line
                if(line.startsWith("2014") || line.startsWith("2013"))
                {
                    // Get date and time from line and set in log entry
                    cal = dateformatter.getCalendarCodexLog(line);
                    millis = dateformatter.convertCalendarToMillis(cal);
                    setLastGoodCalendar(cal);
                    calstring = calformatter.calAsString(cal);
                    logentry.setCal(calstring);
                    logentry.setTime(millis);                      
                }            
                // If there's not a valid year, take the date from the last good one
                else           
                {
                    cal = getLastGoodCalendar();
                    millis = dateformatter.convertCalendarToMillis(cal);
                    calstring = calformatter.calAsString(cal);
                    logentry.setCal(calstring);
                    logentry.setTime(millis);
                    logentry.setNotes("Date/time est.");
                    // Increment the last good date in millis in case it is used again
                    millisincrement = millis + 10;
                    calincrement = Calendar.getInstance();
                    calincrement.setTimeInMillis(millisincrement);
                    setLastGoodCalendar(calincrement);                   
                } 
            // Check if this is a messages log file    
            if(file.contains("messages"))
            {
                // Get date and time from line and set in log entry
                    cal = dateformatter.getCalendarMessagesLog(line);
                    millis = dateformatter.convertCalendarToMillis(cal);
                    calstring = calformatter.calAsString(cal);
                    logentry.setCal(calstring);
                    logentry.setTime(millis);                   
            }
            // Check if this is a drserver log file
            if(file.contains("drserver"))
                // Check if there's a valid year bookended by spaces in the line
                if(line.contains(" 2014 ") || line.contains(" 2013 "))
                {
                    // Get date and time from line and set in log entry
                    cal = dateformatter.getCalendarDrserverLog(line);
                    millis = dateformatter.convertCalendarToMillis(cal);
                    calstring = calformatter.calAsString(cal);
                    setLastGoodCalendar(cal);
                    logentry.setCal(calstring);
                    logentry.setTime(millis);                                       
                }
                // If there's not a valid year, take the date from the last good one
                else           
                {
                    cal = getLastGoodCalendar();
                    millis = dateformatter.convertCalendarToMillis(cal);
                    calstring = calformatter.calAsString(cal);
                    logentry.setCal(calstring);
                    logentry.setTime(millis);
                    logentry.setNotes("Date/time est.");
                    // Increment the last good date in millis in case it is used again
                    millisincrement = millis + 10;
                    calincrement = Calendar.getInstance();
                    calincrement.setTimeInMillis(millisincrement);
                    setLastGoodCalendar(calincrement);
                } 
            
                if(line.contains("loading ID"))
                {
                    // Go through list of GUIDs and return the serial
                    for(String GUID : helper.getGUIDs() )
                    {                
                        if(line.contains(GUID))
                        {                           
                            String notes = helper.getSerial(GUID);
                            logentry.setNotes(notes);
                            break;
                        }
                    }
                }

            // Add the log entry to a list of log entries
            loglist.add(logentry);

            // category = LogCategoriser.categorise(line);
            // Call LogCategoriser on content
            // Call LogHelper on the content
            // Set sourceLog
        }
        // Done with formatting the log file
        br.close();
        }
        
        // Make a list of onstartstop events to add to the summary
        ArrayList<Long> startuplist = new ArrayList();
        ArrayList<Long> shutdownlist = new ArrayList();
        ArrayList<Calendar> poweronhours = new ArrayList();
        OnStartStopList onstartstoplist = new OnStartStopList();
        Calendar start = null;
        Calendar stop = null;
        int on;
            
        // Populate the startuplist and shutdownlist
        for(LogEntry logentry : loglist)
        {            
            if(logentry.getSourceLogAsString().equalsIgnoreCase("drserver.0.log"))
            {
                if(logentry.getContentAsString().contains("drserver starting"))
                {                                           
                    long time = logentry.getTimeLong();                    
                    startuplist.add(time);                    
                }
                if(logentry.getContentAsString().contains("drserver stopping"))
                {                    
                    long time = logentry.getTimeLong();                    
                    shutdownlist.add(time);                                       
                }                
            }
        }
        
        HashMap<Long, Long> updownevents = new HashMap();
        
        // For each shutdown event, check which startup comes before it 
        // and pair them in updownevents
        while(startuplist.size()>1)
        {
            search:
            for(long shutdowntime : shutdownlist)
            {
                for(long startuptime : startuplist)
                {
                    if(shutdowntime>startuptime)
                    {
                        updownevents.put(startuptime, shutdowntime);
                        // When a pairing is found, remove the first
                        // startup from the list so it isn't checked again
                        startuplist.remove(0);
                        break search;
                    }                        
                }
            }                
        }
        
        ArrayList<Long> powerontimes = new ArrayList();
        
        // Calculate the power on hours and make a list
        for (Map.Entry<Long, Long> entry : updownevents.entrySet()) 
        {
            Long startuptime = entry.getKey();
            Long shutdowntime = entry.getValue();
            Long powerontime = shutdowntime - startuptime;
            powerontimes.add(powerontime);
        }
        
        // Calculate the total power on time
        long powerontotal = 0;
        for(long powerontime : powerontimes)
        {
            powerontotal = powerontotal + powerontime;
        }
        
        int seconds = (int) (powerontotal / 1000) % 60 ;
        int minutes = (int) ((powerontotal / (1000*60)) % 60);
        int hours   = (int) ((powerontotal / (1000*60*60)) % 24);
        String powerontotalstring = hours + "h" + minutes + "m" + seconds + "s";
        
        // Calculate the average power on time
        long powerontotalave = powerontotal / startups;
        
        int secondsave = (int) (powerontotalave / 1000) % 60 ;
        int minutesave = (int) ((powerontotalave / (1000*60)) % 60);
        int hoursave   = (int) ((powerontotalave / (1000*60*60)) % 24);
        String poweronavestring = hoursave + "h" + minutesave + "m" + secondsave + "s";

        // Compare them to make sure there is a start first
        // Compare positions from list to make sure starts come before stops
        // Insert lack of starts or lack of stops where necessary
        // Compare them to make a new list of power on hours
                
        // Set up the variables from the current calendar for filenaming
        Calendar rightnow = Calendar.getInstance();
        int yearint = rightnow.get(rightnow.YEAR);
        int monthint = rightnow.get(rightnow.MONTH);
        int dayint = rightnow.get(rightnow.DAY_OF_MONTH);
        int hourint = rightnow.get(rightnow.HOUR_OF_DAY);
        int minuteint = rightnow.get(rightnow.MINUTE);
        
        String year = Integer.toString(yearint);
        String month = Integer.toString(monthint + 1);
        String day = Integer.toString(dayint);
        String hour = Integer.toString(hourint);
        String minute = Integer.toString(minuteint);
        
        if(month.length() == 1)
        {
            month = "0" + month;
        }
        if(day.length() == 1)
        {
            day = "0" + day;
        }
        if(hour.length() == 1)
        {
            hour = "0" + hour;
        }
        if(minute.length() == 1)
        {
            minute = "0" + minute;
        }
        
        String foldername = "logs-" + year + month + day + "_" + hour + minute + "/";
        
        
        // Save the formatted log entries to a CSV file
        String filename = "logs-" + year + month + day + "_" + hour + minute + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) 
        {
            writer.append("TIME_MILLIS,DATE,CONTENT,NOTES,SOURCELOG,CATEGORY");
            writer.append("\r\n");
            for(LogEntry logentry : loglist)
            {
                if(checkfromset)
                {
                    // Check if the logentry occurred after the period of interest began
                    if(logentry.getTimeLong() > checkfrom.getTimeInMillis())
                    {
                        writer.append(logentry.getLogEntryAsString());
                        writer.append("\r\n");
                    }
                }
                else
                {
                    writer.append(logentry.getLogEntryAsString());
                    writer.append("\r\n");
                }
                                
            }
        }   
        
        // Save a System Summary file
        
        // Can get from drserver and keep overwriting fields
        // Things to include:        
        //   
        // ERRORS
        String summaryname = "summary-" + year + month + day + "_" + hour + minute + ".txt";
        String softwareversion = null;
        String serialandkey = null;
        String cpuspec = null;
        String module0 = null;
        String module1 = null;
        String module2 = null;
        String gpu = null;
        String sysconfig = null;
        
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryname))) 
        {
            for(LogEntry logentry : loglist)
            {
                if(logentry.getContent().toString().startsWith("Version:"))
                {
                    softwareversion = logentry.getContent().toString();
                    
                }
                if(logentry.getContent().toString().startsWith("Serial number"))
                {
                    serialandkey = logentry.getContent().toString();
                    
                }
                if(logentry.getContent().toString().startsWith("CPU:"))
                {
                    cpuspec = logentry.getContent().toString();
                    
                }
                if(logentry.getContent().toString().startsWith("System Config:"))
                {
                    sysconfig = logentry.getContent().toString();
                    
                } 
                if(logentry.getContent().toString().startsWith("Module 0:"))
                {
                    module0 = logentry.getContent().toString();
                    
                } 
                if(logentry.getContent().toString().startsWith("Module 1:"))
                {
                    module1 = logentry.getContent().toString();
                    
                }
                
            }
            writer.append("SYSTEM SUMMARY");
            writer.append("\r\n");
            writer.append("==============");
            writer.append("\r\n");
            writer.append("\r\n");
            writer.append("Current Configuration:");
            writer.append("\r\n");
            writer.append("----------------------");
            writer.append("\r\n");
            writer.append(softwareversion);                    
            writer.append("\r\n");
            writer.append(serialandkey);                    
            writer.append("\r\n");
            writer.append(cpuspec);                    
            writer.append("\r\n");
            writer.append(sysconfig);                    
            writer.append("\r\n");
            writer.append(module0);                    
            writer.append("\r\n");
            writer.append(module1);                    
            writer.append("\r\n");
            writer.append("\r\n");
            writer.append("History:");
            writer.append("\r\n");
            writer.append("--------");
            writer.append("\r\n");
            writer.append("Startups - " + startups);
            writer.append(", Shutdowns - " + shutdowns);
            writer.append(", Crashes - " + crashes);
            writer.append("\r\n");
            writer.append("\r\n");
            writer.append("Power on hours:");
            writer.append("\r\n");
            writer.append("---------------");
            writer.append("\r\n");
            writer.append("Total - " + powerontotalstring);
            writer.append(", Ave - " + poweronavestring);
            writer.append("\r\n");
            writer.append("\r\n");
            writer.append("Full List:");
            writer.append("\r\n");
            
            for(Long powerontime : powerontimes)
            {
                int seconds1 = (int) (powerontime / 1000) % 60 ;
                int minutes1 = (int) ((powerontime / (1000*60)) % 60);
                int hours1   = (int) ((powerontime / (1000*60*60)) % 24);
                writer.append(hours1 + "h" + minutes1 + "m" + seconds1 + "s");
                writer.append("\r\n");
            }
            
        }  
    }
    
    public void setLastGoodCalendar(Calendar cal)
    {
        lastgoodcalendar = cal;
    }
    
    public Calendar getLastGoodCalendar()
    {
        return lastgoodcalendar;
    }
            
    public void buildFileList() throws IOException
    {
        System.out.println("Please enter path to log folder:");
        Scanner input = new Scanner(new InputStreamReader(System.in));
        String logfolder = input.nextLine();
        //System.out.println(logfolder);
        // Need to sort out this example code to build a file list.
        dir = FileSystems.getDefault().getPath(logfolder);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream)
            {
                //fullfilelist.add(path.getFileName().toString());
                fullfilelist.add(path);
            }
        }
        
    }
    
    public ArrayList<String> logsOfInterest()
    {
        ArrayList<String> logs = new ArrayList<>();
        for(int i = 0 ; i < CodexLogViewer.userargs.length ; i++)
        {
            if(CodexLogViewer.userargs[i].contains("c"))
            {
                logs.add("codex.log");
                logs.add("codex.log.0");
//                logs.add("codex.log.1");
//                logs.add("codex.log.2");
//                logs.add("codex.log.3");
//                logs.add("codex.log.4");
//                logs.add("codex.log.5");
//                logs.add("codex.log.6");
                // ADD MORE TO CHECK LIMITS
            }            
            if(CodexLogViewer.userargs[i].contains("m"))
            {
                logs.add("messages");
            }
            if(CodexLogViewer.userargs[i].contains("v"))
            {
                logs.add("vaultui.log");
            }
            // Keep drserver logs last to ensure System Summary accurate.
            if(CodexLogViewer.userargs[i].contains("d"))
            {
                logs.add("drserver.0.log");
                // logs.add("drserver.1.log");
                //logs.add("drserver.2.log");
            }  
        }
        return logs;
    }
    
    // Return a value in millis from before now
    // where the logs are of interest from
    public Long periodOfInterest()
    {
        long millis = 0;
        for (String userarg : CodexLogViewer.userargs) {
            // Check what time period (days) the user is interested in
            if (userarg.contains("1")) {
                // Convert 1 day to milliseconds
                millis = 1 * 86400000;                 
            }
            if (userarg.contains("2")) {
                // Convert 2 days to milliseconds
                millis = 2 * 86400000;                 
            }
            if (userarg.contains("3")) {
                // Convert 3 days to milliseconds
                millis = 3 * 86400000;                 
            }
            // A large time period, to run as a test
            if (userarg.contains("180")) {
                // Convert 180 days to milliseconds
                millis = 180 * 86400000;                 
            }
        }
        return millis;        
    }
    
    public Path getPath()
    {
        return dir;
    }
}
