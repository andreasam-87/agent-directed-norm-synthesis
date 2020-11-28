import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Processes
{
    private static final String NEWLINE = System.getProperty("line.separator");

    /**
     * @param command the command to run
     * @return the output of the command
     * @throws IOException if an I/O error occurs
     */
    public static String run(String command) throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder(command).redirectErrorStream(true);
        Process process = pb.start();
        StringBuilder result = new StringBuilder(80);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream())))
        {
            while (true)
            {
                String line = in.readLine();
                if (line == null)
                    break;
                result.append(line).append(NEWLINE);
            }
        }
        return result.toString();
    }

    
    @SuppressWarnings("resource")
	public static String execCmd(String cmd) throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
    public static void runShellCmd(String command) throws IOException, InterruptedException
    {
    	//String command = "ping -c 3 www.google.com";

        Process proc = Runtime.getRuntime().exec(command);

        // Read the output

        BufferedReader reader =  
              //new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader errorReader =  
                new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        
        //use two bufferedreaders, one for normal input one for errors and print both to screen. 
        
        
       // System.out.print("x");
        StringBuilder str_res = new StringBuilder("");
        String line = "";
//        while(!reader.ready())
//        {
//        	System.out.print("waiting");
//        }
        while((line = reader.readLine()) != null) {
        	str_res.append(line+ "\n");
            System.out.print(line + "\n");
           // System.out.print("y");
        }

        line = "";

	      while((line = errorReader.readLine()) != null) {
	          System.out.print(line + "\n");
	         // System.out.print("y");
	      }
	      
        proc.waitFor();   
    }
    
    public static String runShellCmdRes(String command) throws IOException, InterruptedException
    {
    	Process proc = Runtime.getRuntime().exec(command);

        // Read the output

        BufferedReader reader = 
        		new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader errorReader =  
                new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        
        StringBuilder str_res = new StringBuilder("");

        String line ="";
        while((line = reader.readLine()) != null) {
        	str_res.append(line+ "\n");
        }
        
        line = "";
        StringBuilder str_err = new StringBuilder("");
	      while((line = errorReader.readLine()) != null) {
	    	  str_err.append(line+ "\n");
	  
	      }
	      
        proc.waitFor(); 
        if(str_res.length()>0)
        	return str_res.toString();
        else
        	return str_err.toString();
    }
    
    /**
     * Prevent construction.
     */
    public Processes()
    {
    }
}