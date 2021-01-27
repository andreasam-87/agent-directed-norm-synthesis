import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;

import instal.InstalModel;
import instal.InstalQuery;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;


public class RoomEnvironmentLocal_Inst extends StepSynchedEnvironment {
	
	JsonExtractor jsonExtractor_prev = new JsonExtractor();
	JsonExtractorOrgSimple jsonExtractor = new JsonExtractorOrgSimple();
	
	public void init(String[] args) {
	       // super.init(args);
	        super.init(new String[]{"1000"});
	        //super.setSleep(100);
	        
//	        institutions = new InstalModel[] { room_inst };
//	        this.model = room_inst;
	        initialiseInstitution();
//	        if (this.grounding == null) {
//	    		this.grounding = toGrounding();
//	    		}

	        
	    }
	    
	
//	
//    @Override
//    public boolean executeAction(String agName, Structure action) {
//
//    	System.out.print(agName+ " ");
//    	String ex = action.toString();
//		if (action.getFunctor().equals("enter")) {
//			//String name = (String) ((StringTerm) act.getTerm(0)).toString();
//			//String room = (String) ((StringTerm) act.getTerm(1)).toString(); 
//			current_action = ex.replace("\"","");
//			ex = ex.replace("\"","");
//			//current_action = current_action.replaceFirst("ent","enter");
//			//System.out.println("The action is "+current_action);
//			for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitution(agName).entrySet())
//	    	{
//	    		for (int i = 0; i < entry.getValue().length; i++)
//	    		{
//	    			addPercept(entry.getKey(), entry.getValue()[i]);
//	    			//addPercept(entry.getValue()[i]);
//	    			//System.out.println("Percept"+i+ " : "+ entry.getValue()[i]);
//	    		
//	    		}
//	    	}
//
//			
//		}
//		
//		else if (action.getFunctor().equals("leave")) { 
//			current_action = ex.replace("\"","");
//			//String ex = act.toString();
//			//for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitution().entrySet())
//			for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitution(agName).entrySet())
//	    	{
//	    		for (int i = 0; i < entry.getValue().length; i++)
//	    		{
//	    			addPercept(entry.getKey(), entry.getValue()[i]);
//	    			//addPercept(entry.getValue()[i]);
//	    			//System.out.println("Percept 2: "+ entry.getValue()[i]);
//	    		}
//	    	}
//
//		}
//
//		
//		
//		else
//		{
//			System.out.println("Dunno what action was executed");
//			//inst_state--;
//		}
//		
//		
//		//inst_state++;
//        return true; // the action was executed with success
//    }
//    
//    
//    
//    public Map<String, Literal[]> perceptsFromInstitutionLocal(String ag) {
// 		Map<String, Literal[]> m = new HashMap<String, Literal[]>();
//    
//	    try {
//	    	//how to call local instal without files or how to get files from these variables and placed in the appropriate place
//	    	
//			String cmd = "/usr/local/bin/docker run -v /Users/andreasamartin/Documents/InstalExamples/rooms:/workdir instal-stable solve $* -i /workdir/rooms.ial -f /workdir/room-conflict.iaf -d /workdir/rooms.idc -q /workdir/rooms-blank.iaq -j /workdir/out.json -v";
//			//Processes.runShellCmd(cmd);
//			
//			String output = Processes.runShellCmdRes(cmd);
//			
//			System.out.println("Output \n" + output ); 
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			 
//				System.out.println("Err"); 
//			
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			
//				System.out.println("Errr 1" ); 
//			
//			e.printStackTrace();
//		}
//	    return m;
// 	}
//    
//    
//    public Map<String, Literal[]> perceptsFromInstitution(String ag) {
// 		Map<String, Literal[]> m = new HashMap<String, Literal[]>();
//
// 		int state = whichState();
// 		//System.out.println("state is: "+state);
// 		
// 		//ArrayList<String> facts = getFacts(state);
// 		ArrayList<String> facts = new ArrayList<String>();
// 		//ArrayList<String> query = getQuery(state);
// 		ArrayList<String> query = new ArrayList<String>();
// 		facts.addAll(facts_store.get(state));
// 		
// 		//System.out.println("facts: "+facts_store.get(state));
// 		
// 		//String ag = agent;
// 		
// 			query.add(current_action);
// 			
// 			InstalQuery q = this.grounding.newQuery(facts.toArray(new String[facts.size()]), query.toArray(new String[query.size()]));
// 			JSONObject queryOutput = q.getQueryOutput();
// 			
//
// 			JSONArray occurred = queryOutput.getJSONArray("json_out").getJSONArray(0).getJSONObject(1).getJSONObject("state").getJSONArray("occurred");
// 	    	boolean success = false;
// 	    	boolean success2 = false;
// 	    	boolean success3 = false;
// 	    	for (int i = 0; i < occurred.length(); i++) {
// 	    		if (occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0).equals("arrive")) {
// 	    			success = true;
// 	    		}
// 	    		if (occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0).equals("exit")) {
// 	    			success2 = true;
// 	    		}
// 	    		if (occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0).equals("capacityExceeded")) {
// 	    			//System.out.println("this is here");
// 	    			success3 = true;
// 	    		}
//// 	    		if (occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0).equals("setpermenteri")) {
//// 	    			success3 = true;
//// 	    		}
// 	    		//System.out.println("Occurred: " + occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0));
// 	    	}
// 	    	//System.out.println("Success 1,2,3 " +  success + success2 + success3);
// 	    	
// 	    /*	if(current_action.equals("enter("+ag+",room1)"))
// 	    	{
// 	    		if (success) {
// 	        		m.put(ag,new Literal[] {Literal.parseLiteral("perm(leave)")});
// 	        		inroom = true;
// 	        		//System.out.println(("I can now wave"));
// 	        		
// 	        	} else {
// 	        		m.put(ag, new Literal[] {Literal.parseLiteral("prob(enter)")});
// 	        	}
// 	    	}*/
// 	    	if(current_action.equals("enter("+ag+",room1)") || current_action.equals("enter("+ag+",room2)"))
// 	    	{
// 	    		if (success) {
// 	        		m.put(ag,new Literal[] {Literal.parseLiteral("perm(leave)")});
// 	        		//inroom = true;
// 	        		//System.out.println(("I can now wave"));
// 	        		if(success3)
// 	        		{
// 	        			m.put(ag,new Literal[] {Literal.parseLiteral("perm(leave)"), Literal.parseLiteral("roomCapacityExceeded")});
// 	        		}
// 	        	} else {
// 	        		m.put(ag, new Literal[] {Literal.parseLiteral("prob(enter)")});
// 	        	}
// 	    	}
// 	    	else if (current_action.equals("leave("+ag+",room1)") || current_action.equals("leave("+ag+",room2)"))
// 	    	{
// 	    		
// 	    		if (success2) {
// 	        		m.put(ag,new Literal[] {Literal.parseLiteral("perm(idle)")});
// 	        		//inroom = true;
// 	        		//System.out.println(("I just waved can now leave"));
// 	        		if(success3)
// 	        		{
// 	        			m.put(ag,new Literal[] {Literal.parseLiteral("perm(leave)"), Literal.parseLiteral("roomCapacityExceeded")});
// 	        		}
// 	        		
// 	        	} else {
// 	        		m.put(ag, new Literal[] {Literal.parseLiteral("prob(leave)")});
// 	        		//System.out.println(("I cannot wave :( "));
// 	        	}
// 	    	}
//// 	    	else if (current_action.equals("setpermenter("+ag+")"))
//// 	    	{
//// 	    		System.out.println("hey..... "+ success3);
//// 	    		if (success3) {
//// 	        		m.put(ag,new Literal[] {Literal.parseLiteral("perm(enterroom)")});
//// 	        		//inroom = true;
//// 	        		//System.out.println(("I just waved can now leave"));
//// 	        		
//// 	        	} else {
//// 	        		m.put(ag, new Literal[] {Literal.parseLiteral("prob(enterroom)")});
//// 	        		//System.out.println(("I cannot wave :( "));
//// 	        	}
//// 	    	}
// 	    	else
// 	    	{
// 	    		System.out.println(current_action);
// 	    		System.out.println(("Error"));
// 	    	}
//
// 	    	
// 	    	
// 	    	JSONObject holdsat = queryOutput.getJSONArray("json_out").getJSONArray(0).getJSONObject(1).getJSONObject("state").getJSONObject("holdsat");
// 	    		//	getJSONObject("holdsat");
// 	    	//System.out.println(holdsat);
// 	    	//System.out.println(holdsat.keySet());
// 	    	
// 	    	
// 	    	facts_store.add(jsonExtractor.extractHoldsatJson(holdsat));
// 	    	//extractJson(holdsat);
// 	    	
// 	    	
// 	    	ArrayList<String> tempValues = jsonExtractor.extractHoldsatJson(holdsat);
// 	    	String add = "";	
// 	//    	Literal[] inst_sense = new Literal[15];
// 	    	int c=0;
// 	    //	int noOfOccurs = Collections.frequency(tempValues, ag);
// 	    	int count = StringUtils.countMatches(tempValues.toString(), ag);
// 	    	//System.out.println("Occurences: "+ noOfOccurs+ " "+count);
// 	    	Literal[] inst_sense = new Literal[count+1];
// 	    	for (String var : tempValues) 
// 	    	{ 
// 	    		if (var.contains(ag))
// 	    		{
// 	    			//strip initiallys and rooms from the string.
// 	    			var = var.substring(10, var.length());
// 	    			var = var.substring(0, var.length()-7);
// 	    			inst_sense[c] = Literal.parseLiteral(var);
// 	    			c++;
// 	    			//System.out.println(var);
// 	    		}
// 	    	    //add+=var;
// 	    	}
// 	    	if (success3)
// 	    	{
// 	    		inst_sense[c] = Literal.parseLiteral("roomCapacityExceeded");
// 	    	}else
// 	    	{
// 	    		inst_sense[c] = Literal.parseLiteral("roomCapacityOkay");
// 	    	}
// 	    	
// 	    	m.put(ag,inst_sense);
// 		
// 		//System.out.println("Agents: "+getNamesAgents());
// 		
// 		
//     	//Storing the state
// 	    stateList.put(inst_state, new State(occurred,holdsat));
// 	    	
// 	    	
//     	return m;
// 	}
    
    
    
    
	
