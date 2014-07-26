/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package codexlogviewer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks the content of the log line and reports back possible causes
 * of the messages, or errors.
 * 
 * @author alastairnicholls
 */
public class LogHelper {

    HashMap<String, String> checklist;
    HashMap<String, String> GUIDs;
    
    public LogHelper() throws IOException
    {
        makeCheckList();
        loadGUIDs();
    }
    
    public void makeCheckList()
    {
        checklist = new HashMap<>();
        // Define what we're trying to match on...
        // drserver checks
        checklist.put("drserver starting", "Software starting.");
        checklist.put("breakpoint", "Software crash.");
        checklist.put("drserver stopped", "Software shutdown.");
        checklist.put("PHY0", "SSD slot 0 (Transfer Module cd3), Module interface below (Storage Module cd4)");
        checklist.put("PHY1", "SSD slot 1 (Transfer Module cd3), Module interface below (Storage Module cd4)");
        checklist.put("PHY2", "Capture Drive 3 (Transfer Module cd3), Module interface below (Storage Module cd4)");
        checklist.put("PHY3", "Capture Drive 3 (Transfer Module cd3), Module interface below (Storage Module cd4)");
        checklist.put("PHY4", "External SAS port (Transfer Module cd3), Storage Side 1 (Storage Module cd4)");
        checklist.put("PHY5", "External SAS port (Transfer Module cd3), Storage Side 1 (Storage Module cd4)");
        checklist.put("PHY6", "External SAS port (Transfer Module cd3), Storage Side 1 (Storage Module cd4)");
        checklist.put("PHY7", "External SAS port (Transfer Module cd3), Storage Side 1 (Storage Module cd4)");
        checklist.put("PHY8", "Module interface below (Transfer Module cd3), Storage Side 2 (Storage Module cd4)");
        checklist.put("PHY9", "Module interface below (Transfer Module cd3), Storage Side 2 (Storage Module cd4)");
        checklist.put("PHY10", "Module interface below (Transfer Module cd3), Storage Side 2 (Storage Module cd4)");
        checklist.put("PHY11", "Module interface below (Transfer Module cd3), Storage Side 2 (Storage Module cd4)");
        checklist.put("PHY12", "Capture Drive 4 (Transfer Module cd3), Module interface above (Storage Module cd4)");
        checklist.put("PHY13", "Capture Drive 4 (Transfer Module cd3), Module interface above (Storage Module cd4)");
        checklist.put("PHY14", "Capture Drive 2 (Transfer Module cd3), Module interface above (Storage Module cd4)");
        checklist.put("PHY15", "Capture Drive 2 (Transfer Module cd3), Module interface above (Storage Module cd4)");
        checklist.put("PHY16", "Onboard Datapack");
        checklist.put("PHY17", "Onboard Datapack");
        checklist.put("PHY18", "Onboard Datapack");
        checklist.put("PHY19", "Onboard Datapack");
        checklist.put("PHY20", "Capture Drive 1");
        checklist.put("PHY21", "Capture Drive 1");
        checklist.put("PHY22", "Host connector 1");
        checklist.put("PHY23", "Host connector 1");
        checklist.put("PHY24", "Host connector 1");
        checklist.put("PHY25", "Host connector 1");
        checklist.put("PHY26", "Host connector 2");
        checklist.put("PHY27", "Host connector 2");
        checklist.put("/dev/md127", "XFS RAID loading/unloading");
        checklist.put("PCIE (serial", "Review Module");
        checklist.put("Removable (serial", "Removable Storage Module");
        checklist.put("load failure", "Load failure");
        checklist.put("all copies of volume header are marked as dirty", "Disk write error(s) leading to failed Roll, recovery may be possible");
        checklist.put("Work thread", "Disk read/write error");
        checklist.put("removed before unloading complete", "Bad unload, either due to user error or hardware error");
        checklist.put("removed without unloading", "Bad unload, either due to user error or hardware error");
        // Config store not set, all 0xff
        
        // messages checks
        checklist.put("fault_state", "Flexi issues - change EEPROM?");
        checklist.put("I/O error", "Disk read/write error");
        // I/O errors on disk
        // end_request: critical target error
        // Medium Error
    }
    
    public String findCategory(String content)
    {
        String category = "INFO";
        if(content.contains("INFO"))
        {
            category = "INFO";
        }
        if(content.contains("ERROR"))
        {
            category = "ERROR";
        }
        if(content.contains("WARNING"))
        {
            category = "WARNING";
        }
        // Fix to check content and find correct category
        return category;
    }
    
    public void loadGUIDs() throws FileNotFoundException, IOException
    {
        GUIDs = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader("CDX-3730.csv"));
        String line = null;
        while((line = br.readLine())!=null)
        {
            String[] str = line.split(",");
            for(int i = 1; i < str.length ; i++)
            {
                GUIDs.put(str[0], str[1]);
            }
        }
    }
    
    public Set<String> getKeys()
    {
        return checklist.keySet();
    }
    
    public String getNotes(String key)
    {
        return checklist.get(key);
    }
    
    public Set<String> getGUIDs()
    {
        return GUIDs.keySet();
    }
    
    public String getSerial(String GUID)
    {
        return GUIDs.get(GUID);
    }
    
    
    
    
}
