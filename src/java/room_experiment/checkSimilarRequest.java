// Internal action code for project rooms

package room_experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

import org.apache.commons.lang3.StringUtils;

public class checkSimilarRequest extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	int ret =-1;
    	//getting info for the first agent ~ currently handling
    	String A1_ActResp = args[0].toString();
    	String A1_ActAtmp = args[1].toString();
    	String A1_ActExpResp = args[2].toString();

    	//getting info for the second agents - potentially need to handle
    	String A2_ActResp = args[3].toString();
    	String A2_ActAtmp = args[4].toString();
    	String A2_ActExpResp = args[5].toString();
    	
    	if(StringUtils.startsWith(A2_ActAtmp,"\""))
		{
			System.out.println("has uplifted commas");
			A2_ActResp = StringUtils.strip(A2_ActResp, "\""); 
			A2_ActAtmp = StringUtils.strip(A2_ActAtmp, "\""); 
			A2_ActExpResp = StringUtils.strip(A2_ActExpResp, "\""); 
		}
//		else
//		{
//			System.out.println("No uplifted commas - " + A2_ActAtmp);
//		}
    	
//    	if(StringUtils.startsWith(A1_ActAtmp,"\""))
//		{
//			System.out.println("has uplifted commas");
//			A1_ActResp = StringUtils.strip(A1_ActResp, "\""); 
//			A1_ActAtmp = StringUtils.strip(A1_ActAtmp, "\""); 
//			A1_ActExpResp = StringUtils.strip(A1_ActExpResp, "\""); 
//		}

    	
    	System.out.println("Values input: "+A1_ActAtmp+ " & "+  A2_ActAtmp);
    	if(A1_ActResp.equals(A2_ActResp))
    	{
    		//System.out.println("Same response from the actions attempted");
    		if(A1_ActExpResp.equals(A2_ActExpResp))
        	{
    			//System.out.println("Same expected response from the actions attempted");
    			//	substringBetween(String str, String open, String close)
    			
    			String act1 = StringUtils.substringBefore(A1_ActAtmp,"(");
    			String act2 = StringUtils.substringBefore(A2_ActAtmp,"(");
    			if(act1.equals(act2))
    			{
    				//System.out.println("Attempted Same action");
    				String sub1 = StringUtils.substringBetween(A1_ActAtmp, "(", ")");
        			String sub2 = StringUtils.substringBetween(A2_ActAtmp, "(", ")");
        			String a1 = StringUtils.substringBefore(sub1,",");
        			String l1 = StringUtils.substringAfter(sub1,",");
        			String a2 = StringUtils.substringBefore(sub2,",");
        			String l2 = StringUtils.substringAfter(sub2,",");
        			if(l1.equals(l2))
        			{
        				//System.out.println("Entered same room");
        				ret=0;
        				//return un.unifies(new NumberTermImpl(0), args[6]);
        			}
        			else
        			{
        				ret=1;
        			//	System.out.println("Entered different rooms");
        				//return un.unifies(new NumberTermImpl(1), args[6]);
        			}
    				
    			}
    			else
    			{
    				ret=1;
    				//return un.unifies(new NumberTermImpl(1), args[6]);
    			}
    			
        	}
//    		else
//        	{
//        		//maybe need to handle it differently if they expected a different result. 
//        	}
    	}
    	else 
    	{
    		ret=1;
    		//return un.unifies(new NumberTermImpl(1), args[6]);
    	}
    	
    	return un.unifies(new NumberTermImpl(ret), args[6]);
    }
}
