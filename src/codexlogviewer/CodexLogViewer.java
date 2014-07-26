package codexlogviewer;
import java.io.IOException;
import java.text.ParseException;
/**
 *
 * @author alastairnicholls
 */
public class CodexLogViewer {

    /**
     * @param args the command line arguments
     * @throws java.text.ParseException
     * @throws java.io.IOException
     */
    static String[] userargs;
    
    public static void main(String[] args) throws ParseException, IOException 
    {
        /**
         * Command line arguments needed:
         * c - codex logs
         * d - drserver logs
         * m - messages logs
         * v - vault logs
         * a - all of the above logs
         * s - summary
        */
        userargs = args;
        CodexLogViewer logviewer = new CodexLogViewer();
        logviewer.run();
    }
    
    public void run() throws ParseException, IOException
    {
        
        LogFormatter formatter = new LogFormatter();
        formatter.format();
    }
    
    public String[] getArgs()
    {
        return userargs;
    } 
}
