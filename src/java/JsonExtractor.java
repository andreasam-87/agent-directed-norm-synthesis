import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonExtractor {

	//private static final Class<? extends Object>  = null;
	StringBuilder sbb = new StringBuilder("");
	int check =0,pass=0,first=0,traceCount=0;
	String inst;
	
	StringBuilder temp = new StringBuilder("");
	Set<String> tempSet;// = new HashSet<String>();
	
	//StringBuilder examples = new StringBuilder("");
	
	public JsonExtractor(String inst) {
		this.inst = inst;
	}
	
	public JsonExtractor() {
		this.inst = "rooms";
	}
	
	protected String extractDeeper(JSONArray ext)
	{
		//for (String key: ext.get())
		for(int i=0;i<ext.length();i++)
		{
			if(ext.get(i) instanceof JSONArray)
				extractDeeper(ext.getJSONArray(i));
			else
				temp.append(ext.get(i).toString());
				
				//System.out.println("trying stuff");
			//ext.get(i);
			temp.append(",");
		}
		
		return temp.toString();
		
	}

	
/* found online - https://www.programmersought.com/article/3218326044/ [retrieved 26/01/2021]
 * Trying to make it work for me */
	protected StringBuilder extract(Object obj, StringBuilder sb)
	{
		this.sbb = sb;
		analysisJson(obj);
		return sbb;
	}
	
	 protected void  analysisJson(Object objJson){
		 HashMap<String, Object> map = new HashMap<>();
	    	
		
		 
		 // If obj is a json array
	    	if(objJson instanceof JSONArray){
	    		JSONArray objArray = (JSONArray)objJson;
	    		//System.out.println("here");
	    		check =0;
	    		pass=0;
//	    		if(objArray.length()>1)
//					c++;
				for (int i = 0; i < objArray.length(); i++) {
					//System.out.println(" trying  a thing"+objArray.get(i));
					
						//sbb.append(")"+objArray.get(i).toString());
//					if(i>0)
//						sbb.append(",");
					analysisJson(objArray.get(i));
				
//					if(c==1)
//						sbb.append(",");
					//sbb.append("("+objArray.get(i).toString());
				}
				
				sbb.append(")");
				//check =0;
	    	}
	    	 // If the json object
	    	else if(objJson instanceof JSONObject){
	    		System.out.println("Get here");
	    		JSONObject jsonObject = (JSONObject)objJson;
				Iterator it = jsonObject.keys();
				while(it.hasNext()){
					String key = it.next().toString();
					Object object = jsonObject.get(key);
					 // If you get an array
					if(object instanceof JSONArray){
						JSONArray objArray = (JSONArray)object;
						analysisJson(objArray);
					}
					 // If the key is a json object
					else if(object instanceof JSONObject){
						analysisJson((JSONObject)object);
					}
					 // If the key is other
					else{
						System.out.println("["+key+"]:"+object.toString()+" ");
					}
				}
	    	}
	    	else
	    	{
	    		
	    		//System.out.println("check: "+check);
	    		if(check!=0)
	    		{
	    			if(pass>0)
		    			sbb.append(",");
	  
	    			sbb.append(objJson.toString());
	    		}
	    			
	    		else
	    			sbb.append("("+objJson.toString());
	    		
	    		//	else
	    				
	    		pass++;
	    	}
	    		//System.out.println(objJson.toString());
	    	check++;
	    	//sbb.append(";");
	    	//System.out.println("my sbb: "+sbb.toString());
	    }
	
	 protected void  analyseJson(Object objJson){
		 HashMap<String, Object> map = new HashMap<>();
	    		 
		 // If obj is a json array
	    	if(objJson instanceof JSONArray){
	    		JSONArray objArray = (JSONArray)objJson;
	    		//System.out.println("here");
	    		check =0;
	    		pass=0;
	    		first=0;

				for (int i = 0; i < objArray.length(); i++) {
				
					analyseJson(objArray.get(i));

				}
				
				sbb.append(")");
				//check =0;
	    	}
	    	 // If the json object
	    	else if(objJson instanceof JSONObject){
	    		System.out.println("Get here");
	    		JSONObject jsonObject = (JSONObject)objJson;
				Iterator it = jsonObject.keys();
				while(it.hasNext()){
					String key = it.next().toString();
					Object object = jsonObject.get(key);
					 // If you get an array
					if(object instanceof JSONArray){
						JSONArray objArray = (JSONArray)object;
						analysisJson(objArray);
					}
					 // If the key is a json object
					else if(object instanceof JSONObject){
						analysisJson((JSONObject)object);
					}
					 // If the key is other
					else{
						System.out.println("["+key+"]:"+object.toString()+" ");
					}
				}
	    	}
	    	else
	    	{
	    		
	    		if(check!=0)
	    		{
	    			if(pass>0)
		    			sbb.append(",");
	  
	    			sbb.append(objJson.toString());
	    		}
	    		
	    		else
	    		{
	    			System.out.println("First is "+first);
	    			if(first==0)
	    				sbb.append(objJson.toString());
	    			else
	    				sbb.append("("+objJson.toString());
	    			
	    			first++;
	    		}
	    			
	    		
	    		//	else
	    				
	    		pass++;
	    	}
	    	check++;
	    }
	 
	/*public void loopThroughJson(Object input) throws JSONException {
	    if (input instanceof JSONObject) {
	        Iterator<?> keys = ((JSONObject) input).keys();
	        while (keys.hasNext()) {
	            String key = (String) keys.next();
	            if (!(((JSONObject) input).get(key) instanceof JSONArray))
	                if (((JSONObject) input).get(key) instanceof JSONObject) {
	                    loopThroughJson(((JSONObject) input).get(key));
	                } else
	                    System.out.println(key + "=" + ((JSONObject) input).get(key));
	            else
	                loopThroughJson(new JSONArray(((JSONObject) input).get(key).toString()));
	        }
	    }
	    if (input instanceof JSONArray) {
	        for (int i = 0; i < ((JSONArray) input).length(); i++) {
	            JSONObject a = ((JSONArray) input).getJSONObject(i);
	            loopThroughJson(a);
	        }
	    }
	}*/
	
	
	protected JSONArray extractHoldsat(JSONObject toExtract)
	{
		JSONArray j;
		JSONArray ret=new JSONArray();
		for (String keys : toExtract.keySet()) 
		{
		    if(keys.equals("fluents"))// && toExtract.get(keys)!=null)// toExtract.get(keys).equals(null)) // ArrayUtils.isEmpty(toExtract.get(keys))) // toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    		ret.put(j);
		    		
		    }
		    else if(keys.equals("gpows"))//) && !toExtract.get(keys).equals(null))//!=null) //((JSONArray)toExtract.get(keys).length()))//.isNull()) // toExtract.get(keys)!=null) // toExtract.get(keys).equals(null))//&& toExtract.get(keys)!=null)
		    { 
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    		ret.put(j);
		    	
		    	
		    }
		    else if(keys.equals("ipows")) // && toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    		ret.put(j);
		    	
		    }
		    else if(keys.equals("tpows")) // && toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    		ret.put(j);
		    	
		    }
		    else if(keys.equals("obls")) // && toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    		ret.put(j);
		    	
		    }
		    else if(keys.equals("perms")) // && toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    		ret.put(j);
		    	
		    }
		    else if(keys.equals("pows")) // && toExtract.get(keys)!=null)
		    {
		    	j = (JSONArray)toExtract.get(keys);
		    	if (j.length()!=0)
		    		ret.put(j);
		    	
		    }
		    else
		    {
		    	System.out.println("ERROR 1");
		    }
		}
		return ret;
		
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
	protected String parseStr(String toParse, int flag)
	{
		//int index = toParse.lastIndexOf(")");
		int indx = toParse.indexOf(")");
		int i;
		//System.out.println("index - "+indx);
		for(i=indx;i<toParse.length()-1;i++)
		{
			if(toParse.charAt(i)!=')')
				break;
		}
		//System.out.println(toParse.charAt(i));
		toParse = toParse.substring(0, i); //cuts the end of the string past the first comma
		toParse = toParse.replace("(holdsat(", "");//removes the whole holdsat bit at the front
		toParse = toParse.substring(0,toParse.length()-1);//removes the last )
		if(toParse.chars().filter(ch -> ch == '(').count() < toParse.chars().filter(ch -> ch == ')').count() )
		{
			toParse = toParse.replaceFirst("\\)","");
		}
		if(flag==0)
		{
			toParse = "initially("+toParse+","+inst+")";//have a variable with the name of the institution to add here
		}
		return toParse;
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
	
	public String getStateFactsandEvents(int stateKey, HashMap <Integer,State> stateList)
	{
		State state = (State)stateList.get(stateKey);
		
		String ev = state.getEventsStr();
	
		String fct = state.getFactsStr();
		
		return fct+ev;
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
			String st2,st3;
			//			String st2 = facts.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
//			String st3 = facts.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(1);
			
            if (facts.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).length()==1)
            {
            	st2 = facts.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
				st3 = "";
            }
            else
            {
            	st2 = facts.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(0);
				st3 = facts.getJSONArray(i).getJSONArray(1).getJSONArray(0).getJSONArray(1).getString(1);
//				 
            }
			
			String ev = st+ "("+st2+","+st3+"),";
			sb.append(ev);
		}
		//System.out.println("Facts string: "+sb);
		return sb.toString();
	}
	
	
	public int checkStateForEvent(String event, HashMap <Integer,State> stateList)
	{
		int stateOccurred=0;

		// Using for-each loop 
        for (Map.Entry mapElement : stateList.entrySet()) { 
            int key = (int)mapElement.getKey(); 
            State state = (State)mapElement.getValue();
            
            String events = state.getEventsStr();
           
            if(events.contains(event))
            {
            	stateOccurred = key;
            //	System.out.println("Found "+event+ " in "+ events);
            	break;
            } 
            
        } 
        
        return stateOccurred;

	}
	
	
	public int checkStateForEventOld(String event, HashMap <Integer,State> stateList)
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
	
	
	public String getModesFile(String file, String filter)
	{
		/*Function to parse a json file with instal dictionary and return a modes file as string 
		 * This function may need to be modified to should only a specific set of fluents and events rather than all
		 * */
		
		String line="";
		String toReturn = "";
		StringBuilder modeh=new StringBuilder();
		StringBuilder modeb=new StringBuilder();
		StringBuilder examplepattern=new StringBuilder();
		
		tempSet = new HashSet<String>();
		 
		try {
			System.out.println("trying to read this file");
			List<String> lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
	    	for (String line2 : lines) {
	    	//System.out.println("line read: " + line2);
	    		line+=line2;
	    	}
			
	//		System.out.println("pass 1");
			//JSONObject object = new JSONObject(strRet.toString());

			JSONObject object = new JSONObject(line);
//			JSONArray jArr = object.getJSONArray("institution_ir");
//			JSONObject obj1 = jArr.getJSONObject(0);		
//			System.out.println("Object : "+obj1);
//			//System.out.println("Object : "+object.getJSONArray("institution_ir").getJSONObject(0).getJSONArray("noninertial_fluents"));
	
//			JSONObject obj2 = obj1.getJSONObject("contents");		
//			JSONObject obj3 = obj2.getJSONObject("noninertial_fluents");
			
			JSONObject niFluents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("noninertial_fluents");
			JSONObject fluents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("fluents");
			JSONObject ievents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("inevents");
			JSONObject vievents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("vievents");
			JSONObject exevents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("exevents");
			
			JSONArray initials = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONArray("initials");// .getJSONObject("initiates");
			JSONArray initiates = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONArray("initiates");// .getJSONObject("initiates");
			JSONArray generates = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONArray("generates");// .getJSONObject("initiates");
			
			
			ArrayList <String> modesh = getModesList(niFluents,'f');
			for(String s: modesh)
			{
				modeh.append("modeh(holdsat("+s);
				modeb.append("modeb(holdsat("+s);
				modeb.append("modeb(not holdsat("+s);
				examplepattern.append("examplePattern((holdsat("+s);
			}
			
			modesh = getModesList(fluents,'f');
			for(String s: modesh)
			{
				modeh.append("modeh(initiated("+s);
				modeh.append("modeh(terminated("+s);
				modeb.append("modeb(holdsat("+s);
				modeb.append("modeb(not holdsat("+s);
				examplepattern.append("examplePattern((holdsat("+s);
			}
			
			modesh = getModesList(ievents,'e');
			for(String s: modesh)
			{
				modeh.append("modeh(occurred("+s);
				modeb.append("modeb(occurred("+s);
				modeb.append("modeb(not occurred("+s);
				examplepattern.append("examplePattern((occurred("+s);
			}
			
			modesh = getModesList(vievents,'e');
			for(String s: modesh)
			{
				modeh.append("modeh(occurred("+s);
				modeb.append("modeb(occurred("+s);
				modeb.append("modeb(not occurred("+s);
				examplepattern.append("examplePattern((occurred("+s);
			}
			
			//Do I need the exogeneous or observable events 
			modesh = getModesList(exevents,'e');
			for(String s: modesh)
			{
			//	Deciding to justt add them as possible body elements
				//modeh.append("modeh(observed("+s);
				modeb.append("modeb(observed("+s);
				modeb.append("modeb(not observed("+s);
			//	examplepattern.append("examplePattern((observed("+s);
			}
			
//			System.out.println("Checking out the initiates and generates");
//			getModesListFromArray(initiates,'f');
			System.out.println("Checking out the initiallies");
		//	getModesListFromArray(initials,'f');
			
			modesh = getModesListFromArray(initials,'f',"none");
			for(String s: modesh)
			{
				modeh.append("modeh(initiated("+s);
				modeh.append("modeh(terminated("+s);
				modeb.append("modeb(holdsat("+s);
				modeb.append("modeb(not holdsat("+s);
				examplepattern.append("examplePattern((holdsat("+s);
			}
			
			toReturn = modeh.toString()+"\n"+modeb.toString()+"\n"+examplepattern.toString();
			
			if(!(filter.equals("")))
			{
				System.out.println("Filtering the modes file for "+filter);
				
				//get the items needed to do some filtering on the modes stuff
				modesh = getModesListFromArray(initiates,'f',filter);
				modesh = getModesListFromArray(generates,'f',filter);
				
				String [] s_a = tempSet.toArray(new String[0]); //converts the object array to string array
				
				//*alternative method for above stackoverflow
				//String[] stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);*./
		
				//Filtering each list
				HashSet<String> set1 = filterList(modeh.toString(),s_a);

				HashSet<String> set2 = filterList(modeb.toString(),s_a);

				HashSet<String> set3 = filterList(examplepattern.toString(),s_a);
				
				//creating string to return 
				String write = setToString(set1)+"\n"+setToString(set2)+"\n"+setToString(set3);
				toReturn = write;
			//	System.out.println("After function call");
				printArrayContents(tempSet.toArray());
				//printArrayContents(set2.toArray());
				
			//	Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/test.txt"), write.getBytes());
				//return write;
			}
			//get the items needed to do some filtering on the modes stuff
//			modesh = getModesListFromArray(initiates,'f',"enter");
//			modesh = getModesListFromArray(generates,'f',"enter");
//
//			//System.out.println("Before function call");
//			//String [] s_a = ((String[])(Object[])tempSet.toArray()); ///whyyyyyy
//			String [] s_a = tempSet.toArray(new String[0]);
//			
//			//	System.out.println("Array casting "+ s_a.getClass());
//			HashSet<String> set1 = filterList(modeh.toString(),s_a);
//
//			HashSet<String> set2 = filterList(modeb.toString(),s_a);
//
//			HashSet<String> set3 = filterList(examplepattern.toString(),s_a);
//			String write = setToString(set1)+"\n"+setToString(set2)+"\n"+setToString(set3);
//		//	System.out.println("After function call");
//			printArrayContents(tempSet.toArray());
//			//printArrayContents(set2.toArray());
//			//printArrayContents(tempSet.toArray());
//			Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/test.txt"), write.getBytes());
//			
//	
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error with json modes file");
			e1.printStackTrace();
		}
	//	System.out.println("Still reached here");
		return toReturn;
		//return modeh.toString()+"\n"+modeb.toString()+"\n"+examplepattern.toString();
		//return modeh.toString()+modeb.toString()+examplepattern.toString();
	}

	public String getModesFileXhail(String file, String filter)
	{
		/*Function to parse a json file with instal dictionary and return a modes file as string 
		 * This function may need to be modified to should only a specific set of fluents and events rather than all
		 * */
		
		String line="";
		String toReturn = "";
		StringBuilder modeh=new StringBuilder();
		StringBuilder modeb=new StringBuilder();
		StringBuilder examplepattern=new StringBuilder();
		
		tempSet = new HashSet<String>();
		 
		try {
			System.out.println("trying to read this file");
			List<String> lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
	    	for (String line2 : lines) {
	    	//System.out.println("line read: " + line2);
	    		line+=line2;
	    	}

			JSONObject object = new JSONObject(line);
		
			JSONObject niFluents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("noninertial_fluents");
			JSONObject fluents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("fluents");
			JSONObject ievents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("inevents");
			JSONObject vievents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("vievents");
			JSONObject exevents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("exevents");
			
			JSONArray initials = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONArray("initials");// .getJSONObject("initiates");
			JSONArray initiates = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONArray("initiates");// .getJSONObject("initiates");
			JSONArray generates = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONArray("generates");// .getJSONObject("initiates");
			
			
			ArrayList <String> modesh = getModesListXhail(niFluents,'f');
			for(String s: modesh)
			{
				modeh.append("#modeh holdsat("+s);
				modeb.append("#modeb holdsat("+s);
				modeb.append("#modeb not holdsat("+s);
			}
			
			modesh = getModesListXhail(fluents,'f');
			for(String s: modesh)
			{
				modeh.append("#modeh initiated("+s);
				modeh.append("#modeh terminated("+s);
				modeb.append("#modeb holdsat("+s);
				modeb.append("#modeb not holdsat("+s);
			}
			
			modesh = getModesListXhail(ievents,'e');
			for(String s: modesh)
			{
				modeh.append("#modeh occurred("+s);
				modeb.append("#modeb occurred("+s);
				modeb.append("#modeb not occurred("+s);
			}
			
			modesh = getModesListXhail(vievents,'e');
			for(String s: modesh)
			{
				modeh.append("#modeh occurred("+s);
				modeb.append("#modeb occurred("+s);
				modeb.append("#modeb not occurred("+s);
			}
			
			//Do I need the exogeneous or observable events 
			
			//not using the observable events for now - Aug 10, 2022
			/*
			modesh = getModesListXhail(exevents,'e');
			for(String s: modesh)
			{
			//	Deciding to justt add them as possible body elements
				//modeh.append("modeh(observed("+s);
				modeb.append("#modeb observed("+s);
				modeb.append("#modeb not observed("+s);
			//	examplepattern.append("examplePattern((observed("+s);
			}
			*/
			
//			System.out.println("Checking out the initiates and generates");
//			getModesListFromArray(initiates,'f');
			System.out.println("Checking out the initiallies");
		//	getModesListFromArray(initials,'f');
			
			modesh = getModesListFromArrayXhail(initials,'f',"none");
			for(String s: modesh)
			{
				modeh.append("#modeh initiated("+s);
				modeh.append("#modeh terminated("+s);
				modeb.append("#modeb holdsat("+s);
				modeb.append("#modeb not holdsat("+s);
			}
			
			toReturn = modeh.toString()+"\n"+modeb.toString()+"\n"; //+examplepattern.toString();
			
			if(!(filter.equals("")))
			{
				System.out.println("Filtering the modes file for "+filter);
				
				//get the items needed to do some filtering on the modes stuff
				modesh = getModesListFromArrayXhail(initiates,'f',filter);
				modesh = getModesListFromArrayXhail(generates,'f',filter);
				
				String [] s_a = tempSet.toArray(new String[0]); //converts the object array to string array
				
				//*alternative method for above stackoverflow
				//String[] stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);*./
		
				//Filtering each list
				HashSet<String> set1 = filterList(modeh.toString(),s_a);

				HashSet<String> set2 = filterList(modeb.toString(),s_a);

				//HashSet<String> set3 = filterList(examplepattern.toString(),s_a);
				
				//creating string to return 
				String write = setToString(set1)+"\n"+setToString(set2); //+"\n"+setToString(set3);
				toReturn = write;
			//	System.out.println("After function call");
				printArrayContents(tempSet.toArray());

			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error with json modes file");
			e1.printStackTrace();
		}
		//not needed since already in the template file
		//toReturn = toReturn + "<<exception>>";
		return toReturn;

	}

	
	
	 /* Function to pull a set's items into a single string*/
	private String setToString(HashSet<String> set)
	{
		//System.out.println("Array Contents");
		String str = "";
		for(String s: set)
		{
			str+=s+"\n";
		}
		return str;
	}
	
	
   /* Function to filter the modes lists based on some specific term specified */
	private HashSet<String> filterList(String toFilter,String [] terms)
	{
		//System.out.println("Entered function");
		//should use a set rather than an arraylist here to fix duplicates
		HashSet <String> retList = new HashSet<String>();
				
		String [] filter_array = toFilter.split("\n");

		//navigates the specific modes list
		for(String str: filter_array)
		{
		//	int check =0;
		//	System.out.println("Entered first loop");
			
			//navigates the terms list
			for(int i=0;i<terms.length;i++)
	    	{	
				//System.out.println("Entered second loop");
				
				//only adds to the set if the modes item contains the term
	    		if(str.contains(terms[i]))
	    		{
	    			//System.out.println("Adding: "+ str);
	    			retList.add(str);
	   // 			check++;
	    		}
	    	}
		}
		
		//System.out.println("Leaving function");
		return retList;
	}
	
	
	/*check initiates and build a list of fluents to keep then remove the rest from the current list, 
	or only grab that from the current set  */
	
	private ArrayList<String> getModesListFromArray(JSONArray toext,char t, String custom)
	{
		ArrayList <String> retList = new ArrayList<String>();
		//Set<String> tempSet = new HashSet<String>();
		//System.out.println("Items from Initiates:");

		for (int j=0; j<toext.length();j++)
		{
			JSONArray jArr = (JSONArray)toext.get(j);	
			StringBuilder tmp = new StringBuilder();
			
			this.sbb = tmp;
			analysisJson((Object)jArr);
		//	System.out.println(sbb.toString());
			
			
			String str = sbb.toString();
	//		System.out.println(str);
			if(custom.equals("none"))
			{
				str = parseStr(str,1);
				str = str.replaceFirst("\\(","");
				str = str.replaceFirst("\\)","");
				//System.out.println(str);
				if(str.startsWith("perm"))
				{
					str = str.replace("P","+person");
					str = str.replace("L","+location");
					if(t=='e')
					{
						str+=",+inst,+instant))\n";
					}else
					{
						str+=",+inst,+event,+instant))\n";
					}
					retList.add(str);
				}
			
			}
			else
			{
				//first check the string to see if it contains the custom filter before doing any of this
				//otherwise move on to the next one.
				if (str.contains(custom))
				{
					System.out.println("String: "+str);
					
					String strArr[] = StringUtils.split(str, "("); 
					Set<String> set = getCustomFluentList(strArr);
					tempSet.addAll(set);
				}
				
				//printArrayContents(set.toArray());
				//Set
				//System.out.println(strArr);
				
			}

		}
	//	printArrayContents(tempSet.toArray());
		return retList;

	}

	private ArrayList<String> getModesListFromArrayXhail(JSONArray toext,char t, String custom)
	{
		ArrayList <String> retList = new ArrayList<String>();

		for (int j=0; j<toext.length();j++)
		{
			JSONArray jArr = (JSONArray)toext.get(j);	
			StringBuilder tmp = new StringBuilder();
			
			this.sbb = tmp;
			analysisJson((Object)jArr);
		
			String str = sbb.toString();
	//		System.out.println(str);
			if(custom.equals("none"))
			{
				str = parseStr(str,1);
				str = str.replaceFirst("\\(","");
				str = str.replaceFirst("\\)","");
				//System.out.println(str);
				if(str.startsWith("perm"))
				{
					str = str.replace("P","+person");
					str = str.replace("L","+location");
					/*if(t=='e')
					{
						str+=",+inst,+instant).\n";
					}else
					{
						str+=",+inst,+event,+instant).\n";
					}*/
					str+=",+inst,+instant).\n";
					retList.add(str);
				}
			
			}
			else
			{
				//first check the string to see if it contains the custom filter before doing any of this
				//otherwise move on to the next one.
				if (str.contains(custom))
				{
					System.out.println("String: "+str);
					
					String strArr[] = StringUtils.split(str, "("); 
					Set<String> set = getCustomFluentList(strArr);
					tempSet.addAll(set);
				}
				
			}

		}
	//	printArrayContents(tempSet.toArray());
		return retList;

	}

	
	
	/*Function to access extract the fluents to construct a modes list
	 * */
	
	private ArrayList<String> getModesList(JSONObject toext,char t)
	{
		//should use a set rather than an arraylist here to fix duplicates
		ArrayList <String> retList = new ArrayList<String>();
		for (String key : toext.keySet()) 
		{
			String str="";
			
			//System.out.println(key +" : "+ niFluents.get(key)+" - "+ niFluents.get(key).getClass());
		//	modeh.append("modeh(holdsat("+key+"(");
		//	examplepattern.append("examplePattern((holdsat("+key+"(");
			str+=key+"(";
			//JSONArray jArr = (JSONArray)niFluents.get(key);
			JSONArray jArr = (JSONArray)toext.get(key);
			for(int i=0;i<jArr.length();i++)
	    	{
				//System.out.println(jArr.get(i)+" "+jArr.get(i).getClass());
	    	//	modeh.append("+"+((String)jArr.get(i)).toLowerCase());
	    	//	examplepattern.append("+"+((String)jArr.get(i)).toLowerCase());
				str+="+"+((String)jArr.get(i)).toLowerCase();
	    		if(i!=(jArr.length()-1))
	    		{
	    		//	modeh.append(",");
	    		//	examplepattern.append(",");
	    			str+=",";
	    		}
	    	}
		//	modeh.append("),+inst,+event,+instant))\n");	
		//	examplepattern.append("),+inst,+event,+instant))\n");
			if(t=='e')
			{
				str+="),+inst,+instant))\n";
			}else
			{
				str+="),+inst,+event,+instant))\n";
			}
			
			retList.add(str);
		}
		return retList;
	}
	
	private ArrayList<String> getModesListXhail(JSONObject toext,char t)
	{
		//should use a set rather than an arraylist here to fix duplicates
		ArrayList <String> retList = new ArrayList<String>();
		for (String key : toext.keySet()) 
		{
			String str="";
			
			str+=key+"(";
			//JSONArray jArr = (JSONArray)niFluents.get(key);
			JSONArray jArr = (JSONArray)toext.get(key);
			for(int i=0;i<jArr.length();i++)
	    	{
				str+="+"+((String)jArr.get(i)).toLowerCase();
	    		if(i!=(jArr.length()-1))
	    		{
	    		
	    			str+=",";
	    		}
	    	}
			/*if(t=='e')
			{
				str+="),+inst,+instant).\n";
			}else
			{
				str+="),+inst,+event,+instant).\n";
			}*/
			int count = StringUtils.countMatches(str,"number");
			if(count==2)
			{
				str = replaceLast("number","number2",str);
			}
			
			str+="),+inst,+instant).\n";
			if(!str.contains("revise") || !str.contains("_create_"))
			{
				retList.add(str);	
			}
		}
		return retList;
	}
	
	public String getTraceFile(int stateKey, int howMuchStates,HashMap <Integer,State> stateList )
	{
		/*Function to access state facts and return a trace file as string 
		 * This function may need to be modified to allow changes to the fluents and events 
		 * to properly represent/demonstrate the rule that needs to be learnt
		 * */

		System.out.print("Retrieving states: ");
		
		StringBuilder ret=new StringBuilder();
		
		// Loop through to identify states to use
		for (int find=(stateKey-howMuchStates);find<=(stateKey+howMuchStates);find++)
		{
			if(stateList.containsKey(find))
			{
				System.out.print(find+" ");
				
				String facts=getStateFactsandEvents(find,stateList);
		
				String [] facts_array = facts.split("\n");
			//	System.out.println("Size of facts: "+ facts_array.length);
				ret.append("%Timestep "+find+".\n");
				for(String str: facts_array)
				{
					//StringUtils.
					str = str.replace("initially","holdsat");
					String tempstr = str;
					str = replaceLast(")",","+find+")",str);
					
					/*
					 * Attempting to find the occurrence of an agent in the string 
					 * and filtering only for agents related to the synthesiser revising now
					 * 
					System.out.println("#### Filtered String - "+ tempstr);
					int index1= tempstr.indexOf("(");
					int index2= tempstr.lastIndexOf(",");
					String tempSubString = tempstr.substring(index1+1, index2);
					System.out.println("#### Filtered String 2 - "+ tempSubString);
					// an if statement that looks for a particular fluent, it can change holdsat to not holdsat at the simplest case
					if(tempSubString.matches("\\w+\\(\\w+\\)"))
					{
						//\(+\w+(?:\(+\w+\))?,\w+,\w+\)
						//result = re.findall('(?:not )?holdsat+\(+\w+(?:\(+\w+\))?,\w+,\w+\)|(?:not )?occurred+\(+\w+(?:\(\w+,?\w+\))?,\w+,\w+\)', toAdd)
		                
						System.out.println("#### Matches string with only one item in bracket - "+ tempSubString);
					}
					if(tempSubString.matches("\\w+(?:\\(+\\w+\\))?,\\w+,\\w+\\)"))
					{
						System.out.println("#### Matches normal string with comma - "+ tempSubString);
					}
					*/
					ret.append(str+"\n");
				}
				
			}
			ret.append("\n\n");
		}
		System.out.println("Finished Retrieval");
		return ret.toString();
	
	}
	
	public ArrayList<Object> getTraceFileXhail(int stateKey, int howMuchStates,HashMap <Integer,State> stateList, String toAdd, String toModify, ArrayList<String> allAgents, ArrayList<String> subsetAgents)
	{
		/*Function to access state facts and return a trace file as string 
		 * This function may need to be modified to allow changes to the fluents and events 
		 * to properly represent/demonstrate the rule that needs to be learnt
		 * */
		
		StringBuilder examples = new StringBuilder(); //examples.setLength(0);
		HashSet<String> examp = new HashSet();
		System.out.print("Retrieving states: ");
		toModify = toModify.trim();
		System.out.println("To modify /////////"+ toModify+"/////////// ");
		StringBuilder ret=new StringBuilder();
		
		int traceCount = 0;
		int count = 0;
		String forExamplesList="";
		
		int countBrackets = StringUtils.countMatches(toModify, "(");
		if(countBrackets>1)
			forExamplesList =StringUtils.substringBeforeLast(toModify, "(").trim();
		else
			forExamplesList =StringUtils.substringBefore(toModify, "(").trim();
		
			
		
		System.out.println("To modify 2 /////////"+ forExamplesList+"/////////// ");
		
		// Loop through to identify states to use
		for (int find=(stateKey-howMuchStates);find<=(stateKey+howMuchStates);find++)
		{
			if(stateList.containsKey(find))
			{
				//System.out.print(find+" ");
				
				String facts=getStateFactsandEvents(find,stateList);
		
				String [] facts_array = facts.split("\n");
			//	System.out.println("Size of facts: "+ facts_array.length);
				ret.append("%Timestep "+count+".\n");
				for(String str: facts_array)
				{
					//StringUtils.
					str = str.replace("initially","holdsat");
					//str = replaceLast(")",","+count+")",str);
					
					if(str.contains("occurred"))
					{
						str = replaceLast(")",","+inst+","+count+")",str);
					}
					else
					{

						str = replaceLast(")",","+count+")",str);
					}
					
					// an if statement that looks for a particular fluent, it can change holdsat to not holdsat at the simplest case	
					//probably need to remove this to a new structure to retrieve for the example file.
					
					//if(str.contains(toModify.substring(, endIndex))) 
					//substringBefore("This is my string", " "));
					
					
				//	System.out.println("CHECKING /////////"+ str+"/////////// for ///////" + forExamplesList + "/////////");
					if(str.contains(forExamplesList))
					{
						//System.out.print("Contains capacaity exceeded - "+str);
						if(str.contains(toModify) && find>=stateKey)
						{
							System.out.print("String we are putting in example file - "+str+" - tomodify is "+ toModify);
							str = "not "+str;
							examples.append("#example "+str+".\n");
							examp.add("#example "+str+".\n");
							/*Maybe I need to add the occurrence of the good event here 
							 * Decided to add it for now
							 * */
							if(toAdd.contains("occurred"))
							{
								examples.append("#example "+toAdd+","+count+").\n");
								examp.add("#example "+toAdd+", "+count+").\n");
							}
							
						}
						else {
							examples.append("#example "+str+".\n");
							examp.add("#example "+str+".\n");
						}
					}
					else
					{
						if(!str.contains("revise"))
						{
							if(!str.contains("_create_"))
							{
								if(str.length()>0)
								{
										ret.append(str+".\n");	
									
								}
								
							}
							
						}
						
					}
					
				
				}
				// add a new fluent if necessary
				if(find>=stateKey)
				{
					String add = toAdd+",rooms,"+count+")";
			
					System.out.println("//////"+add);
					
					if(!toAdd.contains("occurred"))
						ret.append(add+".\n");
				}
				count++;
				traceCount++;
			}
			ret.append("\n\n");
			examples.append("\n\n");
			examp.add("\n");
		}
		examples.append("%%%For 1st agent");
		System.out.println("Finished Retrieval");
		//System.out.println("Examples are : \n"+examples.toString());

		//System.out.println("Examples from hashset are also : \n"+examp.toString());
		//return ret.toString();
	
		ArrayList<Object> response = new ArrayList<Object>();
		response.add(ret);
		response.add(traceCount - 1);
		response.add(examples);
		return response;
	}
	
	public ArrayList<Object> getLocalTraceFileXhail(int stateKey, int howMuchStates,HashMap <Integer,State> stateList, String toAdd, String toModify, ArrayList<String> allAgents, ArrayList<String> subsetAgents)
	{
		/*Function to access state facts and return a trace file as string 
		 * This function may need to be modified to allow changes to the fluents and events 
		 * to properly represent/demonstrate the rule that needs to be learnt
		 * */
		
		StringBuilder examples = new StringBuilder(); //examples.setLength(0);
		HashSet<String> examp = new HashSet();
		System.out.print("Retrieving states: ");
		toModify = toModify.trim();
		System.out.println("To modify /////////"+ toModify+"/////////// ");
		StringBuilder ret=new StringBuilder();
		String temp="",subStr="";
		int traceCount = 0;
		int count = 0;
		String forExamplesList="";
		
		
		HashMap<String, ArrayList<String>> specialPercept = new HashMap<String, ArrayList<String>>();
		specialPercept.put("original",new ArrayList<String>());
		//specialPercept.put("modified",new ArrayList<String>());
		specialPercept.put("examples",new ArrayList<String>());
		specialPercept.put("agents",new ArrayList<String>());
		
		boolean found = false;
		//agentsBySynthesizerInMAS.get("synthesizer1").add(ag);
		//agentsBySynthesizerInMAS.put(ag,new ArrayList<String>());
		
		int countBrackets = StringUtils.countMatches(toModify, "(");
		if(countBrackets>1)
			forExamplesList =StringUtils.substringBeforeLast(toModify, "(").trim();
		else
			forExamplesList =StringUtils.substringBefore(toModify, "(").trim();
		
		if(toModify.contains("|"))
		{
			forExamplesList= toModify;
			//System.out.println()
			//toAdd="";
		}
		else if(toModify.contains("missing"))
		{
			forExamplesList= toModify;
			//toAdd="";
		}else if(forExamplesList.equals("perm"))
		{
			forExamplesList= toModify;
			toAdd="";
		}
		
		if(forExamplesList.equals("occurred"))
		{
			forExamplesList= toModify;
			temp=toAdd;
			toAdd="";
		}
		
		System.out.println("To modify 2 /////////"+ forExamplesList+"/////////// ");
		
	
		
		// Loop through to identify states to use
		for (int find=(stateKey-howMuchStates);find<=(stateKey+howMuchStates);find++)
		{
			if(stateList.containsKey(find))
			{
				//System.out.print(find+" ");
				
				String facts=getStateFactsandEvents(find,stateList);
		
				String [] facts_array = facts.split("\n");
			//	System.out.println("Size of facts: "+ facts_array.length);
				ret.append("%Timestep "+count+".\n");
				int size = facts_array.length;
				
				if(factAboutAgent(facts_array[size-1],subsetAgents))
				{
					//this state is about the occurrence of an event by one of the agent's oversee-ees
				
				
					
					for(String str: facts_array)
					{
						//StringUtils.
						str = str.replace("initially","holdsat");
						//str = replaceLast(")",","+count+")",str);
						
						if(str.contains("occurred"))
						{
							str = replaceLast(")",","+inst+","+count+")",str);
						}
						else
						{

							str = replaceLast(")",","+count+")",str);
						}
						
						
						// an if statement that looks for a particular fluent, it can change holdsat to not holdsat at the simplest case	
						//probably need to remove this to a new structure to retrieve for the example file.
						
						//if(str.contains(toModify.substring(, endIndex))) 
						//substringBefore("This is my string", " "));
						
						
					//	System.out.println("CHECKING /////////"+ str+"/////////// for ///////" + forExamplesList + "/////////");
						
						
						//System.out.print("Contains capacaity exceeded - "+str);
						if(forExamplesList.contains("|")) 
						{
							//System.out.println("Looking for "+ StringUtils.substringBefore(forExamplesList,"|"));
							String early = StringUtils.substringBefore(forExamplesList,"|");
							early =early.replace("\"", "");
							if (str.contains(early) && factAboutAgent(str,subsetAgents))
							{
								System.out.println("I found this to add - "+str);
								
								str = "not "+str;
								examples.append("#example "+str+".\n");
								examp.add("#example "+str+".\n");
							}
							else if(str.contains("perm(enter") && factAboutAgent(str,subsetAgents))
							{
								/*int i = str.indexOf("perm(leave");
								int l = "perm(leave".length();
								subStr = StringUtils.substringBetween(str,"perm(leave", ")");
								
								examples.append("#example holdsat(in_room"+subStr+"),rooms,"+count+").\n");
								examp.add("#example holdsat(in_room"+subStr+"),rooms,"+count+").\n");
								
					
								ret.append(str+".\n");*/
								String tempEx = "#example not "+str+".\n";
								specialPercept.get("original").add(str+".\n");
								specialPercept.get("examples").add(tempEx);
								
								//specialPercept.put("original",new ArrayList<String>());
								//specialPercept.put("modified",new ArrayList<String>());
								//specialPercept.put("examples",new ArrayList<String>());
								
								//boolean found = false;
								//agentsBySynthesizerInMAS.get("synthesizer1").add(ag);
								
							}/*else if(str.contains("perm(enter") && !factAboutAgent(str,subsetAgents))
							{
								ret.append(str+".\n");
								
							}*/
							else if(str.contains("in_room"))
							{
								String tempStr = StringUtils.substringBetween(str,"in_room(", ",");	
								String tempStr2 = StringUtils.substringBetween(str,",", ")");	
								specialPercept.get("agents").add(tempStr+","+tempStr2);
								
								ret.append(str+".\n");
								
							//holdsat(in_room(baseAgent6,room1),rooms,1)
							}
							else
							{
								if(!str.contains("revise"))
								{
									if(!str.contains("_create_"))
									{
										if(str.length()>0)
										{
											//Need to filter by the synthesiser//
											if(factAboutAgent(str,allAgents))
											{
												//System.out.print("Fact about an agent - "+str+"\n");
												
												if(factAboutAgent(str,subsetAgents))
												{
														ret.append(str+".\n");
												}
												else if(str.contains("occurred") || str.contains("observed"))
												{
													ret.append(str+".\n");
													
												}
													
											}
											else
												ret.append(str+".\n");	
											
										}
										
									}
									
								}
								
							}
									
						}
						
						
						else if(str.contains(forExamplesList))
						{
							
							if(str.contains(toModify) && find>=stateKey)
							{
								System.out.println("String we are putting in example file - "+str+" - tomodify is "+ toModify);
								//str = "not "+str;
								//examples.append("#example "+str+".\n");
								//examp.add("#example "+str+".\n");
								
								//Looking for the missing percept
								if(str.contains("perm(leave") && factAboutAgent(str,subsetAgents))
								{
									int i = str.indexOf("perm(leave");
									int l = "perm(leave".length();
									subStr = StringUtils.substringBetween(str,"perm(leave", ")");
									//System.out.println("Substring to enter into examples "+sub);
									
									examples.append("#example holdsat(in_room"+subStr+"),rooms,"+count+").\n");
									examp.add("#example holdsat(in_room"+subStr+"),rooms,"+count+").\n");
									
									//still add the percept
									ret.append(str+".\n");
									//toAdd="";
									
									/*
									 * substringBetween(String str, String open, String close)
										Gets the String that is nested in between two Strings.
									 */
									
								}else if(str.contains("perm(leave") && !factAboutAgent(str,subsetAgents))
								{
									ret.append(str+".\n");
									//toAdd="";
									
								}
								else if(str.contains("deniedEntry") && factAboutAgent(str,subsetAgents))
								{
									//int i = str.indexOf("deniedEntry(");
									//int l = "deniedEntry(".length();
									subStr = StringUtils.substringBetween(str,"deniedEntry(", ")");
									//System.out.println("Substring to enter into examples "+sub);
									
									if(subStr.contains("vip"))
									{
										str = "not "+str;
										examples.append("#example "+str+".\n");
										examp.add("#example "+str+".\n");
										
										examples.append("#example not "+temp+subStr+"),rooms,"+count+").\n");
									}else
									{
										examples.append("#example "+str+".\n");
										examp.add("#example "+str+".\n");
										examples.append("#example "+temp+subStr+"),rooms,"+count+").\n");
									}
									
									//examples.append("#example holdsat(in_room"+sub+"),rooms,"+count+").\n");
									//examp.add("#example holdsat(in_room"+sub+"),rooms,"+count+").\n");
									//ret.append(str+".\n");
									//toAdd="";
									
								}
								else
								{
									str = "not "+str;
									//what to do normally
									examples.append("#example "+str+".\n");
									examp.add("#example "+str+".\n");
								}
								
								/*Maybe I need to add the occurrence of the good event here 
								 * Decided to add it for now
								 * */
								if(toAdd.contains("occurred"))
								{
									examples.append("#example "+toAdd+",rooms,"+count+").\n");
									examp.add("#example "+toAdd+",rooms,"+count+").\n");
								}
								
							}
							else {
								if(str.contains("perm(leave"))
								{
									ret.append(str+".\n");
									toAdd="";
								}
								else if(str.contains("denied"))
								{
									subStr = StringUtils.substringBetween(str,"deniedEntry(", ")");
									if(subStr.contains("vip"))
									{
										str = "not "+str;
										examples.append("#example "+str+".\n");
										examp.add("#example "+str+".\n");
										
										examples.append("#example not "+temp+subStr+"),rooms,"+count+").\n");
									}else
									{
										examples.append("#example "+str+".\n");
										examp.add("#example "+str+".\n");
										examples.append("#example "+temp+subStr+"),rooms,"+count+").\n");
									}
									
									//examples.append("#example "+str+".\n");
									//examp.add("#example "+str+".\n");
									//subStr = StringUtils.substringBetween(str,"deniedEntry(", ")");
									//examples.append("#example "+temp+subStr+"),rooms,"+count+").\n");
								}else
								{
									examples.append("#example "+str+".\n");
									examp.add("#example "+str+".\n");
								}
								
								//examples.append("#example "+temp+sub+"),rooms,"+count+").\n");
								
							}
						}
						else
						{
							if(!str.contains("revise"))
							{
								if(!str.contains("_create_"))
								{
									if(str.length()>0)
									{
										//Need to filter by the synthesiser//
										if(factAboutAgent(str,allAgents))
										{
											//System.out.print("Fact about an agent - "+str+"\n");
											
											if(factAboutAgent(str,subsetAgents))
											{
												if(toModify.contains("denied") && !str.contains("restrictAccess"))
													ret.append(str+".\n");
												else if(!toModify.contains("denied"))
													ret.append(str+".\n");
											}
											else if(str.contains("occurred") || str.contains("observed"))
											{
												ret.append(str+".\n");
												
												//For using null instead
												/*if(str.contains("occurred"))
													ret.append("occurred(null,"+inst+","+count+").\n");
												else
													ret.append("observed(null,"+count+").\n");
											
												
												holdsat(pow(null),rooms,0).
												occurred(enter(agent3,room2),0).
												observed(enter(agent3,room2),0).*/
	
											}
												
										}
										else
											ret.append(str+".\n");	
										
									}
									
								}
								
							}
							/*if(!str.contains("revise") || !str.contains("_create_"))
							{
								ret.append(str+".\n");	
							}
							//ret.append(str+"\n");*/
						}
						
						
					//ret.append(modifyFact(str,forExamplesList,toModify,find,stateKey));	
						
						
					}
					if(toModify.contains("denied"))
					{
						toAdd="";
					}
					
					if(toModify.contains("|"))
					{
						
						//specialPercept.get("original").add(str+".\n");
						//specialPercept.get("examples").add(tempEx);
						for (String perms : specialPercept.get("original")) 
						{ 
							for(String agent : specialPercept.get("agents"))
							{
								String agn = StringUtils.substringBefore(agent,",");
								String rm = StringUtils.substringAfter(agent,",");
								if(perms.contains(agent))
								{
									for(String ex : specialPercept.get("examples"))
									{
										if(ex.contains(agn) & ex.contains(rm))
										{
											examples.append(ex);
										}
									}
									//examples.append()
									found = true;
								}
							}
							if(!found)
							{
								ret.append(perms);
							}
							else
							{
								found = false;
							}
						    //statements using var;
						}
						
						specialPercept.put("original",new ArrayList<String>());
						specialPercept.put("examples",new ArrayList<String>());
						specialPercept.put("agents",new ArrayList<String>());
					}
					// add a new fluent if necessary
					if(find>=stateKey && !toAdd.isBlank())
					{
						System.out.println("I am adding - "+toAdd);
						String add = toAdd+",rooms,"+count+")";
					/*	if(toAdd.contains("(")) {
							int count = StringUtils.countMatches(reason, "(");
							if(count>1)
							{
								reason = StringUtils.substringBetween(reason, "(", "))");
								toAdd = reason+"("+room+"),rooms,"+when+")";
							}else {
								reason = StringUtils.substringBetween(reason, "(", ")");
								toAdd = reason+",rooms,"+when+")";
							}
							
						}*/
						
						System.out.println("//////"+add);
						/*Decided to not add anything to the trace file for now
						 * if(toAdd.contains("occurred"))
						{
							if(find==stateKey)
								ret.append(add+".\n");
						}
						else
							ret.append(add+".\n");*/
						if(!toAdd.contains("occurred"))
							ret.append(add+".\n");
					}
					//count++;
					//traceCount++;
				
				//using all the traces available
				}
				//trying something where the number is the correct sequence
				count++;
				traceCount++;
				ret.append("\n\n");
				examples.append("\n\n");
				examp.add("\n");
			}
		}
		examples.append("%%%For 1st agent");
		System.out.println("Finished Retrieval");
		//System.out.println("Examples are : \n"+examples.toString());

		//System.out.println("Examples from hashset are also : \n"+examp.toString());
		//return ret.toString();
	
		ArrayList<Object> response = new ArrayList<Object>();
		response.add(ret);
		response.add(traceCount - 1);
		response.add(examples);
		return response;
	}
	
	public boolean factAboutAgent(String fact, ArrayList<String> agents)
	{
		
		for (String agent : agents)
	      { 		      
	           if(fact.contains(agent))		
	        	   return true;
	      }
		
		return false;
	}
	public String modifyFact(String fact, String examples, String mod, int find, int stateKey)
	{
		String modifiedFact = "";
		String eFile = "";
		
		if(fact.contains(examples))
		{
			//System.out.print("Contains capacaity exceeded - "+str);
			if(fact.contains(mod) && find>=stateKey)
			{
				fact = "not "+fact;
				eFile = eFile + "#example "+fact+".\n";
			}
			else {
				eFile = eFile + "#example "+fact+".\n";
			}
		}
		else
		{
			if(!fact.contains("revise"))
			{
				if(!fact.contains("_create_"))
				{
					modifiedFact=fact+".\n";	
				}
				
			}
			
		}
		try {
			Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/examplesTest"), eFile.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return modifiedFact;
	}
	
	/*public int getTraceCount()
	{
		return traceCount-1;
	}
	
	public String getExampleDefintions()
	{
		return examples.toString();
	}*/
	
	/* Found here https://bytenota.com/java-replace-last-occurrence-of-a-string/ */
	 public static String replaceLast(String find, String replace, String string) 
	 {
	        int lastIndex = string.lastIndexOf(find);
	        
	        if (lastIndex == -1) {
	            return string;
	        }
	        
	        String beginString = string.substring(0, lastIndex);
	        String endString = string.substring(lastIndex + find.length());
	        
	        return beginString + replace + endString;
	 }
	 
	 
	private void printArrayContents(Object ar[])
	{
		System.out.println("Array Contents");
		for(int i=0;i<ar.length;i++)
		{
			System.out.println(ar[i]);
		}
	}
	
	/*Function to extract the terms from the generates and initiates list that are in 
	 * the same rule of the term to filter for*/
	private Set<String> getCustomFluentList(Object ar[])
	{
		Set<String> hash_Set = new HashSet<String>();
		//System.out.println("Array Contents");
		for(int i=0;i<ar.length;i++)
		{
			String s= (String)ar[i];
			if(!(s.contains("and")) && !(s.contains(")")) && !(s.contains("not")))
			{
			//	System.out.println("smh - "+ar[i]);
				if (s.equals("perm") || s.equals("pow"))
				{
					hash_Set.add(s+"("+(String) ar[i+1]);
					i++;
				}
				else
					hash_Set.add(s);
			}
//			else
//				System.out.println("here - "+ar[i]);
			//System.out.println(ar[i]);
		}
		return hash_Set;
	}
	
    
}