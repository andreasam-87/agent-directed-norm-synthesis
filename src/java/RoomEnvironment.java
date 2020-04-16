// Environment code for project room_experiment
import instal.*;
import jason.asSyntax.*;
import jason.environment.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

import org.json.JSONArray;
import org.json.JSONObject;

import ExpUtility.instal.InstalQuery;

//import ExpUtility.instal.InstalGrounding;
//import ExpUtility.instal.InstalModel;

public class RoomEnvironment extends StepSynchedEnvironment {

    private Logger logger = Logger.getLogger("room_experiment."+RoomEnvironment.class.getName());

    InstalModel room_inst = new InstalModel(new String[] { getFileContents("roomsV2.ial")  }, new String[] {}, new String[] {});
    
    public InstalModel[] institutions = new InstalModel[] {};
    
    public static final Literal check  = Literal.parseLiteral("check");
    
 // Institutional information
 	public InstalModel model;
 	public InstalGrounding grounding;
    
// 	public RoomEnvironment() {
//		super();
//		institutions = new InstalModel[] { room_inst };
//	}
 	
 	int inst_state=0;
 	String current_action ="";
 	
 	ArrayList<ArrayList<String>> facts_store = new ArrayList<ArrayList<String>>();
 	HashMap <Integer,State> stateList = new HashMap<>();
 	
 	JsonExtractor jsonExtractor = new JsonExtractor();
 	
    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
       // super.init(args);
        super.init(new String[]{"1000"});
        //super.setSleep(100);
        
