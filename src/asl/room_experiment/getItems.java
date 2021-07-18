// Internal action code for project rooms

package room_experiment;

import org.apache.commons.lang3.StringUtils;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class getItems extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
    	//String str = ((ListTerm) args[0]).toString();

    	ListTerm Lt = ((ListTerm) args[0]);
    	
   // 	System.out.println("Term1 - "+(Lt.toString()));
    	int sz = Lt.size();
    	String[] strArr = new String[sz];
    	for(int j=0;j<sz;j++)
    	{
    		//strArr[j] = ((StringTerm)Lt.get(j)).getString();// . .toString(); 
    		strArr[j] = Lt.get(j).toString(); 
    	}
//   	String str = args[0].toString();
//    	StringTerm s = (StringTerm) args[0];
//    	String str = s.getString();
//    	str=str.replace("[","");
//    	str=str.replace("]","");
    //	String[] strArr = str.split(",");
    	
    	int numRet = (int)((NumberTerm) args[1]).solve();
    //	System.out.println("Items- "+str+ ": "+strArr.toString());
    	//int size=strArr.length;
   // 	int size = (int)((NumberTerm) args[0]).solve();
    //	int asr = (int)((NumberTerm) args[1]).solve();
    	
        // everything ok, so returns true
   // 	return un.unifies(new StringTermImpl(strArr[0]), args[1]) && un.unifies(new StringTermImpl(strArr[1].concat(","+strArr[2])), args[2]) && un.unifies(new StringTermImpl(strArr[3]), args[3]) && un.unifies(new StringTermImpl(strArr[4]), args[4]);
    	
//    	for(int i=0; i<strArr.length;i++)
//    	{
//    		if(StringUtils.startsWith(strArr[i],"\""))
//    		{
//    			strArr[i] = StringUtils.strip(strArr[i], "\""); 
//    		}
//    		
//    	}
    	
    	if(numRet == 3)
    	{
    		//return un.unifies(new StringTermImpl(strArr[0]), args[2]) && un.unifies(new StringTermImpl(strArr[1].concat(",\""+strArr[2])), args[3]) && un.unifies(new StringTermImpl(strArr[3]), args[4]);
    	    
    	//	return un.unifies(new StringTermImpl(strArr[0]), args[2]) && un.unifies(new StringTermImpl(strArr[1]), args[3]) && un.unifies(new StringTermImpl(strArr[2]), args[4]);
       		return un.unifies(Lt.get(0), args[2]) && un.unifies(Lt.get(1), args[3]) && un.unifies(Lt.get(2), args[4]);

    	    
    	}else if(numRet == 2)
    	{
    		//return un.unifies(new StringTermImpl(strArr[0]), args[2]) && un.unifies(new StringTermImpl(strArr[1]), args[3]);
    		return un.unifies(Lt.get(0), args[2]) && un.unifies(Lt.get(1), args[3]);

    	}
    	else if(numRet == 4)
    	{
    		//return un.unifies(new StringTermImpl(strArr[0]), args[2]) && un.unifies(new StringTermImpl(strArr[1]), args[3]) && un.unifies(new StringTermImpl(strArr[2]), args[4]) && un.unifies(new StringTermImpl(strArr[3]), args[5]);

//    		return un.unifies(new StringTermImpl(strArr[0]), args[2]) && un.unifies(new StringTermImpl(strArr[1].concat(",\""+strArr[2])), args[3]) && un.unifies(new StringTermImpl(strArr[3]), args[4]) && un.unifies(new StringTermImpl(strArr[4]), args[5]);
    		return un.unifies(Lt.get(0), args[2]) && un.unifies(Lt.get(1), args[3]) && un.unifies(Lt.get(2), args[4])&& un.unifies(Lt.get(3), args[5]);


    	}
    	else
    		return true;
    	 
    	//return true;
    }
}
