package codexlogviewer;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Formats the date to be stored in the LogEntry in
 * a consistent manner.
 * 
 * @author alastairnicholls
 */
public class DateFormatter {
    
    int count = 0;
    
    public DateFormatter()
    {
        
    }
        
    // Get the date formatted from drserver log
    public Calendar getCalendarDrserverLog(String log) throws ParseException
    {
        // drserver log date
        // Example line = "15 Apr 2014 6:19:16 pm Vault internal 1: unloaded";
        if(log.startsWith("MountUpdate") ||
           log.startsWith("MountDelete"))
        {
            ParsePosition start = new ParsePosition(12);
            String logline = log;
            SimpleDateFormat sdf;
            if(logline.charAt(14) == ':')
            {
                sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
            }
            else
            {
                sdf = new SimpleDateFormat("dd MMM yyyy h:mm:ss aa");
            } 
            sdf.setLenient(false);
            Date date = sdf.parse(logline, start);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            // System.out.println("As Calendar: " + cal);
            return cal;
        }
        else
        {
            // if char 14 = : then use one rule
            // else use another rule
            String logline = log;
            SimpleDateFormat sdf;
            if(logline.charAt(14) == ':')
            {
                sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa");
            }
            else
            {
                sdf = new SimpleDateFormat("dd MMM yyyy h:mm:ss aa");
            }            
            sdf.setLenient(false);
            Date date = sdf.parse(logline);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        }
    }
    
    public long convertCalendarToMillis(Calendar cal)
    {
        long calAsMillis = cal.getTimeInMillis();
        return calAsMillis;
    }
       
    public Calendar getCalendarMessagesLog(String log) throws ParseException
    {
        // messages log
        // String line3 = "Jun 18 11:26:12 vaults1102 NetworkManager[937]:";
        String logline = log;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm:ss");
        Date date = sdf.parse(logline);
        // Set year as 2014, NEEDS FIXING TO PULL DATE FROM ELSEWHERE
        date.setYear(2014-1900);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    
    public Calendar getCalendarCodexLog(String log) throws ParseException
    {
        // codex log date
        // Example line = "2014-05-16 3:09:15,INFO,TASK,*,Created work template,verify";
        Date date = null;
        Calendar cal = Calendar.getInstance();
        try
        {
            String logline = log;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sdf.parse(logline);
            
            cal.setTime(date);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
        finally
        {
            return cal;
        }
    }
    
    public String calAsString(Calendar cal)
    {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // String string = sdf.format(date);
        int yearint = cal.get(cal.YEAR);
        int monthint = cal.get(cal.MONTH);
        int dateint = cal.get(cal.DAY_OF_MONTH);
        int hourint = cal.get(cal.HOUR_OF_DAY);
        int minuteint = cal.get(cal.MINUTE);
        int secondint = cal.get(cal.SECOND);
        
        String year = Integer.toString(yearint);
        String month = Integer.toString(monthint);
        String date = Integer.toString(dateint);
        String hour = Integer.toString(hourint);
        String minute = Integer.toString(minuteint);
        String second = Integer.toString(secondint);
        
        String completedate = year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second;
        return completedate;
    }
}
