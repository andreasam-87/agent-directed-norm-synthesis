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
import java.util.HashSet;
import java.util.Iterator;
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

	JsonExtractor jsonExtractor_prev;// = new JsonExtractor("rooms");
	//JsonExtractorOrgSimple jsonExtractor = new JsonExtractorOrgSimple();
	
	InstitutionHandler instHandle;  //file that deals with the institutional changes

	int inst_state=0; //to keep track of the institutional state at any timestep since different from env timesteps
	String current_action =""; //keep track of the current action being executed by any agent

	int trace_count=0;
	StringBuilder strRet = new StringBuilder(); //to build string after parsing json return object from request
	ArrayList<String> facts_store = new ArrayList<String>(); //collect of holdsat/facts/fluents true

	HashMap <Integer,State> stateList = new HashMap<>(); //collection of states of the inst
	
	HashSet <InstMods> instModList = new HashSet<>();

	HashMap <Long,Boolean> completed_infinites  = new HashMap<>();  //collection of completed infinites for action that can take forever

	String inst_file;// = "rooms.ial"; //which institutional file we will be running
	
	String curInstRuleSet="";// = "rooms.ial"; //which institutional file we will be running
	String ruleSet2Mod="";
	//Boolean decision = true;
	
	int count_inst =0; //keep track of the institutional file

	HashMap <Integer,String> instChangePoint = new HashMap<>(); //collection of states of the inst

	JSONObject domainConf; //object to keep track of domain configuration information
	
	public void init(String[] args) {
		// super.init(args);
		super.init(new String[]{"1000"});
		//super.setSleep(100);
		
		System.out.println("Parsing json file ");
		try {
			parseDomainConf("domain.json");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//load values based on domain config file
	//	inst_file = (String) domainConf.get("originalfile");
		//jsonExtractor_prev = new JsonExtractor((String) domainConf.get("institution"));
		

		inst_file = domainConf.get("originalfile").toString();
		jsonExtractor_prev = new JsonExtractor(domainConf.get("institution").toString());
		
		instHandle = new InstitutionHandler("rules.json","modRulesDict.json");
		//String rule_set = "R1-R31";
		String rule_set = "R1;R2;R3;R4;R5;R6;R7;R8;R9;R10;R11;R12;R13;R14;R15;R16;R17;R18;R19;R20;R21;R22;R23;R24;R24;R25;R26;R27;R28";
		String str =instHandle.reviseInst(rule_set);

		try {
			Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/rules.txt"), str.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//	        institutions = new InstalModel[] { room_inst };
//	        this.model = room_inst;
		initialiseInstitution();
//	        if (this.grounding == null) {
//	    		this.grounding = toGrounding();
//	    		}
		instChangePoint.put(inst_state, inst_file);

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

		//clearPercepts(agName); //remove old percepts and add new percepts
		
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
			

			if(agName.equals("bob"))
				System.out.println("Bob in enter block " );

			clearPercepts(agName); //remove old percepts and add new percepts

			for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitutionLocal(agName).entrySet())
			{
				for (int i = 0; i < entry.getValue().length; i++)
				{
					addPercept(entry.getKey(), entry.getValue()[i]);
					
//					if(agName.equals("bob"))
//					{
//						System.out.println("adding percept for bob");
//						System.out.println("Percept"+i+ " : "+ entry.getValue()[i]);
//					}
					//	System.out.println("Bob in enter block " );
					//System.out.println("adding percept");
					//addPercept(entry.getValue()[i]);
					//	System.out.println("Percept"+i+ " : "+ entry.getValue()[i]);

				}
			}
			updateAgsPercept();
//
//			if(agName.equals("bob"))
//				System.out.println("Bob leaving enter block " );
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
		else if (action.getFunctor().equals("sense")) {
			clearPercepts(agName); //remove old percepts and add new percepts
			
			String tempInst = inst_file;
			
			//inst_file = "roomsInst.lp";
			String tocheck = (action.getTerm(0)).toString();
			tocheck= tocheck.replace("\"","");
			tocheck = "observed("+tocheck+")";
			//current_action = "observed("+current_action+")";
			//String ex = act.toString();

			String inst =  (action.getTerm(1)).toString();
			inst= inst.replace("\"","");
			inst_file = inst;
			
			//clearPercepts(agName); //remove old percepts and add new percepts

			//	public static final Literal check  = Literal.parseLiteral("check");
			//for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitution().entrySet())
			System.out.println("TO Check: "+ tocheck);
			
			//senseInstitution(String ag, String act, String avoid) {
			for (Map.Entry<String, Literal[]> entry : this.senseInstitution(agName,tocheck,"capacityExceeded").entrySet())
			{

				//System.out.println("Percepts from sense: ");
				for (int i = 0; i < entry.getValue().length; i++)
				{
					//if(entry.getValue()[i].equals(Literal.parseLiteral(" ")))
					addPercept(entry.getKey(), entry.getValue()[i]);
					//addPercept(entry.getValue()[i]);
					//System.out.println("Percept: "+ entry.getValue()[i]);
				}
			}
			inst_file = tempInst;
			inst_state--;
			return true; 
			

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
//			if(!isRunning(action))
//			{
//				markAsExecuting(action);
			
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						int when = (int)((NumberTerm) action.getTerm(0)).solve();
						int numStates = (int)((NumberTerm) action.getTerm(1)).solve();

						String atmpt = (action.getTerm(2)).toString();

						String problem = (action.getTerm(3)).toString();
						
						System.out.println("Checking if the problem has an existing solution");
						
						//public String checkExistingMods(String act, String prob)
						String solution = checkExistingMods(atmpt,problem);
						if(solution.equals("none"))
						{
							System.out.println("A solution does not currently exists for this action and problem");
							
							System.out.println("Check state "+when+ " and if possible "+ numStates +" states before and after.");

							System.out.println("Revision begins.................");

							String modes = jsonExtractor_prev.getModesFile("/Users/andreasamartin/Documents/InstalExamples/rooms/dict.txt","enter");

						//	System.out.println(modes); //just printing for now
							System.out.println("Modes file created");
							
							//writing to the modes file is what is required so that the ILP can access this file
							Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/modes"+trace_count+""), modes.getBytes());
							
							String trace = jsonExtractor_prev.getTraceFile(when,numStates,stateList);
							
							System.out.println("Trace file created");
							//writing to the trace file is what is required so that the ILP can access this file
							Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/trace"+trace_count+".txt"), trace.getBytes());
							trace_count++;
						
							System.out.println("About to Run pyhton script to create xhail file");
							try {
								Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/toRevise"), compileXHAIL().getBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//System.out.println(compileXHAIL());
							System.out.println("Finished Running pyhton script for xhail");
							
							String revision = reviseInstitution("/Users/andreasamartin/Documents/InstalExamples/rooms/trace"+trace_count+".txt","/Users/andreasamartin/Documents/InstalExamples/rooms/modes"+trace_count+"");
							
							
							//String rule_set = "R1;R2;R3;R4;R5;R6;M1R7;R8;R9;R10;R11;R12;R13;R14;R15;R16;R17;R18;R19;R20;R21;R22;R23;R24;R24;R25;R26;R27;R28;R29;R30;R31;R32;R33";
							String rule_set = instHandle.getRuleSet(problem, atmpt,curInstRuleSet);
							String str =instHandle.reviseInst(rule_set);
							/** the above probably shouldn't happen here, to rethink*/
							
							System.out.println("Revision has ended.... Completing action ......");
							
							if(str.equals("none"))
							{
								System.out.println("No solution available currently.");
								
								addPercept(agName, Literal.parseLiteral("revisionFailed"));
							}
							else
							{
								if(getDecisionOracle())
								{
									System.out.println("Revision approved by Oracle, can be implemented");
									
									//Add solution to the solution set
									instModList.add(new InstMods(atmpt,problem,rule_set,str));
									//curInstRuleSet = rule_set;
									ruleSet2Mod = rule_set;
									addPercept(agName, Literal.parseLiteral("revisionSuccess"));
									}
								else
								{
									System.out.println("Revision not approved by Oracle, will be discarded");
									
									addPercept(agName, Literal.parseLiteral("revisionFailed"));
								}
							}

						}
						else
						{
							System.out.println("A solution exists for this problem");
							System.out.println("Checking if the solution is currently in place");
//							System.out.println("Solution - "+solution+" active inst file - "+inst_file);
//							if(solution.equals(inst_file))
//							{
//								System.out.println("Solution is active");
//								addPercept(agName, Literal.parseLiteral("revisionSuccessful(active)"));
//							}
//							else	
//							{
//								System.out.println("Changing to the existing solution");
//								addPercept(agName, Literal.parseLiteral("revisionSuccess"));
								
							//System.out.println("Solution ruleset - "+solution+" active ruleset - "+curInstRuleSet);
							if(solution.equals(curInstRuleSet))
							{
								System.out.println("Solution is active");
								addPercept(agName, Literal.parseLiteral("revisionSuccessful(active)"));
							}
							else	
							{
								//	TODO:CHANGE TO THE NEW INSTITUTION, WORK THAT OUT WITH PERCEPTS
								
								//maybe need to do below as well
								//add a record to the changepoint list
								//instChangePoint.put(inst_state, inst_file);
								
								System.out.println("Changing to the existing solution");
								ruleSet2Mod = solution;
								addPercept(agName, Literal.parseLiteral("revisionSuccess"));
						
							}
						}

//						if(decision)
//						{
//							addPercept(agName, Literal.parseLiteral("revisionSuccess"));
//							decision = false; 
//						}
//						else
//						{
//							addPercept(agName, Literal.parseLiteral("revisionFailed"));
//							decision = true; 
//						}	
						
						RoomEnvironmentLocal_Inst.this.markAsCompleted(action);
					}catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};

			Thread t = new Thread(r);
			t.start();

			//addPercept(agName, Literal.parseLiteral("revisionFailed"));
			inst_state--;
			// return true; //do I need a separate return here or the one to the bottom will do.
			
		//	}
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
			System.out.println("Skipping a step or 2");
			inst_state--;
			return true;
		}
		else if (action.getFunctor().equals("changeInst")) {
			System.out.println("Setting up new institution:");
			String file = (action.getTerm(0)).toString();
			inst_file = StringUtils.replaceAll(file,"\"", "");  //file
			//inst_file = file;  //file
			//inst_file = "roomsInst.lp";  //file
			
			//need to do this before or something
			curInstRuleSet = ruleSet2Mod;
			
			
			//add a record to the changepoint list
			instChangePoint.put(inst_state, curInstRuleSet);
			
			System.out.println("New institution enabled");
			inst_state--;
			return true;
		}
		else if (action.getFunctor().equals("addAgent")) {
		//	System.out.println("Setting up new institution:");
			//String agent = (action.getTerm(0)).toString();
			
			//int nParams = action.getTerms().size();
			String list = (action.getTerm(0)).toString();
			System.out.println("List of agent names: "+list);
			String names="";
			

			String add = "initially(meeting,rooms)\n";
			if(list.contains("["))
			{
				System.out.println("Adding multiple agents to the scenario ");
				list = list.substring(1,list.length()-1);
				String [] agentNames = list.split(",");
				int count =0;
				for(String name: agentNames)
				{
					names+=name+" ";
					
					if(count%2==0)
					{
						add+="initially(role("+name+",y),rooms)\n";
					}else
					{
						add+="initially(role("+name+",x),rooms)\n";
					}
					count++;
					
					
					// in the short term, adding the necessary permissions 
//					add+="initially(perm(enter("+name+",room1)),rooms)\n";
//					add+="initially(perm(enter("+name+",room2)),rooms)\n";
//					add+="initially(perm(arrive("+name+",room1)),rooms)\n";
//					add+="initially(perm(arrive("+name+",room2)),rooms)\n";
//					add+="initially(pow(enter("+name+",room1)),rooms)\n";
//					add+="initially(pow(enter("+name+",room2)),rooms)\n";
					

					addPercept(name,Literal.parseLiteral("overseer(synthesizer)"));
					
					//temporarily assigning all agents to the single synthesizer
					addPercept("synthesizer",Literal.parseLiteral("assignee("+name+")"));
				}
			}
			else
			{
				names = list;
				String agent = list;
				System.out.println("Adding "+agent+ " to the environment");
				
				//inst_file = "rooms_v2.lp"; 
				
				add+="initially(role("+agent+",y),rooms)\n";
	
				// in the short term, adding the necessary permissions 
				add+="initially(perm(enter("+agent+",room1)),rooms)\n";
				add+="initially(perm(enter("+agent+",room2)),rooms)\n";
				add+="initially(perm(arrive("+agent+",room1)),rooms)\n";
				add+="initially(perm(arrive("+agent+",room2)),rooms)\n";
				add+="initially(pow(enter("+agent+",room1)),rooms)\n";
				add+="initially(pow(enter("+agent+",room2)),rooms)\n";
			
				addPercept(agent,Literal.parseLiteral("overseer(synthesizer)"));
				
				//temporarily assigning all agents to the single synthesizer
				addPercept("synthesizer",Literal.parseLiteral("assignee("+agent+")"));
			}
			//initially(perm(enter(agent6,room2)),rooms)
			String line = "";
			String file = "/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf";
			try {
				
				// Editing the facts file
				List<String> lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
		    	for (String line2 : lines) {
		    		if(!(line2.contains("capacityExceededViol")))
		    			line+=line2+"\n";
//		    		else
//		    			System.out.println("line: "+line2);
		    	}
				line+=add;
				//System.out.println("New facts file: \n"+line);
				
				//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), add.toString().getBytes(),StandardOpenOption.APPEND);
				Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), line.toString().getBytes());
			
				//Must edit the config file to include the new agent names. 
			
				//errors adding an agent from here. 
//				getEnvironmentInfraTier().getRuntimeServices().createAgent(  //arg0, arg1, arg2, arg3, arg4, arg5, arg6) .getRuntimeServices().createAgent(
//			         "anotherAg",     // agent name
//			         "ag1.asl",       // AgentSpeak source
//			         null,            // default agent class
//			         null,            // default architecture class
//			         null,            // default belief base parameters
//			         null);           // default settings
			
				
				// updating the config file
				file = "/Users/andreasamartin/Documents/InstalExamples/rooms/roomsConf.idc";
				lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
				line="";
		    	for (String line2 : lines) {
		    		if(line2.contains("Person"))
		    			line+=line2+names+" \n";
		    		else
		    			line+=line2+"\n";

		    	}
		
				//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), add.toString().getBytes(),StandardOpenOption.APPEND);
				Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsConf.idc"), line.toString().getBytes());
			
				System.out.println("Agent added");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
//		if(agName.equals("bob"))
//			System.out.println("What is happening to Bob? " + action.toString());
//		//System.out.print("Stuck here ");
		inst_state++;
		return true; // the action was executed with success
	}
	
	private String compileXHAIL()
	{
	//	String cmd = "python3 /Users/andreasamartin/InstalStable/istable/code/instal-stable/compiler.py compile_ial test.txt";
		String cmd = "python3 compiler.py compile_ial /Users/andreasamartin/Documents/InstalExamples/rooms/outDict.txt";
		
		String output;
		try {
			return Processes.runShellCmdRes(cmd);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private void Updatefile(String file, String update, String add, String remove)
	{
		String line = "";
		try {
			
			// Editing the facts file
			List<String> lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
	    	for (String line2 : lines) {

	    		if(!remove.contentEquals("") && !(line2.contains(remove)))
	    			line+=line2+"\n";
	    	}
	    	if(!add.contentEquals(""))
	    		line+=add;

			//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), add.toString().getBytes(),StandardOpenOption.APPEND);
			Files.write(Paths.get(file), line.toString().getBytes());
		
	
			// updating the config file
		//	file = "/Users/andreasamartin/Documents/InstalExamples/rooms/roomsConf.idc";
//			lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
//			line="";
//	    	for (String line2 : lines) {
//	    		if(line2.contains("Person"))
//	    			line+=line2+names+" \n";
//	    		else
//	    			line+=line2+"\n";
//
//	    	}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Map<String, Literal[]> senseInstitution(String ag, String act, String avoid) {
		Map<String, Literal[]> m = new HashMap<String, Literal[]>();

		System.out.println("Inst file being used is: "+inst_file);
		
		Updatefile("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf","","initially(meeting,rooms)\n", "capacityExceededViol");

		
		try {
			Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsQuery.iaq"),act.getBytes());


			String cmd = "/usr/local/bin/docker run -v /Users/andreasamartin/Documents/InstalExamples/rooms:/workdir instal-stable solve $* -i /workdir/"+ inst_file +" -f /workdir/roomsFacts.iaf -d /workdir/roomsConf.idc -q /workdir/roomsQuery.iaq -j /workdir/out.json -v";
		
			String output = Processes.runShellCmdRes(cmd);
			
			ArrayList<Literal> inst_sense = new ArrayList<Literal>();
			
			//what occurred this timestep
			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","occurred",1);
			String occurred = strRet.toString();
		
		//	getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","observed",1);
		//	String observed = strRet.toString();

			System.out.println("Sense - what occurred: "+occurred);
			//returning what occurred
			for(String s : occurred.split("\n"))
			{
				inst_sense.add(Literal.parseLiteral(s));
				System.out.println("occurred: "+s);
			}
			
			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","holdsat",1);

	
			String[] percepts = strRet.toString().split("\n");

			int count = StringUtils.countMatches(strRet.toString(), ag);
			//System.out.println("Count "+count);
			int c=0;
			boolean viol = false;
			
			String roomCap="";
			String inroom="";
			int in = act.indexOf("(");
			int ind = StringUtils.ordinalIndexOf(act, "(", 2);//  .act.indexOf("(");
			String str = act.substring(ind,act.length()-1);
			System.out.println("String to check "+str);
			
			boolean entered = false;
			for (String var : percepts)
			{
				if(var.contains(avoid))
				{
					viol=true; //indicates a violation of the
				
					inst_sense.add(Literal.parseLiteral(var));
				}
				if(var.contentEquals("in_room"+str))
				//if (var.matches("in_room"+str))
				{
					System.out.println("Found it");
					inst_sense.add(Literal.parseLiteral(var));
				}
//				else
//					System.out.println("Matches didn't work");

				
			}


			//check if agent is in the room first
			if (viol)
			{
				inst_sense.add(Literal.parseLiteral("revisionUnacceptable"));
				
			}
			else
			{
				inst_sense.add(Literal.parseLiteral("revisionAcceptable"));
			}


	    	int s = inst_sense.size();
	    	
		//	Literal[] inst = (Literal []) inst_sense.toArray(); 

	    	//used arralist for variable size percepts then converting arraylist to an array as expected by the collection.
			Literal[] inst = new Literal [s];// (inst_sense.toArray()); 
			for(int i=0;i<s;i++)
			{
				inst[i] = inst_sense.get(i);
			}
	    
			//m.put(ag,inst_sensors);

			m.put(ag,inst);
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
			String cmd = "/usr/local/bin/docker run -v /Users/andreasamartin/Documents/InstalExamples/rooms:/workdir instal-stable solve $* -i /workdir/"+ inst_file +" -f /workdir/roomsFacts.iaf -d /workdir/roomsConf.idc -q /workdir/roomsQuery.iaq -j /workdir/out.json -v";
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
		//	System.out.println("\nwhat occurred "+strRet.toString());
			
			
			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","observed",1);
			String observed = strRet.toString();
			//	System.out.println("\nwhat occurred "+strRet.toString());

			//get the new facts sorted and saved to file for next run
			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","holdsat",0);

			//get feedback for the agents as percepts
			//System.out.println("\npotentially agent output "+strRet.toString());
			getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","holdsat",1);

			//add to the collection of states after each agent's action
			stateList.put(inst_state, new State(occurred,observed,facts_store.get(inst_state)));

			String[] percepts = strRet.toString().split("\n");

			//System.out.println("\npotentially agent output Begin \n");
			//System.out.println(percepts.toString());
			int count = StringUtils.countMatches(strRet.toString(), ag);
			//System.out.println("Count "+count);
			int c=0;
			boolean viol = false;
			Literal[] inst_sensors = new Literal[count+1]; //add +1 to array size if capacity percept being added
			
			ArrayList<Literal> inst_sense = new ArrayList<Literal>();
			
			String roomCap="";
			String inroom="";
			boolean entered = false;
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
					inst_sense.add(Literal.parseLiteral(var));
					c++;
					
					//check if the agent wasn't allowed in the room
					//could be problematic given array size, maybe use an array list then turn it into an arrray. 

					if (var.contains("in_room"))
					{
						inroom=var; //keeps track of if the agent is in some room
						entered=true;
					}
				}
//				if (var.contains("in_room"))
//				{
//					inroom=var; //keeps track of if the agent is in some room
//					entered=true;
//				}
				
			}

			//checking if the capacity exceeded fluent is set and sets agent percept
			//if (Arrays.asList(percepts).contains("capacityExceeded")) //doesn't work actually

			//check if agent is in the room first
			if (viol)
			{
				//System.out.println("true");
				roomCap=StringUtils.substringBetween(roomCap, "(", ")"); //find the name of the room which has exceeded capacity
				if(inroom.contains(roomCap)) //check if this agent is in the room
				{
					inst_sensors[c] = Literal.parseLiteral("roomCapacityExceeded");
					inst_sense.add(Literal.parseLiteral("roomCapacityExceeded"));
				}
				else
					inst_sensors[c] = Literal.parseLiteral("roomCapacityOkay");
			}else
			{
				inst_sensors[c] = Literal.parseLiteral("roomCapacityOkay");
			}
			
			
			//check if the agent wasn't allowed in the room
			//could be problematic given array size, maybe use an array list then turn it into an arrray. 
//			if (!(var.contains("in_room")) && current_action.contains("enter"))
//				inst_sensors[c] = Literal.parseLiteral("roomCapacityOkay");
			if (!entered && current_action.contains("enter"))
			{
				inst_sense.add(Literal.parseLiteral("prob(enter)"));
				
			}
			
			inst_sense.add(Literal.parseLiteral("extraPercept"));
//	    	//System.out.println(inst_sensors.length);
	    	int s = inst_sense.size();
	    	
		//	Literal[] inst = (Literal []) inst_sense.toArray(); 

	    	//used arralist for variable size percepts then converting arraylist to an array as expected by the collection.
			Literal[] inst = new Literal [s];// (inst_sense.toArray()); 
			for(int i=0;i<s;i++)
			{
				inst[i] = inst_sense.get(i);
			}
	    
			//m.put(ag,inst_sensors);

			m.put(ag,inst);
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
				addPercept(ag,Literal.parseLiteral("bold("+count+")"));
			}else
			{
				//facts.add("initially(role("+ag+", y), rooms)");
				strRet.append("initially(role("+ag+", y), rooms)\n");
				addPercept(ag,Literal.parseLiteral("role(y)"));
				addPercept(ag,Literal.parseLiteral("bold("+count+")"));
			}
			count++;
			//addPercept(ag,Literal.parseLiteral("overseer(synthesizer)"));
			
			//identifying overseer and assignee at startup so agents can know who to contact
			if(!(ag.contains("synthesizer")))
			{
				if(count%2==0)
				{
				//temporarily setting all agents' overseer to the single synthesizer.
				addPercept(ag,Literal.parseLiteral("overseer(synthesizer1)"));
				
				//temporarily assigning all agents to the single synthesizer
				//addPercept("synthesizer1",Literal.parseLiteral("assignee("+ag+")"));
				}
				else
				{
					//temporarily setting all agents' overseer to the single synthesizer.
					addPercept(ag,Literal.parseLiteral("overseer(synthesizer2)"));
					
					//temporarily assigning all agents to the single synthesizer
					//addPercept("synthesizer2",Literal.parseLiteral("assignee("+ag+")"));
					
				}
			}

		}
		config.append("\nRole: x y\n" +
				"Location: room1 room2\n" +
				"Number: 0 1 2 3 4 5 6 7 8 9 ");
		
//		String r = domainConf.get("role").toString();
//		String l = domainConf.get("location").toString();
//		String n = domainConf.get("number").toString();
//		
//		String r1 = "x y";
//		String r = (String) domainConf.get("role");
//		String l = (String) domainConf.get("location");
//		String n = (String) domainConf.get("number");
		//System.out.println(domainConf.get("role")+ " --test--" + domainConf.get("role").toString());
		
//		r = r.replace("\"","");
//		l = l.replace("\"","");
//		n = n.replace("\"","");
		
//		if(r1.equals(r))
//		{
//			System.out.println("No problem");
//		}
//		else
//			System.out.println("Problem");
//		
//		String con = "\nRole: "+r+
//				"\nLocation: "+l +
//				"\nNumber: " + n+ " ";
		
//		config.append("\nRole: "+r1+
//				"\nLocation: "+l +
//				"\nNumber: " + n+ " ");
//		
//		config.append(con);
		//domainConf.get("institution").toString()
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

	private String checkExistingMods(String act, String prob)
	{
		String ret="none";
		for(InstMods ins: instModList)
		{
			String ac = ins.getAction();
			if(StringUtils.substringBefore(ac,"(").equals(StringUtils.substringBefore(act,"(")))
			{
				if(ins.getProblem().equals(prob))
					return ins.getRuleSet(); //old -> .getInstFile();
			}
		}
		return ret;
	}
	
	private String reviseInstitution(String tracefile, String modesfile)
	{
		//will need to use a collection or file that maps the issues to the already generated files - modified institution
		
		//maybe do the file thing here or have t
		
		return "roomsInst.lp";
	}
	
	private boolean getDecisionOracle()
	{
		return true;
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
	
	/* Method to extract the domain configuration information, basically used readfile in rovers env */
	
	private void parseDomainConf(String path) throws Exception{
        String jsonString;

        final BufferedReader fileReader = new BufferedReader(new FileReader(path));
        final StringBuilder jsonContent = new StringBuilder();
        String line;
        while ((line =  fileReader.readLine()) != null) {
            jsonContent.append(line);
        }
        fileReader.close();
        jsonString = jsonContent.toString();

        domainConf = new JSONObject(jsonString);
        
//        Iterator <String> itr1 = domainConf.keys();// .iterator();
//        
//        
//        while (itr1.hasNext()) {
//        	 String key = itr1.next();
//            System.out.println(key + " : " + domainConf.get(key));
//        }
    
    }
	
	
	/** Called before the end of MAS execution */
	@Override
	public void stop() {
		super.stop();
	}


}
