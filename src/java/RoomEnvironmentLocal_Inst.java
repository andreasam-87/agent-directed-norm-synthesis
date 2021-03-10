import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.JSONParser;

import instal.InstalModel;
import instal.InstalQuery;
import jason.NoValueException;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;


public class RoomEnvironmentLocal_Inst extends StepSynchedEnvironment {

	JsonExtractor jsonExtractor_prev = new JsonExtractor("rooms");
	//JsonExtractorOrgSimple jsonExtractor = new JsonExtractorOrgSimple();

	int inst_state=0; //to keep track of the institutional state at any timestep since different from env timesteps
	String current_action =""; //keep track of the current action being executed by any agent

	StringBuilder strRet = new StringBuilder(); //to build string after parsing json return object from request
	ArrayList<String> facts_store = new ArrayList<String>(); //collect of holdsat/facts/fluents true

	HashMap <Integer,State> stateList = new HashMap<>(); //collection of states of the inst

	HashMap <Long,Boolean> completed_infinites  = new HashMap<>();  //collection of completed infinites for action that can take forever

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

	/*Include Step finished function and work to add percept to agent in the room only */
	/* @Override
		protected void stepFinished(int step, long elapsedTime, boolean byTimeout) {
			 
			 //State now = stateList.get(inst_state);
			 

			// String array size number of rooms
			 String [] noRooms = new String[2];
			 
			//figure out how to get the inst_state and how to navigate the array
			 
			 if(inst_state!=0)
			{
				// previously ArrayList<String> factsnow = facts_store.get(inst_state);
				String[]  factsnow = facts_store.get(inst_state-1).split("\n"); 
				 
			
				// System.out.println("State:" +inst_state+ " facts: "+ factsnow);
				 int cRooms=0;
				 for(String s: factsnow)
				 {
					 if(s.contains("capacityExceeded"))
					 {
						 s = s.substring(10, s.length());
			    		s = s.substring(0, s.length()-7);
						s = StringUtils.substringBetween(s, "(", ")");
						
						noRooms[cRooms]=s;
						cRooms++;
						 System.out.println("Room with exceeded capacity is " + s);
						 
					 }
				 }
				 
				 for(String s: factsnow)
				 {
					 if(s.contains("in_room"))
					 {
						 s = s.substring(10, s.length());
			    		s = s.substring(0, s.length()-7);
						String s1 = StringUtils.substringBetween(s, "(", ",");
						String s2 = StringUtils.substringBetween(s, ",", ")");
						if ((s2.equals(noRooms[0])) || (s2.equals(noRooms[1])))
						{
							///<<<<This gets added after so may cause some trouble, look into it>>>>>
							addPercept(s1, Literal.parseLiteral("roomCapacityExceeded"));
							 System.out.println("Agents affected " + s1);
						}
						
						 
					 }
				 }		 
			}
			
		
		}
	*/

	/** to be overridden by user class **/


	@Override
	protected boolean isOpenEnded (String agName, Structure action) {
		return action.getFunctor().equals("delay") || action.getFunctor().equals("revise");
	}

	@Override
	protected  boolean isComposite (String agName , Structure action) {
		return action.getFunctor().equals("skip_steps");
	}

	@Override
	protected ArrayList <Structure> getComposites (String agName, Structure action) {
		ArrayList<Structure> composites =  new ArrayList<Structure>();
		try{

			final int stepsToSkip = (int) ((NumberTerm) action.getTerm(0)).solve();
			for (int i=0; i < stepsToSkip; i++) {
				Structure newAction =  (Structure) action.clone();
				newAction.setTerm(0, new NumberTermImpl(1));
				composites.add(newAction);
			}

		} catch (Exception ex){ }
		return composites;
	}


