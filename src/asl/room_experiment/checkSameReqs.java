// Internal action code for project rooms

package room_experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class checkSameReqs extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	ArrayList list = new ArrayList(((ListTermImpl) args[0]).getAsList());
    	ListTermImpl lists = (ListTermImpl) args[0];
    	ListTermImpl lists2 = new ListTermImpl();
    	
    	Set<String> uniqueReqs = new HashSet<String>();
    	uniqueReqs.add(lists.get(0).toString());
    	lists2.add(lists.get(0));
 //   	System.out.println("Calculating distances ");
    	for(int i=0;i<lists.size();i++)
    	{
    		String str1 = lists.get(i).toString();	
    		for (int j = i+1; j < lists.size(); j++) {
    			String str2 = lists.get(j).toString();
    			 int distance =  StringUtils.getLevenshteinDistance(str1, str2); //how different are the two strings, how many changes to make them the same
    			 System.out.println("Distance between "+str1+ " and " +str2+ " is " +distance);
    			 if(distance>2)
    			 {
    				 uniqueReqs.add(str2);
    				 lists2.append(lists.get(j));
    			 }
    			 else
    			 {
//    				 uniqueReqs.remove(str2);
//					 lists2.remove(lists.get(j));
    				 if((uniqueReqs.contains(str1)) && (uniqueReqs.contains(str2)))
    				 {
    					 uniqueReqs.remove(str2);
    					 
    				 }
    				 if(lists2.contains(lists.get(i)) && lists2.contains(lists.get(j)))
    				 {
    					 lists2.remove(lists.get(j));
    				 }
    			 }
    				 

    		    // Need a logic for comparing items already in list before adding new ones....ugh
    			
    			
    		  }
    		//System.out.println("String "+str1);
    	}
    	
//    	for(int i=0;i<list.size();i++)
//    	{
//    		String str1 = list.get(i).toString();	
//    		for (int j = i+1; j < list.size(); j++) {
//    			String str2 = list.get(j).toString();
//    			 int distance =  StringUtils.getLevenshteinDistance(str1, str2); //how different are the two strings, how many changes to make them the same
//    			 System.out.println("Distance between "+str1+ " and " +str2+ " is " +distance);
//    			 if(distance>2)
//    			 {
//    				 uniqueReqs.add(str2);
//    			 }
//    			 else
//    			 {
//    				 if((uniqueReqs.contains(str1)) && (uniqueReqs.contains(str2)))
//    				 {
//    					 uniqueReqs.remove(str2);
//    				 }
//    			 }
//    				 
//
//    		    // Need a logic for comparing items already in list before adding new ones....ugh
//    			
//    			
//    		  }
//    		//System.out.println("String "+str1);
//    	}
    	
    	
    	Object[] ret = uniqueReqs.toArray();
    	int size =ret.length;
    	int size2=lists2.size(); 
    	System.out.println("size: "+size+" and size: "+size2);
    //	System.out.print("String from set: "+ret[0].toString());
    	if(size==1)
    	{
    		String rtr =ret[0].toString() ;
    		//System.out.println("here");
    		return un.unifies(new StringTermImpl(rtr),args[1]) && un.unifies(new NumberTermImpl(0), args[2]);
    	}
    	else
    	{
    		String rtr =uniqueReqs.toString() ;
    		System.out.println("set contents: "+rtr);
//    		ListTermImpl retLists = new ListTermImpl();
//    		for(Object t:ret)
//    		{
//    			retLists.add((ListTerm)t); //need to figure out how to get a term t
//    		}
    	//	System.out.println("set contents: "+rtr);
    //		List lt = Arrays.asList(ret)
    		//ArrayList<Object> list2 = new ArrayList(Arrays.asList(ret));
    	//	return un.unifies(new StringTermImpl(rtr),args[1]) && un.unifies(new NumberTermImpl(1), args[2]);
    		return un.unifies(lists2,args[1]) && un.unifies(new NumberTermImpl(1), args[2]);

    	}
        // everything ok, so returns true
    	//return un.unifies(new StringTermImpl(room),args[0]);
       // return true;
    }
}
