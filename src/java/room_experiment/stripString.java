// Internal action code for project rooms

package room_experiment;

import org.apache.commons.lang3.StringUtils;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class stripString extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	        // execute the internal action
    	    	int ret =-1;
    	    	//getting info for the first agent ~ currently handling
    	    	String toStrip = args[0].toString();
    	    	String retStripped = StringUtils.strip(toStrip, "\"");
    	    	System.out.println("String before "+toStrip+ ", String after "+retStripped);
    	    	
    	    	
    	    //	return un.unifies(parseString(retStripped), args[1]);
    
    	    	return un.unifies(new StringTermImpl(retStripped), args[1]);
    }
}
