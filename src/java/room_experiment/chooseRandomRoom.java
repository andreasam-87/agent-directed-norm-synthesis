// Internal action code for project room_experiment

package room_experiment;

import java.util.Random;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class chooseRandomRoom extends DefaultInternalAction {

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	 int min = 1;
         //int max = 3;
         int max = 2;
         	//String room;
         //int random = new Random().nextInt(max-min) + min;//.nextBoolean() ? a : b;
         int random = new Random().nextBoolean() ? min : max;
         String room = "room"+random;
       
         return un.unifies(new StringTermImpl(room),args[0]);

    }
}
