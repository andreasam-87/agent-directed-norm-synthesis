// Internal action code for project rooms

package room_experiment;

import org.apache.commons.lang3.StringUtils;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class sameRoom extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	int ret =-1;
    	//getting info for the first agent ~ currently handling
    	String room1 = args[0].toString();
    	String room2 = args[1].toString();
    	
    	if(StringUtils.startsWith(room1,"\""))
		{
			System.out.println("has uplifted commas");
			room1 = StringUtils.strip(room1, "\""); 
			
		}
    	if(room1.equals(room2))
    	{
    		ret = 0;
    	}
    	else
    	{
    		ret = 1;
    	}
    	
    	return un.unifies(new NumberTermImpl(ret), args[2]);
    }
}
