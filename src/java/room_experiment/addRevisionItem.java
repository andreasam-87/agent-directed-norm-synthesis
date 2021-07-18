// Internal action code for project rooms

package room_experiment;

import java.util.HashMap;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class addRevisionItem extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	
        SynthesizerRevisionQueue.getInstance().addToQueue(args[0].toString(), args[1].toString());
        
     // Posting to the database
        HashMap<String, String> data = new HashMap<>();
        data.put("g", "good");
//        data.put("b", "bee");
//        data.put("c", "cow");
        Couch.getInstance().storeData(data);
        System.out.println("Storing - ----------------------------------------------------------- ");
        return true;
    }
}