        institutions = new InstalModel[] { room_inst };
        this.model = room_inst;
        initialiseInstitution();
        if (this.grounding == null) {
    		this.grounding = toGrounding();
    		}
       // addPercept(ASSyntax.parseLiteral("percept(demo)"));
    }
    

    protected String getFileContents(String Path) 
	{
		try {
			//System.out.println("File contents: "+new String(Files.readAllBytes(Paths.get(Path)),StandardCharsets.UTF_8));
			//return Files.readAllBytes(Paths.get(Path)).toString();
			return new String(Files.readAllBytes(Paths.get(Path)),StandardCharsets.UTF_8);
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}

    
	public Collection<String> getAgentNames() {
		System.out.println(getEnvironmentInfraTier().getRuntimeServices().getAgentsNames());
		return getEnvironmentInfraTier().getRuntimeServices().getAgentsNames();
	}
	
	
	public List<String> getNamesAgents() {
		List<String> names = new ArrayList<String>();
		try {
			jason.mas2j.MAS2JProject project = new jason.mas2j.MAS2JProject().parse("room_experiment.mas2j");
		    
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
	

	
    
    
    @Override
    public boolean executeAction(String agName, Structure action) {
//        logger.info("executing: "+action+", but not implemented!");
//        if (true) { // you may improve this condition
//             informAgsEnvironmentChanged();
//        }
    	System.out.print(agName+ " ");
    	String ex = action.toString();
		if (action.getFunctor().equals("enter")) {
			//String name = (String) ((StringTerm) act.getTerm(0)).toString();
			//String room = (String) ((StringTerm) act.getTerm(1)).toString(); 
			current_action = ex.replace("\"","");
			ex = ex.replace("\"","");
			//current_action = current_action.replaceFirst("ent","enter");
			//System.out.println("The action is "+current_action);
			for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitution(agName).entrySet())
	    	{
	    		for (int i = 0; i < entry.getValue().length; i++)
	    		{
	    			addPercept(entry.getKey(), entry.getValue()[i]);
	    			//addPercept(entry.getValue()[i]);
	    			//System.out.println("Percept"+i+ " : "+ entry.getValue()[i]);
	    		
	    		}
	    	}
			
			//System.out.println("The action is "+ex);
		
			//action = true;
			
		}
		
		else if (action.getFunctor().equals("leave")) { 
			current_action = ex.replace("\"","");
			//String ex = act.toString();
			//for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitution().entrySet())
			for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitution(agName).entrySet())
	    	{
	    		for (int i = 0; i < entry.getValue().length; i++)
	    		{
	    			addPercept(entry.getKey(), entry.getValue()[i]);
	    			//addPercept(entry.getValue()[i]);
	    			//System.out.println("Percept 2: "+ entry.getValue()[i]);
	    		}
	    	}
 
			//System.out.println("hello: inroom is " + inroom);
			//inroom = true;
			//action = true;
			//inst_state++;
		}
		else if(action.equals(check))
		{
			System.out.println("Problem found");
			//addPercept(ag, Literal.parseLiteral("Checking A"));
		//	action = true;
			//inst_state++;
		}
//		else if (act.equals(enter)) {
//			System.out.println("Problem found");
//		}
//		else if (act.equals(setpermenter)) { 
//			//String ex = act.toString();
//			for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitution(ag).entrySet())
//	    	{
//	    		for (int i = 0; i < entry.getValue().length; i++)
//	    		{
//	    			addPercept(entry.getValue()[i]);
//	    			//System.out.println("Percept 2: "+ entry.getValue()[i]);
//	    		}
//	    	}
// 
//			//System.out.println("Requested to enter");
//			//inroom = true;
//			action = true;
//			inst_state++;
////		}
		
		else if (action.getFunctor().equals("checkState")) {
			//String eventOccurred = (String) ((StringTerm) act.getTerm(0)).toString();
			//String eventOccurred = (String) ((StringTerm) act.getTerm(0)).getString();
				//	.toString();
			//String eventOccurred =(String)(StringTerm) act.getTerm(0)).getString();
			//int x=0;
			try {
				//x = (int) ((NumberTerm) act.getTerm(0)).solve();
				String eventOccurred = (action.getTerm(0)).toString();
				eventOccurred = eventOccurred.replace("\"","");
				System.out.println("Checking state for: "+eventOccurred);
				int whenOcc = jsonExtractor.checkStateForEvent(eventOccurred,stateList);
				System.out.println("Event occurred in state: "+whenOcc);
				String data = jsonExtractor.getStateFacts(whenOcc-1,stateList);
				addPercept(agName, Literal.parseLiteral(data));
				System.out.println("Data - "+data);
				
				//probably should just do something that puts the facts of the previous 
				//state back in to the new slot 
				inst_state--;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("Checking state for: "+eventOccurred);
			//System.out.println("Checking state for: "+x);
			//action = true;
			//inst_state++;
			 //stateList.put(inst_state, new State(occurred,holdsat));
		}
		else
		{
			System.out.println("Dunno what action was executed");
		}
		
		
		inst_state++;
        return true; // the action was executed with success
    }
    


    
    
    public Map<String, Literal[]> perceptsFromInstitution(String ag) {
		Map<String, Literal[]> m = new HashMap<String, Literal[]>();

		int state = whichState();
		//System.out.println("state is: "+state);
		
		//ArrayList<String> facts = getFacts(state);
		ArrayList<String> facts = new ArrayList<String>();
		//ArrayList<String> query = getQuery(state);
		ArrayList<String> query = new ArrayList<String>();
		facts.addAll(facts_store.get(state));
		
		//System.out.println("facts: "+facts_store.get(state));
		
		//String ag = agent;
		
			query.add(current_action);
			
			InstalQuery q = this.grounding.newQuery(facts.toArray(new String[facts.size()]), query.toArray(new String[query.size()]));
			JSONObject queryOutput = q.getQueryOutput();
			
			// #### State Keeping begins #### / 
//			JSONArray array_json = new JSONArray();
//
//			array_json.put(queryOutput);
			
			//institutionStates.add(array_json);
			// #### State Keeping ends #### / 
			
			JSONArray occurred = queryOutput.getJSONArray("json_out").getJSONArray(0).getJSONObject(1).getJSONObject("state").getJSONArray("occurred");
	    	boolean success = false;
	    	boolean success2 = false;
	    	boolean success3 = false;
	    	for (int i = 0; i < occurred.length(); i++) {
	    		if (occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0).equals("arrive")) {
	    			success = true;
	    		}
	    		if (occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0).equals("exit")) {
	    			success2 = true;
	    		}
//	    		if (occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0).equals("setpermenteri")) {
//	    			success3 = true;
//	    		}
	    		//System.out.println("Occurred: " + occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0));
	    	}
	    	//System.out.println("Success 1,2,3 " +  success + success2 + success3);
	    	
	    /*	if(current_action.equals("enter("+ag+",room1)"))
	    	{
	    		if (success) {
	        		m.put(ag,new Literal[] {Literal.parseLiteral("perm(leave)")});
	        		inroom = true;
	        		//System.out.println(("I can now wave"));
	        		
	        	} else {
	        		m.put(ag, new Literal[] {Literal.parseLiteral("prob(enter)")});
	        	}
	    	}*/
	    	if(current_action.equals("enter("+ag+",room1)") || current_action.equals("enter("+ag+",room2)"))
	    	{
	    		if (success) {
	        		m.put(ag,new Literal[] {Literal.parseLiteral("perm(leave)")});
	        		//inroom = true;
	        		//System.out.println(("I can now wave"));
	        		
	        	} else {
	        		m.put(ag, new Literal[] {Literal.parseLiteral("prob(enter)")});
	        	}
	    	}
	    	else if (current_action.equals("leave("+ag+",room1)") || current_action.equals("leave("+ag+",room2)"))
	    	{
	    		
	    		if (success2) {
	        		m.put(ag,new Literal[] {Literal.parseLiteral("perm(idle)")});
	        		//inroom = true;
	        		//System.out.println(("I just waved can now leave"));
	        		
	        	} else {
	        		m.put(ag, new Literal[] {Literal.parseLiteral("prob(leave)")});
	        		//System.out.println(("I cannot wave :( "));
	        	}
	    	}
//	    	else if (current_action.equals("setpermenter("+ag+")"))
//	    	{
//	    		System.out.println("hey..... "+ success3);
//	    		if (success3) {
//	        		m.put(ag,new Literal[] {Literal.parseLiteral("perm(enterroom)")});
//	        		//inroom = true;
//	        		//System.out.println(("I just waved can now leave"));
//	        		
//	        	} else {
//	        		m.put(ag, new Literal[] {Literal.parseLiteral("prob(enterroom)")});
//	        		//System.out.println(("I cannot wave :( "));
//	        	}
//	    	}
	    	else
	    	{
	    		System.out.println(current_action);
	    		System.out.println(("Error"));
	    	}

	    	
	    	
	    	JSONObject holdsat = queryOutput.getJSONArray("json_out").getJSONArray(0).getJSONObject(1).getJSONObject("state").getJSONObject("holdsat");
	    		//	getJSONObject("holdsat");
	    	//System.out.println(holdsat);
	    	//System.out.println(holdsat.keySet());
	    	
	    	
	    	facts_store.add(jsonExtractor.extractHoldsatJson(holdsat));
	    	//extractJson(holdsat);

		
		//System.out.println("Agents: "+getNamesAgents());
		
		
    	//Storing the state
	    stateList.put(inst_state, new State(occurred,holdsat));
	    	
	    	
    	return m;
	}
    
    
	
    
    
