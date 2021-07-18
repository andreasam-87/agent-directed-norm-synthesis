// Internal action code for project rooms

package room_experiment;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class concat extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
  

        // everything ok, so returns true
        return un.unifies(args[0],args[1]);
    }
}
