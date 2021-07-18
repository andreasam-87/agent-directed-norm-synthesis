// Internal action code for project rooms

package room_experiment;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class getRandomNum extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
       
    	
    	/* Function from https://www.baeldung.com/java-generating-random-numbers-in-range
    	 * 
    	 * public int getRandomNumber(int min, int max) {
			    return (int) ((Math.random() * (max - min)) + min);
			}
			
			https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
			add one to use the max too
			Min + (int)(Math.random() * ((Max - Min) + 1))
    	 * */
    	int min = (int)((NumberTerm) args[0]).solve();
    	int max = (int)((NumberTerm) args[1]).solve();

       int  result = (int) ((Math.random() * ((max - min)+1)) + min);

        return un.unifies(new NumberTermImpl(result),args[2]);
    }
}
