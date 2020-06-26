import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonExtractor {

	public JsonExtractor() {
		
	}
	
	protected ArrayList<String> extractHoldsatJson(JSONObject toExtract)
	{
		JSONArray j;
		ArrayList<String> new_facts = new ArrayList<String>();
		for (String keys : toExtract.keySet()) 
		{
		    if(keys.equals("fluents"))// && toExtract.get(keys)!=null)// toExtract.get(keys).equals(null)) // ArrayUtils.isEmpty(toExtract.get(keys))) // toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	//occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0).equals("arrives"))
				//System.out.println(" hello "+j.getJSONArray(0).getJSONArray(1).getJSONArray(0).getString(0));
				
				//System.out.println(" hello 2 "+j.getJSONArray(0).getJSONArray(1).toString());
				
		    	if (j.length()!=0)
		    	{
		    		//new_facts.add(j.getJSONArray(0).getJSONArray(1).toString());
		    		//new_facts.add(j.getJSONArray(0).toString());
		    		//System.out.println("Fluents: "+ j.getJSONArray(0).toString());
		    		for(int i=0;i<j.length();i++)
		    		{
		    			new_facts.add(j.getJSONArray(i).getJSONArray(1).toString());
		    		}
		    		
//		    		JSONArray fluents = (JSONArray)toExtract.get(keys);
//		    		for (int i = 0; i < fluents.length(); i++) {
//		    			//String what = fluents.get(i);
//		    			JSONArray arr = (JSONArray)fluents.getJSONArray(i);
//		    			//String post_id = fluents.getJSONObject(i).getString("holdsat");
//		                System.out.println(fluents.get(i).getClass()+ " " + arr);
//		            
//		    		}
//		    		 System.out.println(" Flatten holdsat: "+ fluents.toString());
		    	}
		    }
		    else if(keys.equals("gpows"))//) && !toExtract.get(keys).equals(null))//!=null) //((JSONArray)toExtract.get(keys).length()))//.isNull()) // toExtract.get(keys)!=null) // toExtract.get(keys).equals(null))//&& toExtract.get(keys)!=null)
		    { 
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0) //!j.isEmpty())
		    	{
		    		//new_facts.add(j.getJSONArray(0).getJSONArray(1).toString());
		    		for(int i=0;i<j.length();i++)
		    		{
		    			new_facts.add(j.getJSONArray(i).getJSONArray(1).toString());
		    		}
//		    		System.out.println("Gpows: "+ toExtract.get(keys));
//			    	System.out.println("Class of object is "+ toExtract.get(keys).getClass());
		    	}
		    	
		    }
		    else if(keys.equals("ipows")) // && toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    	{
		    		//new_facts.add(j.getJSONArray(0).getJSONArray(1).toString());
		    		for(int i=0;i<j.length();i++)
		    		{
		    			new_facts.add(j.getJSONArray(i).getJSONArray(1).toString());
		    		}
		    		//System.out.println("Ipows: "+ toExtract.get(keys));
		    	}
		    	
		    }
		    else if(keys.equals("tpows")) // && toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    	{
		    		//new_facts.add(j.getJSONArray(0).getJSONArray(1).toString());
		    		for(int i=0;i<j.length();i++)
		    		{
		    			new_facts.add(j.getJSONArray(i).getJSONArray(1).toString());
		    		}
		    		//System.out.println("Tpows: "+ toExtract.get(keys));
		    	}
		    	
		    }
		    else if(keys.equals("obls")) // && toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    	{
		    		//new_facts.add(j.getJSONArray(0).getJSONArray(1).toString());
		    		for(int i=0;i<j.length();i++)
		    		{
		    			new_facts.add(j.getJSONArray(i).getJSONArray(1).toString());
		    		}
		    		//System.out.println("Obls: "+ toExtract.get(keys));
		    	}
		    	
		    }
		    else if(keys.equals("perms")) // && toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    	{
		    		//new_facts.add(j.getJSONArray(0).getJSONArray(1).toString());
		    		//ArrayList<String> nf = new ArrayList<String>();
		    		//System.out.println("Perms: "+ toExtract.get(keys));
