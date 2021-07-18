// Internal action code for project rooms

package room_experiment;

import java.util.HashMap;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class checkRevisionActive extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
   
    	
//    	if(SynthesizerRevisionQueue.getInstance().checkEmpty())
//    		 return un.unifies(new NumberTermImpl(1),args[0]);
//    	else
//    		return un.unifies(new NumberTermImpl(0),args[0]);
//    	
//    	
    	
//        // Reading from the database
//        String databaseContents = Couch.getInstance().getData();

//        // Posting to the database
//        HashMap<String, String> data = new HashMap<>();
//        data.put("a", "ant");
//        data.put("b", "bee");
//        data.put("c", "cow");
//        Couch.getInstance().storeData(data);


        // searching for data in the database
        String searchResult = Couch.getInstance().findData("x", "xray", Couch.Relation.Equal);
        System.out.println("DB item found - "+searchResult);
        
      HashMap<String, String> data = new HashMap<>();
      data.put("x", "xray");
      Couch.getInstance().storeData(data);
      System.out.println("Storing now - --------------- ");
        return un.unifies(new NumberTermImpl(0),args[0]);
       // return true;
    }
}
