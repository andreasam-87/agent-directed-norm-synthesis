// Internal action code for project rooms

package room_experiment;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;


public class solutionDifferent extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        
        // execute the internal action
      	int ret =-1;
      	String directory = "/Users/andreasamartin/Documents/InstalExamples/rooms/";
      	//getting the files to compare
      	String file1 = args[0].toString();
      	String file2 = args[1].toString();
      	
      	boolean comp;
      	
      	try {
			
      		comp = FileUtils.contentEquals(new File(directory+file1),new File(directory+file2));
      		
      		System.out.println("Solution files are \n"+file1+"\n"+file2+"\nsame? "+comp);
           	if (comp)
           	{
           		ret = 0;
           	}
           	else
           	{
           		ret = 1;
           	}
      		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      	
      	
      	return un.unifies(new NumberTermImpl(ret), args[2]);
    }
}