//    protected void stepStarted(int step) {
//		if (this.grounding == null) {
//		this.grounding = toGrounding();
//		}
//		
//	}  
//  
    
	protected int whichState()
	{
		return inst_state;
	}
	
	public InstalGrounding toGrounding() {
		// TODO Auto-generated method stub
		return toGrounding(this.model);
	}
	
	public InstalGrounding toGrounding(InstalModel model) {
		HashMap<String, String[]> types = new HashMap<String, String[]>();
    	//types.put("Agent", getAgentNames().toArray(new String[getAgentNames().size()]));
    //	types.put("Person", getAgentNames().toArray(new String[getAgentNames().size()]));
    	types.put("Person", getNamesAgents().toArray(new String[getNamesAgents().size()]));
    	types.put("Role", new String[] {"x","y"});
    	types.put("Location", new String[] {"room1","room2"});
    	types.put("Number", new String[] {"0","1","2","3","4","5","6","7","8","9","10"});
    	
    	//types.put("Person", new String[]{"me"});
    	String[] facts = new String[] {};
    	grounding = model.newGrounding(types, facts);
    	return grounding;
	}
    
    protected void initialiseInstitution() {
		System.out.println(("Initialising institution..."));
		

		ArrayList<String> facts = new ArrayList<String>();
		int count=0;
		for (String ag : getNamesAgents()) {
			facts.add("initially(perm(enter("+ag+",room1)), rooms)");
			facts.add("initially(perm(arrive("+ag+",room1)), rooms)");
			facts.add("initially(pow(enter("+ag+",room1)), rooms)");
			facts.add("initially(perm(enter("+ag+",room2)), rooms)");
			facts.add("initially(perm(arrive("+ag+",room2)), rooms)");
			facts.add("initially(pow(enter("+ag+",room2)), rooms)");
			if(count%2==0)
			{
				facts.add("initially(role("+ag+", x), rooms)");
				addPercept(ag,Literal.parseLiteral("role(x)"));
			}else
			{
				facts.add("initially(role("+ag+", y), rooms)");
				addPercept(ag,Literal.parseLiteral("role(y)"));
			}
			count++;
			
		}
		
		
		facts.add("initially(occupancy(room1,0),rooms)");
		facts.add("initially(occupancy(room2,0),rooms)");
		facts.add("initially(count_type(room2,y,0),rooms)");
		facts.add("initially(count_type(room2,x,0),rooms)");
		facts.add("initially(count_type(room1,y,0),rooms)");
		facts.add("initially(count_type(room1,x,0),rooms)");
		
		facts.add("initially(max(room1,5),rooms)");
		facts.add("initially(max(room2,3),rooms)");
//		facts.add("initially(addone(0,1),rooms)");
//		facts.add("initially(addone(1,2),rooms)");
//		facts.add("initially(addone(2,3),rooms)");
//		facts.add("initially(addone(2,3),rooms)");
		
		facts_store.add(facts);
		
	/*	Map<String, Literal[]> m = new HashMap<String, Literal[]>();
		

	//	InstalQuery q = this.grounding.newQuery(facts.toArray(new String[facts.size()]), new String[] {});
		InstalQuery q = this.grounding.newQuery(facts.toArray(new String[facts.size()]), empty.toArray(new String[] {"enter(me)"}));
		
		JSONObject queryOutput = q.getQueryOutput();
		
		JSONArray holdsat = queryOutput.getJSONArray("json_out").getJSONArray(0).getJSONObject(1).getJSONObject("state").getJSONArray("occurred");
    	//boolean success = false;
    	/*for (int i = 0; i < occurred.length(); i++) {
    		if (occurred.getJSONArray(i).getJSONArray(1).getJSONArray(0).getString(0).equals("arrives")) {
    			success = true;
    		}
    	}
    	if (success) {
    		m.put("me",new Literal[] {Literal.parseLiteral("perm(wave(me))")});
    		inroom = true;
    		System.out.println(("I can now wave"));
    		
    	} else {
    		m.put("me", new Literal[] {Literal.parseLiteral("prob(enterroom(me))")});
    	}
    	*/
    	//add literals based on the result of the query
    	addPercept(Literal.parseLiteral("perm(enter)"));
    	System.out.println(("Initialisation complete..."));
    	
	}
    
  
    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}