	@Override
	public boolean executeAction(String agName, Structure action) {

		System.out.print(agName+ " ");
		String ex = action.toString();
		if (action.getFunctor().equals("enter")) {
			//String name = (String) ((StringTerm) act.getTerm(0)).toString();
			//String room = (String) ((StringTerm) act.getTerm(1)).toString();
			current_action = ex.replace("\"","");
			ex = ex.replace("\"","");
			//current_action = current_action.replaceFirst("ent","enter");
			current_action = "observed("+current_action+")";
			//System.out.println("The action is "+current_action);


			clearPercepts(agName); //remove old percepts and add new percepts

			for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitutionLocal(agName).entrySet())
			{
				for (int i = 0; i < entry.getValue().length; i++)
				{
					addPercept(entry.getKey(), entry.getValue()[i]);
					//System.out.println("adding percept");
					//addPercept(entry.getValue()[i]);
					//	System.out.println("Percept"+i+ " : "+ entry.getValue()[i]);

				}
			}
			updateAgsPercept();

		}

		else if (action.getFunctor().equals("leave")) {
			current_action = ex.replace("\"","");
			current_action = "observed("+current_action+")";
			//String ex = act.toString();

			clearPercepts(agName); //remove old percepts and add new percepts

			//	public static final Literal check  = Literal.parseLiteral("check");
			//for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitution().entrySet())
			for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitutionLocal(agName).entrySet())
			{
				for (int i = 0; i < entry.getValue().length; i++)
				{
					//if(entry.getValue()[i].equals(Literal.parseLiteral(" ")))
					addPercept(entry.getKey(), entry.getValue()[i]);
					//addPercept(entry.getValue()[i]);
					//System.out.println("Percept 2: "+ entry.getValue()[i]);
				}
			}

		}
		else if (action.getFunctor().equals("checkState")) {
			//String eventOccurred = (String) ((StringTerm) act.getTerm(0)).toString();
			//String eventOccurred = (String) ((StringTerm) act.getTerm(0)).getString();
			//	.toString();
			//String eventOccurred =(String)(StringTerm) act.getTerm(0)).getString();
			//int x=0;
			//System.out.println("reached here");
			try {
				//x = (int) ((NumberTerm) act.getTerm(0)).solve();
				String eventOccurred = (action.getTerm(0)).toString();
				eventOccurred = eventOccurred.replace("\"","");
				System.out.println("Checking state for: "+eventOccurred);
				int whenOcc = jsonExtractor_prev.checkStateForEvent(eventOccurred,stateList);
				System.out.println("Event occurred in state: "+whenOcc);
				//String data = jsonExtractor_prev.getStateFactsandEvents(whenOcc-1,stateList);
				//System.out.println("Data from that state- "+data);

				//addPercept(agName, Literal.parseLiteral("datafromagents"));
				//String d = "hellohello(howare,you),good";
				//String d = data.substring(0,40).toString().split("\\),")[0]+")";

				//	String d = "stateOccurred("+eventOccurred+","+whenOcc+")";
				//System.out.println("D - "+d);
				addPercept(agName, Literal.parseLiteral("eventOccurred("+whenOcc+")"));
				//addPercept(agName, Literal.parseLiteral(data.substring(0,50)));
				//	addPercept(agName, Literal.parseLiteral(d));

				//System.out.println("Data - "+data);

				//probably should just do something that puts the facts of the previous
				//state back in to the new slot
				inst_state--;
				//System.out.println("reached here too");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error here ");
			}
			//System.out.println("Checking state for: "+eventOccurred);
			//System.out.println("Checking state for: "+x);
			//action = true;
			//inst_state++;
			//stateList.put(inst_state, new State(occurred,holdsat));
		}
		else if (action.getFunctor().equals("revise")) {

//			try {
//				} catch (NoValueException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						int when = (int)((NumberTerm) action.getTerm(0)).solve();
						int numStates = (int)((NumberTerm) action.getTerm(1)).solve();
						System.out.println("Check state "+when+ " and "+ numStates +" states before and after.");

						System.out.println("Revision begins...... (2 seconds)");

						String modes = jsonExtractor_prev.getModesFile("/Users/andreasamartin/Documents/InstalExamples/rooms/dict.txt");

					//	System.out.println(modes); //just printing for now
						System.out.println("Modes file created");
						//writing to the file is what is required so that the ILP can access this file
						Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/modes"), modes.getBytes());

						Thread.sleep(2000);
						System.out.println("Revision has ended.... Completing action ......");

						RoomEnvironmentLocal_Inst.this.markAsCompleted(action);
					}catch (Exception ex) {

					}
				}
			};

			Thread t = new Thread(r);
			t.start();

			addPercept(agName, Literal.parseLiteral("revisionFailed"));
			inst_state--;
			// return true; //do I need a separate return here or the one to the bottom will do.
		}
		else if (action.getFunctor().equals("delay")) {

			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						int delay = (int)((NumberTerm) action.getTerm(0)).solve();
						System.out.println("Waiting begins...... ("+(delay/1000)+" seconds)");
						Thread.sleep(delay);
						System.out.println("Waiting has ended.... Completing action ......");
						//      RoverWorld.this.markAsCompleted(action);
						RoomEnvironmentLocal_Inst.this.markAsCompleted(action);
					}catch (Exception ex) {

					}
				}
			};

			Thread t = new Thread(r);
			t.start();
			//return true;
			inst_state--;
		}
		else if (action.getFunctor().equals("skip_steps")) {
			inst_state--;
			return true;
		}

		else
		{
			System.out.println("Dunno what action was executed");
			return false;
			//do I need a return false in here??
			//inst_state--;
		}


		inst_state++;
		return true; // the action was executed with success
	}



	//
	public Map<String, Literal[]> perceptsFromInstitutionLocal(String ag) {
		Map<String, Literal[]> m = new HashMap<String, Literal[]>();

		try {
			//how to call local instal without files or how to get files from these variables and placed in the appropriate place
			//	Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), strRet.toString().getBytes());
			Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsQuery.iaq"), current_action.getBytes());

//	    	System.out.println("Query file: ");
//	    	List<String> lines = Files.readAllLines(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsQuery.iaq"), Charset.defaultCharset());
//	    	for (String line : lines) {
//	    	System.out.println("line read: " + line);
//	    	
//	    	}
			String cmd = "/usr/local/bin/docker run -v /Users/andreasamartin/Documents/InstalExamples/rooms:/workdir instal-stable solve $* -i /workdir/rooms.ial -f /workdir/roomsFacts.iaf -d /workdir/roomsConf.idc -q /workdir/roomsQuery.iaq -j /workdir/out.json -v";
			//Processes.runShellCmd(cmd);

			String output = Processes.runShellCmdRes(cmd);
			//System.out.println("Command output: " + output);

//			System.out.println("Output file: ");
//	    	lines = Files.readAllLines(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json"), Charset.defaultCharset());
//	    	for (String line2 : lines) {
//	    	System.out.println("line read: " + line2);
//	    	}

			//what occurred this timestep
			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","occurred",1);
			String occurred = strRet.toString();
			//getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","observed",1);
			//	System.out.println("\nwhat occurred "+strRet.toString());

			//get the new facts sorted and saved to file for next run
			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","holdsat",0);

			//get feedback for the agents as percepts
			//System.out.println("\npotentially agent output "+strRet.toString());
			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","holdsat",1);

			//add to the collection of states after each agent's action
			stateList.put(inst_state, new State(occurred,facts_store.get(inst_state)));

			String[] percepts = strRet.toString().split("\n");

			//System.out.println("\npotentially agent output Begin \n");
			//System.out.println(percepts.toString());
			int count = StringUtils.countMatches(strRet.toString(), ag);
			//System.out.println("Count "+count);
			int c=0;
			boolean viol = false;
			Literal[] inst_sensors = new Literal[count+1]; //add +1 to array size if capacity percept being added
			String roomCap="";
			String inroom="";
			for (String var : percepts)
			{
				//System.out.println(var);
				if(var.contains("capacityExceeded"))
				{
					viol=true; //indicates a violation of the
					roomCap=var; //stores the entire percept, maybe just store the name by putting the line from below
				}
				if (var.contains(ag) && !var.matches("(.*)"+ag+"[0-9]+(.*)"))
				{
					//populates the percepts with the agent name to be sent to the agent
					inst_sensors[c] = Literal.parseLiteral(var);
					c++;

				}
				if (var.contains("in_room"))
					inroom=var; //keeps track of if the agent is in some room
			}

			//checking if the capacity exceeded fluent is set and sets agent percept
			//if (Arrays.asList(percepts).contains("capacityExceeded")) //doesn't work actually

			//check if agent is in the room first
			if (viol)
			{
				//System.out.println("true");
				roomCap=StringUtils.substringBetween(roomCap, "(", ")"); //find the name of the room which has exceeded capacity
				if(inroom.contains(roomCap)) //check if this agent is in the room
					inst_sensors[c] = Literal.parseLiteral("roomCapacityExceeded");
				else
					inst_sensors[c] = Literal.parseLiteral("roomCapacityOkay");
			}else
			{
				inst_sensors[c] = Literal.parseLiteral("roomCapacityOkay");
			}
//	    	//System.out.println(inst_sensors.length);
//	    	

			m.put(ag,inst_sensors);

			//System.out.println("potentially agent output END\n ");

		} catch (IOException e) {
			// TODO Auto-generated catch block

			System.out.println("Err");

			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block

			System.out.println("Errr 1" );

			e.printStackTrace();
		}
		return m;
	}
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


	public List<String> getNamesAgents() {
		List<String> names = new ArrayList<String>();
		try {
			jason.mas2j.MAS2JProject project = new jason.mas2j.MAS2JProject().parse("room_test.mas2j");

			for (jason.mas2j.AgentParameters ap : project.getAgents()) {
				String agName = ap.name;
				for (int cAg = 0; cAg < ap.getNbInstances(); cAg++) {
					String numberedAg = agName;
					if (ap.getNbInstances() > 1) {
						numberedAg += (cAg + 1);
					}
					names.add(numberedAg);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		return names;
	}


	protected void initialiseInstitution() {
		System.out.println(("Initialising institution..."));
		StringBuilder config = new StringBuilder();
		ArrayList<String> facts = new ArrayList<String>();
		int count=0;
		config.append("Person: ");
		strRet.append("\n");
		for (String ag : getNamesAgents()) {
			config.append(ag + " ");
			if(count%2==0)
			{
				//facts.add("initially(role("+ag+", x), rooms)");
				strRet.append("initially(role("+ag+", x), rooms)\n");
				addPercept(ag,Literal.parseLiteral("role(x)"));
			}else
			{
				//facts.add("initially(role("+ag+", y), rooms)");
				strRet.append("initially(role("+ag+", y), rooms)\n");
				addPercept(ag,Literal.parseLiteral("role(y)"));
			}
			count++;

		}
		config.append("\nRole: x y\n" +
				"Location: room1 room2\n" +
				"Number: 0 1 2 3 4 5 6 7 8 9 ");
		
		/* Structure of file to create
		 * Person: alice bob eve tony lily jem
Role: x y
Location: room1
Number: 0 1 2 3 4 5 6 7 8 9 */

		//strRet.append(jsonExtractor_prev.parseStr(str,flag)+"\n");

		try {
			Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsConf.idc"), config.toString().getBytes());
			Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), strRet.toString().getBytes());
			//	Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), strRet.toString().getBytes(),StandardOpenOption.APPEND);

			//	System..pause();

//			String cmd = "/usr/local/bin/docker run -v /Users/andreasamartin/Documents/InstalExamples/rooms:/workdir instal-stable solve $* -i /workdir/rooms.lp -f /workdir/roomsFacts.iaf -d /workdir/roomsConf.idc -q /workdir/rooms-blank.iaq -j /workdir/out.json -v";
//			//Processes.runShellCmd(cmd);
//			
//			String output = Processes.runShellCmdRes(cmd);
//			
//			//JSONObject respObj = getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json");
//			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","holdsat",0);
//			
//			
////			JSONArray occurred = respObj.getJSONArray("json_out").getJSONArray(0).getJSONObject(1).getJSONObject("state").getJSONArray("occurred");
//// 	    	
//// 	    	for (int i = 0; i < occurred.length(); i++) {
//// 	    		
//// 	    		System.out.println("Occurred: " + occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0));
//// 	    	}
////			
//			//what occurred this timestep
//			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","occurred",1);
//		//	System.out.println("\nwhat occurred "+strRet.toString());
//			
//			
			//System.out.println("Output \n" + output ); 

		} catch (IOException e) {
			// TODO Auto-generated catch block

			System.out.println("Err");

			e.printStackTrace();
		} /*catch (InterruptedException e) {
			// TODO Auto-generated catch block
			
				System.out.println("Errr 1" ); 
			
			e.printStackTrace();
		}*/




		System.out.println(("Initialisation complete..."));

	}

	public void getJSONObjectFromFile(String file,String request,int flag)
	{
		strRet=new StringBuilder();
		try {
			//JSONTokener token = new JSONTokener(new FileReader(file));
			JSONTokener token = new JSONTokener(new FileReader(file));
//			JSONObject object = new JSONObject(new JSONTokener(new FileReader(file)));
			JSONArray jArr = new JSONArray (token);
			JSONObject object = (JSONObject)jArr.get(1);
			//	System.out.println("Length: "+jArr.length());
			//	System.out.println("JArr: "+jArr.get(0));
			//	System.out.println("JArr: "+jArr.get(1));
			///reading JSON Begin
//			String jsonString;
//			final BufferedReader fileReader = new BufferedReader(new FileReader(file));
//            final StringBuilder jsonContent = new StringBuilder();
//            String line;
//            while ((line =  fileReader.readLine()) != null) {
//                jsonContent.append(line);
//            }
//            fileReader.close();
//            jsonString = jsonContent.toString();
//            jsonString=jsonString.replaceFirst("\\[", "");
//            jsonString=jsonString.substring(0,jsonString.length()-1);
//            final JSONObject object = new JSONObject(jsonString);
//			///reading JSON End



			//	System.out.println("Size "+object.length());

			JSONObject state = (JSONObject) object.get("state");

			Object obj = (Object)state.get(request);
			if (obj instanceof JSONArray)
			{
				JSONArray ob = (JSONArray)obj;

				for(int i=0;i<ob.length();i++)
				{
					StringBuilder sbb = new StringBuilder("");
					String str = jsonExtractor_prev.extract(ob.get(i),sbb).toString();
					str = jsonExtractor_prev.parseStr(str,flag);
					str = str.replaceFirst("\\(","");
					str =str+")";
					strRet.append(str+"\n");

					//System.out.println(request+": "+jsonExtractor_prev.parseStr(str,flag)+"\n");
					//System.out.println(request+": "+str+"\n");
				}
			}
			else if(obj instanceof JSONObject)
			{
				JSONObject ob = (JSONObject)state.get("holdsat");
				JSONArray ar = jsonExtractor_prev.extractHoldsat(ob);
				for(int i=0;i<ar.length();i++)
				{

					JSONArray arr = (JSONArray)ar.get(i);
					for(int j=0;j<arr.length();j++)
					{
						StringBuilder sbb = new StringBuilder("");

						//System.out.println(jsonExtractor_prev.extract(arr.get(j),sbb).toString());
						String str = jsonExtractor_prev.extract(arr.get(j),sbb).toString();
						strRet.append(jsonExtractor_prev.parseStr(str,flag)+"\n");

						//System.out.println(jsonExtractor_prev.parseStr(str,flag));

					}


				}
				if (flag==0)
				{
					Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), strRet.toString().getBytes());
					//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts1.txt"), strRet.toString().getBytes());
					facts_store.add(strRet.toString());
				}

			}
			else
				System.out.println("ERROR - no JSON in file");
			//	facts_store.add("strRet.toString()");
			//	Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), strRet.toString().getBytes());
			//	Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts1.txt"), strRet.toString().getBytes());


		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void getJSONObjectFromFileOld(String file)
	{
		try {
			JSONTokener token = new JSONTokener(new FileReader(file));
			//JSONArray object = (JSONArray) token.nextValue();
			JSONArray object = new JSONArray (token);
			//	System.out.println("Got: "+object + " \nclass "+ object.getClass());
			JSONObject occurred = (JSONObject)object.get(1); //.getJSONArray("json_out").getJSONArray(0).getJSONObject(1).getJSONObject("state").getJSONArray("occurred");
			//System.out.println("Got: "+occurred.getJSONObject("state").getJSONArray("occurred") ); 

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
					System.out.println("Occurred: "+jsonExtractor_prev.extract(ob.get(i),sbb).toString());
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