	protected void initialiseInstitution() {
		System.out.println(("Initialising institution..."));
		

		try {
			String cmd = "/usr/local/bin/docker run -v /Users/andreasamartin/Documents/InstalExamples/rooms:/workdir instal-stable solve $* -i /workdir/rooms.ial -f /workdir/room-conflict.iaf -d /workdir/rooms.idc -q /workdir/rooms-blank.iaq -j /workdir/out.json -v";
			//Processes.runShellCmd(cmd);
			
			String output = Processes.runShellCmdRes(cmd);
			
			//JSONObject respObj = getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json");
			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json");
			
			
//			JSONArray occurred = respObj.getJSONArray("json_out").getJSONArray(0).getJSONObject(1).getJSONObject("state").getJSONArray("occurred");
// 	    	
// 	    	for (int i = 0; i < occurred.length(); i++) {
// 	    		
// 	    		System.out.println("Occurred: " + occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0));
// 	    	}
//			
			
			
			//System.out.println("Output \n" + output ); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 
				System.out.println("Err"); 
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			
				System.out.println("Errr 1" ); 
			
			e.printStackTrace();
		}
		
		
//		ArrayList<String> facts = new ArrayList<String>();
//		int count=0;
//		for (String ag : getNamesAgents()) {
//			facts.add("initially(perm(enter("+ag+",room1)), rooms)");
//			facts.add("initially(perm(arrive("+ag+",room1)), rooms)");
//			facts.add("initially(pow(enter("+ag+",room1)), rooms)");
//			facts.add("initially(perm(enter("+ag+",room2)), rooms)");
//			facts.add("initially(perm(arrive("+ag+",room2)), rooms)");
//			facts.add("initially(pow(enter("+ag+",room2)), rooms)");
//			if(count%2==0)
//			{
//				facts.add("initially(role("+ag+", x), rooms)");
//				addPercept(ag,Literal.parseLiteral("role(x)"));
//			}else
//			{
//				facts.add("initially(role("+ag+", y), rooms)");
//				addPercept(ag,Literal.parseLiteral("role(y)"));
//			}
//			count++;
//			
//		}
//		
//		
//		facts.add("initially(occupancy(room1,0),rooms)");
//		facts.add("initially(occupancy(room2,0),rooms)");
//		facts.add("initially(count_type(room2,y,0),rooms)");
//		facts.add("initially(count_type(room2,x,0),rooms)");
//		facts.add("initially(count_type(room1,y,0),rooms)");
//		facts.add("initially(count_type(room1,x,0),rooms)");
//		
//		facts.add("initially(max(room1,3),rooms)");
//		facts.add("initially(max(room2,3),rooms)");
////		facts.add("initially(addone(0,1),rooms)");
////		facts.add("initially(addone(1,2),rooms)");
////		facts.add("initially(addone(2,3),rooms)");
////		facts.add("initially(addone(2,3),rooms)");
//		
//		facts_store.add(facts);
//		
//    	addPercept(Literal.parseLiteral("perm(enter)"));
    	System.out.println(("Initialisation complete..."));
    	
	}
	
