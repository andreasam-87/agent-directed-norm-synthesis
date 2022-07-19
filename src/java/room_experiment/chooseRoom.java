// Internal action code for project room_experiment

package room_experiment;

import java.util.Random;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class chooseRoom extends DefaultInternalAction {

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
    	 int min = 1;
         //int max = 3;
         int max = 2;
         	//String room;
         //int random = new Random().nextInt(max-min) + min;//.nextBoolean() ? a : b;
        // int random = new Random().nextBoolean() ? min : max;
         //String room = "room"+random;
       
         ListTerm listOfRooms =  (ListTerm) args[0];
         int len = listOfRooms.size();
         
         int rand = new Random().nextBoolean() ? 1 : len;
         
         String room =  listOfRooms.get(rand-1).toString();
         
         System.out.println("Random number is "+rand +". Room selected is  :- " + room);
     	
     	// just like an ArrayList, we can use the enhanced for loop here
     	// all elements in a ListTerm are of type Term
     	/*for (Term innerListAsTerm : listOfRooms) {
     		
     		// Now we convert the inner list into a list term so we may access its properties.
     		ListTerm innerListAsListTerm =  (ListTerm) innerListAsTerm;
     		
     		// Note, we did not need to do much for strings, we just called the toString method
     		String itemName =  innerListAsListTerm.get(0).toString();
     		// log the itemName so we can see that we converted it successfully
     		System.out.println("Printing from an internal action :- " + itemName);
     		
     		// Note we are still using NumberTerm for the number
     		NumberTerm itemCountAsTerm =  (NumberTerm) innerListAsListTerm.get(1);
     		
     		int itemCount =  (int) itemCountAsTerm.solve();
     		//sum += itemCount;	
     	}
         */
         //return un.unifies(new StringTermImpl(listOfRooms.get(rand)),args[0]);
         //return un.unifies(new StringTermImpl(room),args[0]);
         
         return un.unifies(new NumberTermImpl(rand-1),args[1]);

    }
}
