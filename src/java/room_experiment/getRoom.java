// Internal action code for project rooms

package room_experiment;

import org.apache.commons.lang3.StringUtils;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class getRoom extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        
    	String a1 = "";
    	String l1= "";
    	//String action = ((StringTerm)args[0]).getString();
    	
    	//StringTerm st = (StringTermImpl)args[0];
    	//String action = st.getString();
    	String action = args[0].toString();
    	
    	//String action = args[0].toString();

		String sub1 = StringUtils.substringBetween(action, "(", ")");
		
		a1 = StringUtils.substringBefore(sub1,",");
		l1 = StringUtils.substringAfter(sub1,",");
	//	System.out.println("Orginal String - "+ action + "  location - " + l1);
		
        // everything ok, so returns true
		return un.unifies(new StringTermImpl(l1), args[1]);
    }
}