//		    		for(int i=0;i<j.length();i++)
//		    		{
//		    			nf.add(j.getJSONArray(i).getJSONArray(1).toString());
//		    		}
		    		for(int i=0;i<j.length();i++)
		    		{
		    			new_facts.add(j.getJSONArray(i).getJSONArray(1).toString());
		    		}
		    		//System.out.println("All Perms: "+ nf);
		    	}
//		    	System.out.println("Perms have: "+ j.getJSONArray(0).getJSONArray(1).toString());
//		    	System.out.println("Perms need: "+ j.getJSONArray(0).toString());
//		    	System.out.println("Perms: "+ toExtract.get(keys));
		    	
		    }
		    else if(keys.equals("pows")) // && toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    	{
		    		//new_facts.add(j.getJSONArray(0).getJSONArray(1).toString());
		    		
		    		for(int i=0;i<j.length();i++)
		    		{
		    			new_facts.add(j.getJSONArray(i).getJSONArray(1).toString());
		    		}
		    		
		    		//System.out.println("Pows: "+ toExtract.get(keys));
		    	}
		    	
		    }
		    else
		    {
		    	System.out.print("hmmm");
		    }
		}
		//System.out.println("New facts: "+new_facts);
		//System.out.println("Size of facts array before: "+new_facts.size());
		ArrayList<String> temp_facts = new ArrayList<String>();
		for(int i=0;i<new_facts.size();i++)
		{
			temp_facts.add(parseString(new_facts.get(i)));
		}
		//System.out.println("Temp facts: "+temp_facts);
		//System.out.println("Size of facts array after: "+temp_facts.size());
		//facts_store.add(temp_facts);
		return temp_facts;
		
	}
	
	/*I need to make this so that I can add the initially afterwards*/
	protected String parseString(String toParse)
	{
		toParse = toParse.replace('"', ' ');
		toParse = toParse.replaceAll("\\s+", "");
		//toParse = toParse.replaceFirst(",","");
		//toParse = toParse.replaceFirst(",","");
		
		//toParse = toParse.replaceAll("(.)\\1+","$1"); <-Removes 1 or more instances of a character from the string
		//toParse = toParse.replaceAll("[[+","$1");
		
		//HERE
		if(toParse.contains("perm") || toParse.contains("pow"))
		{
			toParse = toParse.replaceAll("]+",")");
			toParse = toParse.replaceAll("\\[+","(");
			toParse = toParse.replaceAll(",\\(","(");
			toParse = toParse.replaceAll("\\),",")),");
			if(toParse.chars().filter(ch -> ch == '(').count() != toParse.chars().filter(ch -> ch == ')').count())
			{
				toParse = toParse.replaceAll("\\)+",")");
			}
			toParse = "initially"+toParse;

		}
		else
		{
			//toParse = toParse.replaceAll("\\[","(");
			//toParse = toParse.replaceAll("]",")");
			
			toParse = toParse.replaceAll("\\[+","(");
			if (toParse.contains("obl"))
			{
				//still to fix this one
				toParse = toParse.replaceAll("]]+","))");
				toParse = toParse.replace(']', ')');
				toParse = toParse.replaceFirst(",","");
				toParse = toParse.replaceFirst(",","");
				toParse = toParse.replaceFirst("\\)+,","),");
				toParse = toParse.replaceFirst(",\\(",",");
				toParse = toParse.replaceFirst(",\\(","(");
				toParse = "initially"+toParse+"";
			}
			else 
			{
				//toParse = toParse.replaceAll("]+","))");
				toParse = toParse.replaceAll("]+",")");
				toParse = toParse.replaceFirst(",","");
				toParse = toParse.substring(0, toParse.length()-1) +"";// replaceLast(",","");
				toParse = "initially"+toParse+")";
			}
			
			
			//System.out.println("Facts after parse: " + toParse );
		}

		return toParse;
	}
	
	public String getStateFacts(int stateKey, HashMap <Integer,State> stateList)
	{
		State state = (State)stateList.get(stateKey);
		JSONArray events = state.getEvents();
		String ev = getFacts(events);
		JSONObject facts = state.getFacts();
		String fct = extractHoldsatJson(facts).toString();
		//System.out.println("Events: "+events);
		//StringBuilder stb = new StringBuilder("");
		return ev+fct;
	}
	
	public String getFacts(JSONArray facts)
	{
		StringBuilder sb = new StringBuilder("");
//    	for (Term arg : args) {
//    		sb.append(arg.toString());
//    	}
		for (int i=0;i<facts.length();i++)
		{
//            String st = facts.getJSONArray(0).getJSONArray(1).getJSONArray(0).getString(0);
//			String st2 = facts.getJSONArray(0).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
//			String st3 = facts.getJSONArray(0).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(1);
			String st = facts.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0);
			String st2 = facts.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
			String st3 = facts.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(1);
			
			String ev = st+ "("+st2+","+st3+"),";
			sb.append(ev);
		}
		System.out.println("Facts string: "+sb);
		return sb.toString();
	}
	
	public int checkStateForEvent(String event, HashMap <Integer,State> stateList)
	{
		int stateOccurred=0;

		// Using for-each loop 
        for (Map.Entry mapElement : stateList.entrySet()) { 
            int key = (int)mapElement.getKey(); 
            State state = (State)mapElement.getValue();
            
            JSONArray ar = state.getEvents();
          //  System.out.println(ar);
            
            for (int i=0;i<ar.length();i++)
    		{
	            String st = ar.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0);
	            String st2,st3;
	            if (ar.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).length()==1)
	            {
	            	st2 = ar.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
					st3 = "";
	            }
	            else
	            {
	            	st2 = ar.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
					st3 = ar.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(1);
//					 
	            }
//				st2 = ar.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
//				st3 = ar.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(1);
//				 String st = ar.getJSONArray(0).getJSONArray(1).getJSONArray(0).getString(0);
//					String st2 = ar.getJSONArray(0).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
//					String st3 = ar.getJSONArray(0).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(1);
//					
				String ev = st+ "("+st2+","+st3+")";
				//if (ev.equals(event)){
				if (event.equals(ev)){
					stateOccurred = key;
					//System.out.println("Found the event that occured "+stateOccurred+" \nEvent- "+ev);
					//System.out.println(ev);
				}
	        }
            
        } 
        
        return stateOccurred;
//		for (State state : stateList.values()){
//			JSONArray ar = state.getEvents();
//			
//			for (int i=0;i<ar.length();i++)
//			{
//				String st = ar.getJSONArray(0).getJSONArray(1).getJSONArray(0).getString(0);
//				//String st2 = ar.getJSONArray(0).getJSONArray(1).getJSONArray(1).getJSONArray(0).getString(0);
//				String st2 = ar.getJSONArray(0).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
//				String st3 = ar.getJSONArray(0).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(1);
//						//getJSONArray(0).getString(0);
//				//String st3 = ar.getJSONArray(0).getJSONArray(1).getJSONArray(2).getString(0);
//				String ev = st+ "("+st2+","+st3+")";
//				//if (ev.equals(event)){
//				if (event.equals(ev)){
//					System.out.println("Found the event that occured ");
//					System.out.println(ev);
//				}
//				//System.out.println("Checking "+st+ "("+st2+","+st3+")");
//				//System.out.println("Checking "+st+" "+st2+" "+st3);
//			}
//			//	occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0).equals("arrive")
//			System.out.println(ar);
//		}
		//State s = stateList.get(0);
		
		
	
		//return "";
	}
	
    
}