	public void getJSONObjectFromFile(String file)
	{
		try {
			JSONTokener token = new JSONTokener(new FileReader(file));
			 //JSONArray object = (JSONArray) token.nextValue();
			 JSONArray object = new JSONArray (token);
		//	System.out.println("Got: "+object + " \nclass "+ object.getClass());
			JSONObject occurred = (JSONObject)object.get(1); //.getJSONArray("json_out").getJSONArray(0).getJSONObject(1).getJSONObject("state").getJSONArray("occurred");
			System.out.println("Got: "+occurred.getJSONObject("state").getJSONArray("occurred") ); 
			
			//System.out.println("Got: "+occurred + " \nclass "+ occurred.getClass());
	//		while(!token.end())
//			{
//				JSONArray object = (JSONArray) token.nextValue();
//				System.out.println("Got: "+object + " \nclass "+ object.getClass());
//			}
		//	while(token.more()) {
			    //System.out.println(token.next());
//			    JSONArray object = (JSONArray) token.nextValue();
				//System.out.println("Got: "+object + " \nclass "+ object.getClass());
	//		}
			System.out.println("Done ");
			
			
			JSONObject state = (JSONObject) occurred.get("state");
			
			//jsonExtractor_prev.loopThroughJson(state);
			
			Object obj = (Object)state.get("holdsat");
			if (obj instanceof JSONArray)
			{
				JSONArray ob = (JSONArray)obj;
				
				for(int i=0;i<ob.length();i++)
				{
					StringBuilder sbb = new StringBuilder("");
					System.out.println(i + jsonExtractor_prev.extract(ob.get(i),sbb).toString());
					;
					//ext.get(i);
				}
			}
			else if(obj instanceof JSONObject)
			{
				JSONObject ob = (JSONObject)state.get("holdsat");
				JSONArray ar = jsonExtractor_prev.extractHoldsat(ob);
				//StringBuilder sbb = new StringBuilder("");
				System.out.println(ar);
				for(int i=0;i<ar.length();i++)
				{
					
					JSONArray arr = (JSONArray)ar.get(i);
					for(int j=0;j<arr.length();j++)
					{
						StringBuilder sbb = new StringBuilder("");
						System.out.println(j + jsonExtractor_prev.extract(arr.get(j),sbb).toString());
						
					}
					
				
				}
				//System.out.println(jsonExtractor_prev.extract(ar,sbb));
//				for (String key : ob.keySet()) 
//		    	{
//					StringBuilder sbb = new StringBuilder("");
//					//System.out.println(ob.get(key).getClass());
//					JSONArray ar = jsonExtractor_prev.extractHoldsat(ob.get(key));
//					//JSONArray ar = jsonExtractor_prev.extract(ar,sbb);
//					//
//					//System.out.println(key+ " "+ jsonExtractor_prev.extract((JSONArray)ob.get(key),sbb));
//					;
//		    	}
				
			}
			else
				System.out.println("ERROR");
			

			//System.out.println(ob.getClass());
			
			//for (Object o : )
			//jsonExtractor_prev.analysisJson(state.get("occurred"));
			//jsonExtractor_prev.analysisJson(state.get("holdsat"));
			
			//System.out.println(state.get("holdsat")+ " \n "+state.get("holdsat").getClass() );
			ArrayList<String> dataList = jsonExtractor_prev.extractHoldsatJson((JSONObject)state.get("holdsat"));
		//			data = JsonExtractor.extractHoldsatJson((JSONObject)state.get("holdsat"));
			//System.out.println(dataList);
			for (String data : dataList) 
	    	{ 
				//strip initiallys and rooms from the string.
    			data = data.substring(10, data.length());
    			data = data.substring(0, data.length()-1);
    			int index = data.lastIndexOf(")");
    			//System.out.println(index+"  "+ data.charAt(index+1));
    			//data = data.substring(0,(index+1));

    			//System.out.println(index+"  "+ data.charAt(index+1));
    			
	    		
				//System.out.println(data);
	    		
	    	}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		JSONParser parser = new JSONParser();
//		try {
//			Object obj = parser.parse(new FileReader(file));
//					
//			// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
//		//	JSONObject jsonObject = (JSONObject) obj;
//			org.json.simple.JSONArray jArr = (org.json.simple.JSONArray) obj;
//			//JSONArray jArr = (JSONArray) obj;
//
//			for (int i = 0; i < jArr.size(); i++) {
//			//	System.out.println(jArr.get(i).getClass());
//				org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jArr.get(i);
//				
//					org.json.simple.JSONObject state = (org.json.simple.JSONObject) jsonObject.get("state");
//								//System.out.println(state.get("holdsat")+ " \n "+state.get("holdsat").getClass() );
//				ArrayList<String> dataList = jsonExtractor.extractHoldsatJson((org.json.simple.JSONObject)state.get("holdsat"));
//			//			data = JsonExtractor.extractHoldsatJson((JSONObject)state.get("holdsat"));
//				
//				for (String data : dataList) 
//		    	{ 
//					//strip initiallys and rooms from the string.
//	    			data = data.substring(10, data.length());
//	    			//data = data.substring(0, data.length()-7);
//	    		
//			//		System.out.println(data);
//		    		
//		    	}
//				
//			}
//
//		} catch (Exception e) {
//			System.out.println("Error from the inception");
//			e.printStackTrace();
//		}

	}
	
    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
  

}
