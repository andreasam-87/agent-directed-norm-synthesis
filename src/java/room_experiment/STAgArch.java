package room_experiment;

import jason.architecture.AgArch;
import jason.asSemantics.*;

/**
 *
 * Much like the MindInspector included in the default JASON package (speaking about Jason 2.2-2.4),
 * this AgArch allows a user to keep track of changes withing the agent.
 *
 * The main difference with the mind inspector that rather than taking a snapshot of an agent at each timestep,
 * we are tracking the change between successive reasoning cycles.
 *
 */
public class STAgArch extends AgArch {

   // private STLogger logger;

    public STAgArch () { }


    public void reasoningCycleStarting() {
     //   logger.onReasoningCycleStarted();
    	System.out.println("------------ newRC");
    }

    public void setTS(TransitionSystem ts) {
         // did not check TransitionSystem and Circumstance for null as default JASON code (see DefaultBeliefBase#init)
         // assumed they are never null...

        //logger = STLogger.getInstance(ts.getUserAgArch().getAgName());
         ts.getC().addEventListener(new CircumstanceListener() {
             @Override
             public void eventAdded(Event event) {
               // logger.onEvent("EVENT", new String[]{event.toString()});
            	 System.out.println("------------ Event"+event.toString());
             }

             @Override
             public void intentionAdded(Intention intention) {
                //logger.onEvent("RUNNING-INTENTION", new String[]{intention.toString()});
                System.out.println("------------ Intention"+intention.toString());
             }

             @Override
             public void intentionDropped(Intention intention) {
                // logger.onEvent("DROPPED-INTENTION", new String[]{intention.toString()});
                 System.out.println("------------ Dropped Intention"+intention.toString());
             }

         //    @Override
             public void intentionSuspended(Intention intention, String s) {
                 if (s.toLowerCase().trim().startsWith("action")) {
                    // logger.onEvent("PENDING-ACTION", new String[]{s.substring(6).trim(), intention.toString()});
                	 System.out.println("------------ Pending Action"+intention.toString());
                 } else {
                     //logger.onEvent("SUSPENDED-INTENTION", new String[]{intention.toString()});
                	 System.out.println("------------ Suspended Intention"+intention.toString());
                 }
             }

            // @Override
             public void intentionResumed(Intention intention) {
                // logger.onEvent("RESUMING-INTENTION", new String[]{intention.toString()});
                 System.out.println("------------ Resuming Intention"+intention.toString());
             }
         });
    }

}