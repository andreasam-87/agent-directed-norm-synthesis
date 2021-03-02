import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonExtractor {

	//private static final Class<? extends Object>  = null;
	StringBuilder sbb = new StringBuilder("");
	int check =0,pass=0;
	String inst;
	
	
	public JsonExtractor(String inst) {
		this.inst = inst;
	}
	
	
	protected String extractDeeper(JSONArray ext)
	{
		//for (String key: ext.get())
		for(int i=0;i<ext.length();i++)
		{
			if(ext.get(i) instanceof JSONArray)
				System.out.println(ext.get(i).toString());
			else
				System.out.println("trying stuff");
			//ext.get(i);
		}
		
		return null;
		
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
	
	
	public String getModesFile(String file)
	{
		
		String line="";
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
////			System.out.println("pass 3 - "+jArr.get(0).getClass());
//			JSONObject obj1 = jArr.getJSONObject(0);
//			
//			System.out.println("Object : "+obj1);
//			//System.out.println("Object : "+object.getJSONArray("institution_ir").getJSONObject(0).getJSONArray("noninertial_fluents"));
//		//	
//			
//			JSONObject obj2 = obj1.getJSONObject("contents");
//			System.out.println("pass 2 ");
//			
//			JSONObject obj3 = obj2.getJSONObject("noninertial_fluents");
//			
//			System.out.println("pass 3");
//			
			JSONObject niFluents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("noninertial_fluents");
			JSONObject fluents = object.getJSONArray("institution_ir").getJSONObject(0).getJSONObject("contents").getJSONObject("fluents");
//			
			;
//			System.out.println("pass 4");
		
//			
			//	WORK OUT HOW TO PUT THIS IN A FUNCTION TO PASS THE APPROPRIATE JSONOBJECT 
			// AND GET BACK ALL THE STRINGS MH,MB,EP
			// PROBABLY GRAB ONE STRING AND THEN ADD THE MODEH OR WHATEVER AFTER SINCE ITS ESSENTIALLY THE SAME STRING
		
			StringBuilder modeh=new StringBuilder();
			StringBuilder modeb=new StringBuilder();
			StringBuilder examplepattern=new StringBuilder();
			
			//for (String key : niFluents.keySet()) 
			for (String key : fluents.keySet()) 
			{
				//System.out.println(key +" : "+ niFluents.get(key)+" - "+ niFluents.get(key).getClass());
				modeh.append("modeh(holdsat("+key+"(");
				examplepattern.append("examplePattern((holdsat("+key+"(");
				//JSONArray jArr = (JSONArray)niFluents.get(key);
				JSONArray jArr = (JSONArray)fluents.get(key);
				for(int i=0;i<jArr.length();i++)
		    	{
					//System.out.println(jArr.get(i)+" "+jArr.get(i).getClass());
		    		modeh.append("+"+((String)jArr.get(i)).toLowerCase());
		    		examplepattern.append("+"+((String)jArr.get(i)).toLowerCase());
		    		if(i!=(jArr.length()-1))
		    		{
		    			modeh.append(",");
		    			examplepattern.append(",");
		    		}
		    	}
				modeh.append("),+inst,+event,+instant))\n");	
				examplepattern.append("),+inst,+event,+instant))\n");

			}
			System.out.println("modeh:\n "+modeh.toString());
//			Object obj = (Object)state.get(request);
//			if (obj instanceof JSONArray)
//			{
//				JSONArray ob = (JSONArray)obj;
//				
//				for(int i=0;i<ob.length();i++)
//				{
//					StringBuilder sbb = new StringBuilder("");
//					String str = jsonExtractor_prev.extract(ob.get(i),sbb).toString();
//					str = jsonExtractor_prev.parseStr(str,flag);
//					str = str.replaceFirst("\\(","");
//					str =str+")";
//					strRet.append(str+"\n");
//					
//					//System.out.println(request+": "+jsonExtractor_prev.parseStr(str,flag)+"\n");
//					//System.out.println(request+": "+str+"\n");
//				}
//			}
//			else if(obj instanceof JSONObject)
//			{
//				JSONObject ob = (JSONObject)state.get("holdsat");
//				JSONArray ar = jsonExtractor_prev.extractHoldsat(ob);
//				for(int i=0;i<ar.length();i++)
//				{
//					
//					JSONArray arr = (JSONArray)ar.get(i);
//					for(int j=0;j<arr.length();j++)
//					{
//						StringBuilder sbb = new StringBuilder("");
//
//						//System.out.println(jsonExtractor_prev.extract(arr.get(j),sbb).toString());
//						String str = jsonExtractor_prev.extract(arr.get(j),sbb).toString();
//						strRet.append(jsonExtractor_prev.parseStr(str,flag)+"\n");
//						
//						//System.out.println(jsonExtractor_prev.parseStr(str,flag));
//						
//					}
//					
//					
//				}
//				if (flag==0)
//				{
//					Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), strRet.toString().getBytes());
//					//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts1.txt"), strRet.toString().getBytes());
//					facts_store.add(strRet.toString());
//				}
				
//			}
//			else
//				System.out.println("ERROR - no JSON in file");
//		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error with json modes");
			e1.printStackTrace();
		}
		return "modes created";
	}
	
    
}
