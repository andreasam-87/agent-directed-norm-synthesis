import java.io.BufferedReader;
import java.io.File;
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

import org.apache.commons.io.FileUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class RoomEnvironmentLocal_Inst extends StepSynchedEnvironment {

	//JsonExtractor new JsonExtractor();// = new JsonExtractor("rooms");
	//JsonExtractorOrgSimple jsonExtractor = new JsonExtractorOrgSimple();
	
	InstitutionHandler instHandle;  //file that deals with the institutional changes

	int inst_state=0; //to keep track of the institutional state at any timestep since different from env timesteps
	String current_action =""; //keep track of the current action being executed by any agent

	int trace_count=0;
	StringBuilder strRet = new StringBuilder(); //to build string after parsing json return object from request
	ArrayList<String> facts_store = new ArrayList<String>(); //collect of holdsat/facts/fluents true
	HashMap<Integer, String> factsPerState = new HashMap<Integer, String>();
	
	HashMap <Integer,State> stateList = new HashMap<>(); //collection of states of the inst
	
	HashMap <String,String> potentialRevision = new HashMap<>(); //collection of states of the inst
	
	HashSet <InstMods> instModList = new HashSet<>();
	HashMap<String, ArrayList<String>> agentsBySynthesizerInMAS = new HashMap<String, ArrayList<String>>();
	 ArrayList<String> agentsInMAS = new ArrayList<String>();

	
	HashMap <Long,Boolean> completed_infinites  = new HashMap<>();  //collection of completed infinites for action that can take forever

	String inst_file;// = "rooms.ial"; //which institutional file we will be running
	String files_directory;
	String temp_files_directory;
	String instal_path,local_inst_path;
	String mas2jfile;
	
	String curInstRuleSet="";// = "rooms.ial"; //which institutional file we will be running
	String ruleSet2Mod="";
	String instFile2Mod="";
	String instRevLog="";
	
	LocalDateTime date;
	//LocalTime time;
	//Boolean decision = true;
	
	int count_inst =0; //keep track of the institutional file

	HashMap <Integer,String> instChangePoint = new HashMap<>(); //collection of states of the inst

	JSONObject domainConf; //object to keep track of domain configuration information
	String logFilePath,instChangeFilePath,revisionsFilePath;
	
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
		//new JsonExtractor() = new JsonExtractor((String) domainConf.get("institution"));
		

		inst_file = domainConf.get("originalfile").toString();
		//new JsonExtractor() = new JsonExtractor(domainConf.get("institution").toString());
		
		files_directory = domainConf.get("filespath").toString();
		temp_files_directory = domainConf.get("tempfilespath").toString();
		mas2jfile = domainConf.get("mas2jfile").toString();
		instal_path = domainConf.get("instalpath").toString();
		local_inst_path = domainConf.get("localinst").toString();
		
		instHandle = new InstitutionHandler("rules.json","modRulesDict.json");
		//String rule_set = "R1-R31";
		/*String rule_set = "R1;R2;R3;R4;R5;R6;R7;R8;R9;R10;R11;R12;R13;R14;R15;R16;R17;R18;R19;R20;R21;R22;R23;R24;R24;R25;R26;R27;R28";
		String str =instHandle.reviseInst(rule_set);

		try {
			//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/rules.txt"), str.getBytes());
			Files.write(Paths.get(files_directory+"rules.txt"), str.getBytes());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		date = LocalDateTime.now();
		//time = LocalTime.now();
		
		logFilePath = files_directory+"MAS_Run_"+date.toString()+".csv";
		instChangeFilePath = files_directory+"MAS_Inst_Change_"+date.toString()+".csv";
		revisionsFilePath = files_directory+"MAS_Revision_Attempts_"+date.toString()+".csv";
		//logFilePath,instChangeFilePath
		
		try {
			
			//create file for participants activity logging
			Files.createFile(Paths.get(logFilePath));
			
			String header ="agent,action,message,when,insState,timestep\n";// ag+","+act+","+msg+","+status+","+st+","+step+"\n";

			Files.write(Paths.get(logFilePath), header.getBytes(),StandardOpenOption.APPEND);
			
			//create file for logging when the institution has changed
			Files.createFile(Paths.get(instChangeFilePath));
			
			header ="synthesiser,filepath,instState,timestep\n";

			Files.write(Paths.get(instChangeFilePath), header.getBytes(),StandardOpenOption.APPEND);
				
			//create file for logging revision activity attempts
			Files.createFile(Paths.get(revisionsFilePath));
			
			header ="synthesiser,action,problem,status,time,instState,timestep\n";

			Files.write(Paths.get(revisionsFilePath), header.getBytes(),StandardOpenOption.APPEND);
	
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
	
	private void logToFile(String ag, String act,String msg,String status,int st, int step)
	{
		String concat = ag+",\""+act+"\","+msg+","+status+","+st+","+step+"\n";
		try {
			Files.write(Paths.get(logFilePath), concat.getBytes(),StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

			if(agName.equals("baseAgent"))
			{
				System.out.println("baseAgent is entering "+current_action );
			}
				
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
			
			inst_state++;
			
			logToFile(agName,current_action,"","start-action",inst_state,super.getStep());
//
//			if(agName.equals("bob"))
//				System.out.println("Bob leaving enter block " );
		}

		else if (action.getFunctor().equals("leave")) {
			current_action = ex.replace("\"","");
			//current_action = "observed("+current_action+")";
			//String ex = act.toString();
			String is = "";
			
			if(action.getTermsSize()>2)
			{
				System.out.println("More than 2 terms");
				is = action.getTerm(2).toString();
				is = is.replace("\"","");
				String toRep = ",\""+is+"\"";
				
				current_action = ex.replace(toRep,"");
				System.out.println("Action "+current_action);
				
				
				if(is.contains(":"))
				{
					is = StringUtils.substringBefore(is,":");//is.split(":")[0];
					//leave(N,Rm,St);
					String temp = StringUtils.substringBetween(current_action, "(", ")");
					//temp = temp.replace(",", ", ");
					String toAdd = "initially(permExit("+temp+"),rooms)\ninitially(in_room(" +temp+"),rooms)\n";
					//String toRem = "permEntry("+temp+")";
					String toRem = "permEntry("+agName;
					
					Updatefile(files_directory+"roomsFacts.iaf", "",toAdd, toRem);
					//System.out.println("I have updated the facts file with "+toAdd + " and removed "+toRem);
					//Updatefile(String file, String update, String add, String remove)
					
					//"initially(meeting("+room+"),rooms)\n"
				}
				//current_action = ex.replace(toRep,"");
				//System.out.println("Action "+current_action);
				
			}
			
			
			current_action = "observed("+current_action+")";
			
			clearPercepts(agName); //remove old percepts and add new percepts
			
			//System.out.println(agName+"Testing 1 ");
				
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
				//System.out.println(agName+"Testing 1 2 ");
			}
			inst_state++;
			logToFile(agName,current_action,is,"end-action",inst_state,super.getStep());
			//System.out.println(agName+"Testing 1 2 3");

		}
		else if (action.getFunctor().equals("sense")) {
			System.out.println(agName+ "is trying to sense ");
			clearPercepts(agName); //remove old percepts and add new percepts
			
			final String tempInst = inst_file;
			
			//inst_file = "roomsInst.lp";
			String tocheck = (action.getTerm(0)).toString();
			tocheck= tocheck.replace("\"","");
			tocheck = "observed("+tocheck+")";
			//current_action = "observed("+current_action+")";
			//String ex = act.toString();

			String inst =  (action.getTerm(1)).toString();
			System.out.println("inst file from agent: "+ inst + " checking -"+tocheck);
			
			inst= inst.replace("\"","");
			
			String toavoid = (action.getTerm(2)).toString();
			toavoid= toavoid.replace("\"","");
			
			System.out.println("trying to avoid what in toavoid - "+ toavoid);
			
			//System.out.println("inst file from agent after: "+ inst);
			inst_file = inst;
			
			//clearPercepts(agName); //remove old percepts and add new percepts

			//	public static final Literal check  = Literal.parseLiteral("check");
			//for (Map.Entry<String, Literal[]> entry : this.perceptsFromInstitution().entrySet())
			System.out.println("TO Check: "+ tocheck);
			
			System.out.println("/////// The file in use is :"+inst_file);
			
			//senseInstitution(String ag, String act, String avoid) {
			
			//for (Map.Entry<String, Literal[]> entry : this.senseInstitution(agName,tocheck,"capacityExceeded").entrySet())
			for (Map.Entry<String, Literal[]> entry : this.senseInstitution(agName,tocheck,toavoid).entrySet())
					
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
			//inst_state--;
			System.out.println(agName+ " has completed a sense");
			return true; 
			

		}
		else if (action.getFunctor().equals("checkState")) {
			//String eventOccurred = (String) ((StringTerm) act.getTerm(0)).toString();
			//String eventOccurred = (String) ((StringTerm) act.getTerm(0)).getString();
			//	.toString();
			//String eventOccurred =(String)(StringTerm) act.getTerm(0)).getString();
			//int x=0;
			//System.out.println("reached here");
			
			clearPercepts(agName); //remove old percepts and add new percepts
			
			try {
				//x = (int) ((NumberTerm) act.getTerm(0)).solve();
				String eventOccurred = (action.getTerm(0)).toString();
				eventOccurred = eventOccurred.replace("\"","");
				System.out.println("Checking state for: "+eventOccurred);
				int whenOcc = new JsonExtractor().checkStateForEvent(eventOccurred,stateList);
				System.out.println("Event occurred in state: "+whenOcc);
				//String data = new JsonExtractor().getStateFactsandEvents(whenOcc-1,stateList);
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
				
				//inst_state--;
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
		else if (action.getFunctor().equals("getLocations")) {

			try {
				//String lookingFor = (action.getTerm(0)).toString();
				
				String locations = domainConf.get("location").toString();
				
				locations = locations.replace("\"","");
				//System.out.println("Checking state for vip rooms "+lookingFor);
				
				String [] locate = locations.split(" ");
				
				for(String item : locate)
				{
					addPercept(agName, Literal.parseLiteral("location("+item+")"));
				}
				
				addPercept(agName, Literal.parseLiteral("locations_found"));
			
				//inst_state--;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error here ");
			}
			
		}
		else if (action.getFunctor().equals("checkVipRoom")) {

			try {
				String lookingFor = (action.getTerm(0)).toString();
				lookingFor = lookingFor.replace("\"","");
				//System.out.println("Checking state for vip rooms "+lookingFor);
				String viproom = "";
				
				if(stateList.size()>0)
				{
					State stateToCheck = stateList.get(0);
					String stateFacts = stateToCheck.getFactsStr();
					
					for (String fact : stateFacts.split("\n"))
			        {
			            if (fact.contains(lookingFor))
			            {
			                viproom = fact;
			                int i = viproom.indexOf("(");
			                int i2 = viproom.lastIndexOf(")");
			                String temp = viproom.substring(i+1,i2);
			               // System.out.println("Temp string: "+temp);
			                String room = StringUtils.substringBetween(temp, "(", ")");
			                
			                viproom = room;
			                
			                addPercept(agName, Literal.parseLiteral("viproom("+viproom+")"));
			                //initially(vip_room(room1),rooms)
			                 
			               // break;
			            }
			        }
				}
				else
				{
					String path = files_directory+"narX";
					List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
					
					String content="";
			    	
					for (String line : lines) {
			    		if(line.contains("vip_room"))
			    		{
			    			//holdsat(vip_room(room2),rooms,0).
			    			viproom = line;
			                int i = viproom.indexOf("(");
			                int i2 = viproom.indexOf(",");
			                // int i2 = viproom.lastIndexOf(")");
			                String temp = viproom.substring(i+1,i2);
			                //System.out.println("Temp string: "+temp);
			                String room = StringUtils.substringBetween(temp, "(", ")");
			                
			                viproom = room;
			                
			                addPercept(agName, Literal.parseLiteral("viproom("+viproom+")"));
			                //break;
			    		}
			    			
			    	}
				}
				
				//if(stateFacts.con)
				//int whenOcc = new JsonExtractor().checkStateForEvent(eventOccurred,stateList);
				//System.out.println("VIP rooms found : "+viproom);
				
				addPercept(agName, Literal.parseLiteral("vipRoomsFound"));
			
				//inst_state--;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error here ");
			}
			
		}
		else if (action.getFunctor().equals("verifyRevisionAcceptable")) {
			clearPercepts(agName);
			try {
				String revFile = (action.getTerm(0)).toString();
				String logFile = (action.getTerm(1)).toString();
				
				System.out.println("Checking if revision okay: "+revFile);
				System.out.println("Checking if revision okay: "+logFile);
				
				if(getDecisionOracleLive(logFile))
				{
						
					System.out.println("Revision approved by Oracle, can be implemented");
					
					addPercept(agName, Literal.parseLiteral("revisionAcceptable("+revFile+")"));
		
					
				}
				else
				{
					System.out.println("Revision not approved by Oracle, will be discarded");
					
					addPercept(agName, Literal.parseLiteral("revisionUnacceptable("+revFile+")"));
				}
				
				

			//	addPercept(agName, Literal.parseLiteral("revisionAcceptable("+revFile+")"));
				
				//addPercept(agName, Literal.parseLiteral("revisionAcceptable"));

				//inst_state--;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error here ");
			}

		}
		else if (action.getFunctor().equals("revise")) {
			
			clearPercepts(agName); //remove old percepts and add new percepts
			/*Agent ID in the file names */
			//RoomEnvironmentLocal_Inst.super.
			//final RoomEnvironmentLocal_Inst sup = this.super;
			
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						int when = (int)((NumberTerm) action.getTerm(0)).solve();
						int numStates = (int)((NumberTerm) action.getTerm(1)).solve();

						String atmpt = (action.getTerm(2)).toString();

						String problem = (action.getTerm(3)).toString();
						
						//Will probably need to make this more robust but currently works for holdsat. meeting
						String reason = (action.getTerm(4)).toString();
						
						String status="";
						
						//0-problem,1-toAdd, 3-modetoadd 
						ArrayList<String> tempArr = decodeString(problem,atmpt,reason); 
						String toAdd = tempArr.get(1);
						String modeAdd=tempArr.get(2);
						String prob = tempArr.get(0);
						
						/*
						//get room in both cases "room1" or room 
						//String room = StringUtils.substringBetween(atmpt, "\"", "\"");
						String room = StringUtils.substringBetween(atmpt, ",", ")");
						room = room.replaceAll("\"", "");
						
						if(reason.contains("(")) {
							int count = StringUtils.countMatches(reason, "(");
							if(count>1)
							{
								reason = StringUtils.substringBetween(reason, "(", "))");
								//toAdd = reason+"("+room+"),rooms,"+when+")";
								toAdd = reason+"("+room+")";
							}else {
								reason = StringUtils.substringBetween(reason, "(", ")");
								//toAdd = reason+",rooms,"+when+")";
								toAdd = reason;
							}
							
						}
						String prob = problem+"("+room+")";*/
						
						//holdsat(pow(leave(bAgent6,room1)),rooms,3) FORMAT
						
						
						//LOGGING  revision start
						//	header ="synthesiser,action,status,insState,timestep\n";
						//Files.write(Paths.get(revisionsFilePath), header.getBytes(),StandardOpenOption.APPEND);
						
						String concat = agName+",\""+atmpt+"\",\""+problem+"\",start,"+LocalTime.now()+","+inst_state+","+RoomEnvironmentLocal_Inst.super.getStep()+"\n";
						try {
							Files.write(Paths.get(revisionsFilePath), concat.getBytes(),StandardOpenOption.APPEND);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						System.out.println("Reason for complaint "+reason+ " what to add "+toAdd);
						
						System.out.println("Checking if the problem has an existing solution");
						
						String revised_file_path = "";
						
						//public String checkExistingMods(String act, String prob)
						//String solution = checkExistingMods(atmpt,problem);
						String solutionFile = checkExistingModsLive(atmpt,problem);
						//if(solution.equals("none"))
						if(solutionFile.equals("none") || solutionFile.length()<1)
						{
							System.out.println("A solution does not currently exists for this action and problem");
							
							System.out.println("Check state "+when+ " and if possible "+ numStates +" states before and after.");

							System.out.println("Revision begins.................");

							
							System.out.println("About to Run pyhton script to create xhail file");
							
							/*
							 * File set up
							 * 
							 */
							String r_path = files_directory+"toRevise_"+agName;
							String m_path = files_directory+"modesX_"+agName;
							String e_path = files_directory+"example"+trace_count+"_X_"+agName;
							String t_path = files_directory+"trace"+trace_count+"_X_"+agName;
							String n_path = files_directory+"narX_"+agName;
							
							try {
								//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/toRevise"), compileXHAIL().getBytes());
								Files.write(Paths.get(r_path), compileXHAIL().getBytes());
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//System.out.println(compileXHAIL());
							System.out.println("Finished Running pyhton script to create xhail asp file");
							
							
							//String modes = new JsonExtractor().getModesFile("/Users/andreasamartin/Documents/InstalExamples/rooms/dict.txt","enter");
							
							//String modesX = new JsonExtractor().getModesFileXhail("/Users/andreasamartin/Documents/InstalExamples/rooms/dict.txt","");
							//final String modesX = new JsonExtractor().getModesFileXhail("/Users/andreasamartin/Documents/InstalExamples/rooms/dict.txt","enter") + "\n\n#modeb holdsat(meeting(+location),$inst, +instant).\n" + 
							//"#modeb not holdsat(meeting(+location), $inst, +instant).\n";

							//final String modesX = new JsonExtractor().getModesFileXhail("/Users/andreasamartin/Documents/InstalExamples/rooms/dict.txt","enter") + modeAdd;
							final String modesX = new JsonExtractor().getModesFileXhail(files_directory+"dict.txt","enter") + modeAdd;

							
							//modesX = modesX + "\n\n#modeb holdsat(meeting(+location),$inst, +instant).\n" + 
							//		"#modeb not holdsat(meeting(+location), $inst, +instant).\n";
															
						//	System.out.println(modes); //just printing for now
							System.out.println("Modes file created");
							
							//writing to the modes file is what is required so that the ILP can access this file
							//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/modes"+trace_count+""), modes.getBytes());
							
							//writing to the modes file is what is required so that the ILP can access this file
							//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/modes"+trace_count+"_X"), modesX.getBytes());
							//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/modes"+trace_count+"_X"), modesX.getBytes());
							Files.write(Paths.get(files_directory+"modes"+trace_count+"_X"), modesX.getBytes());
							
							
							System.out.println("Updating modes file for Xhail");
							
							//String path = "/Users/andreasamartin/Desktop/Sharing_Virtual/modesTemp";
							String path = files_directory+"modesTemp";
							List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
							
							String add = Files.readString(Paths.get("modesappend"), Charset.defaultCharset());
							//String add1 = Files.readString(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/modes"+trace_count+"_X"), Charset.defaultCharset());
							String add1 = Files.readString(Paths.get(files_directory+"modes"+trace_count+"_X"), Charset.defaultCharset());
							
							String content="";
					    	for (String line : lines) {
					    		if(line.contains("<<exception>>"))
					    			content+=add+" \n";
					    		else if(line.contains("<<modes>>"))
					    		{
					    			content+=add1+" \n";
					    		}
					    		else
					    			content+=line+"\n";
					    	}
					
							//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/modesX"), content.getBytes());
							Files.write(Paths.get(m_path), content.getBytes());
							
							//String trace = new JsonExtractor().getTraceFile(when,numStates,stateList);
							
							ArrayList<Object> traceInfo  = new JsonExtractor().getLocalTraceFileXhail(when,numStates,stateList,toAdd,prob,agentsInMAS,agentsBySynthesizerInMAS.get(agName));
							//ArrayList<Object> traceInfo  = new JsonExtractor().getTraceFileXhail(when,numStates,stateList,toAdd,prob,agentsInMAS,agentsBySynthesizerInMAS.get(agName));
							
							
							final String traceX = traceInfo.get(0).toString();
							//Getting this earlier

							int narCount = (int)traceInfo.get(1);
									//new JsonExtractor().getTraceCount();
							final String narUpdate = "final("+narCount+").\n";
							
							
							
							System.out.println("Trace file created");
							//writing to the trace file is what is required so that the ILP can access this file
							//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/trace"+trace_count+".txt"), trace.getBytes());
							
							Files.write(Paths.get(t_path), traceX.getBytes());
							
							//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/trace"+trace_count+"_X.txt"), traceX.getBytes());
							System.out.println("Printing all agents");
							
							//HashMap<String, ArrayList<String>> agentsBySynthesizerInMAS = new HashMap<String, ArrayList<String>>();
							// ArrayList<String> agentsInMAS = new ArrayList<String>();

							/*
							 * Checking for all the agents and then the subset of agents 
							 * It works as expected
							 * 
							 * for (int i = 0; i < agentsInMAS.size();i++) 
						      { 		      
						          System.out.println(agentsInMAS.get(i)); 
						      }
							
							System.out.println("Printing agents related to "+agName);
							ArrayList<String> tempAg = agentsBySynthesizerInMAS.get(agName);
							for (int i = 0; i < tempAg.size();i++) 
						      { 		      
						          System.out.println(tempAg.get(i)); 
						      }
							*/
							
							System.out.println("Retreiving examples definitions from trace data");
							final String examples = traceInfo.get(2).toString();
									//new JsonExtractor().getExampleDefintions();
							//System.out.println("Example definitions are : ////\\\\\\n"+examples);
							
							//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/example"+trace_count+"_X.txt"), examples.getBytes());
							Files.write(Paths.get(e_path), examples.getBytes());
							
							
							System.out.println("Updating narrative file");
							//TRYING TO MOVE THIS
							//int narCount = new JsonExtractor().getTraceCount();
							//String narUpdate = "final("+narCount+").\n";
							String fileContents = Files.readString(Paths.get(files_directory+"narX"), Charset.defaultCharset());
							fileContents = fileContents+"\n"+narUpdate;
							try {
							    //Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/narX"), narUpdate.getBytes(), StandardOpenOption.APPEND);
								//System.out.println("Nar file:\n"+fileContents);
								Files.write(Paths.get(n_path),fileContents.getBytes());
							}catch (IOException e) {
							    //exception handling left as an exercise for the reader
							}
							
							
							//String revision = reviseInstitution("/Users/andreasamartin/Documents/InstalExamples/rooms/trace"+trace_count+".txt","/Users/andreasamartin/Documents/InstalExamples/rooms/modes"+trace_count+"");
								
							trace_count++;
							
							/*XHAIL logic will go here after all the definitions are found
							 * */
							//String xhail_output = "/Users/andreasamartin/Desktop/Sharing_Virtual/out"+inst_state;
							//String revised_output = "/Users/andreasamartin/Desktop/Sharing_Virtual/rooms_revised"+inst_state+".ial";
							String xhail_output = files_directory+"out_"+agName+"_"+inst_state;
							String revised_output = files_directory+"rooms_revised_"+agName+"_"+inst_state+".ial";
							
							revised_file_path = "rooms_revised_"+agName+"_"+inst_state+".ial";
							
							//String out = reviseInstitutionXHAIL("/Users/andreasamartin/Desktop/Sharing_Virtual/trace31", "/Users/andreasamartin/Desktop/Sharing_Virtual/modes", "/Users/andreasamartin/Desktop/Sharing_Virtual/nar312.lp", "/Users/andreasamartin/Desktop/Sharing_Virtual/asp_rev3v3.lp", "/Users/andreasamartin/Desktop/Sharing_Virtual/instalPrelude3.lp", "/Users/andreasamartin/Desktop/Sharing_Virtual/outP_nov22.txt", "/Users/andreasamartin/Documents/InstalExamples/rooms/outDict.txt");
							
							//String out = reviseInstitutionXHAIL("/Users/andreasamartin/Desktop/Sharing_Virtual/trace31", "/Users/andreasamartin/Desktop/Sharing_Virtual/modes", "/Users/andreasamartin/Desktop/Sharing_Virtual/nar312.lp", "/Users/andreasamartin/Desktop/Sharing_Virtual/asp_revRole.lp", "/Users/andreasamartin/Desktop/Sharing_Virtual/instalPrelude3.lp", xhail_output, "/Users/andreasamartin/Documents/InstalExamples/rooms/outDict.txt",revised_output, "/Users/andreasamartin/Documents/InstalExamples/rooms/narX" );

							System.out.println("Started Running python XHAIL Revision process");
							//String out = reviseInstitutionXHAIL(files_directory+"trace"+trace_count+"_X.txt", files_directory+"modesX", files_directory+"example"+trace_count+"_X.txt", files_directory+"toRevise",temp_files_directory+"instalPrelude3.lp", xhail_output, files_directory+"outDict.txt",revised_output,files_directory+"narX");
							String out = reviseInstitutionXHAIL(t_path, m_path, e_path, r_path,temp_files_directory+"instalPrelude3.lp", xhail_output, files_directory+"outDict.txt",revised_output,n_path);

							
							System.out.println("Finished Running python XHAIL Revision process");
							//trace_count++;
							
							System.out.println("----Analysis of xhail console output ----\n"+out);
						
							//CAN THIS BE CHECKED FIRST
							
							//private void reviseInstitutionXHAIL(String tracefile, String modesfile, String examplefile, String aspfile, String preludefile, String output, String dictfile)
							
							
							//String rule_set = "R1;R2;R3;R4;R5;R6;M1R7;R8;R9;R10;R11;R12;R13;R14;R15;R16;R17;R18;R19;R20;R21;R22;R23;R24;R24;R25;R26;R27;R28;R29;R30;R31;R32;R33";
							//String rule_set = instHandle.getRuleSet(problem, atmpt,curInstRuleSet);
							//String str =instHandle.reviseInst(rule_set);
							
							/** the above probably shouldn't happen here, to rethink*/
							File temp = new File(revised_output);
							if(temp.exists())
							{
								if(temp.length()<2)
								{
									//Revision is unsuccessful, no solution
									System.out.println("No solution available currently, empty file from XHAIL output analysis.");
									
									addPercept(agName, Literal.parseLiteral("revisionFailed"));
									status="end_fail";
								}
								else
								{
									if(compareFiles(files_directory+inst_file,revised_output))
									{
										System.out.println("Files are the same, no revision necessary");
										addPercept(agName, Literal.parseLiteral("revisionSuccessful(active)"));
										status="end_active";
									}
									else {
										System.out.println("Files are different, revision successful");
										
										//Revision is successful, otherwise not
										addPercept(agName, Literal.parseLiteral("revisionSuccess"));
										addPercept(agName, Literal.parseLiteral("revisionFile("+revised_file_path+")"));
										
										instFile2Mod = revised_file_path;
										
										int ext = revised_file_path.indexOf(".");
										instRevLog = revised_file_path.substring(0, ext)+"revLog";
										
										addPercept(agName, Literal.parseLiteral("revisionLog("+instRevLog+")"));
										
										
										potentialRevision.put(revised_file_path, instRevLog);
										
										status="end_success";
										
										//temporarily doing this
										//instModList.add(new InstMods(atmpt,revised_file_path,problem));
										
									/*To Move to Oracle action	
									 * if(getDecisionOracle())
										{
											//XHAIL version
											//instModList.add(new InstMods(atmpt,revised_output,problem));
											instModList.add(new InstMods(atmpt,revised_file_path,problem));
											//instFile2Mod = revised_file_path;
											
											System.out.println("Revision approved by Oracle, can be implemented");
											
											addPercept(agName, Literal.parseLiteral("revisionSuccess"));
											
												
											
										}
										else
										{
											System.out.println("Revision not approved by Oracle, will be discarded");
											
											addPercept(agName, Literal.parseLiteral("revisionFailed"));
										}*/
									}
									
								}
							}
							else {
								//Revision is unsuccessful, no solution
								System.out.println("No solution available currently, no output file created from XHAIL.");
								status="end_fail";
								addPercept(agName, Literal.parseLiteral("revisionFailed"));
							}
							
							
							System.out.println("Revision has ended.... Completing action ......");
																

						}
						else
						{
							System.out.println("A solution exists for this problem");
							System.out.println("Checking if the solution is currently in place");

							
							//We need to use the f
							boolean exists = new File(files_directory+solutionFile).exists();
							
							if(exists)
							{
								System.out.println("File paths:\n"+files_directory+inst_file+"\n"+files_directory+solutionFile+"\n End");
								//CAN THIS BE CHECKED FIRST
								if(compareFiles(files_directory+inst_file,files_directory+solutionFile))
								{
									System.out.println("Solution is active");
									addPercept(agName, Literal.parseLiteral("revisionSuccessful(active)"));
									status="end_active";
								}
								else {
									System.out.println("Changing to the existing solution");
									instFile2Mod = solutionFile;
									addPercept(agName, Literal.parseLiteral("revisionSuccess"));
									status="end_exists";
								}
							}
							else
							{
								System.out.println("///The file does not exist, I do not know why the program thinks it does ARGHHH///");
							}
	
						}

						
						RoomEnvironmentLocal_Inst.this.markAsCompleted(action);
						
						
						
						//LOGGING  revision end
						//	header ="synthesiser,action,status,insState,timestep\n";
						//Files.write(Paths.get(revisionsFilePath), header.getBytes(),StandardOpenOption.APPEND);
						
						concat = agName+",\""+atmpt+"\",\""+problem+"\",end,"+LocalTime.now()+","+inst_state+","+RoomEnvironmentLocal_Inst.super.getStep()+"\n";
						try {
							Files.write(Paths.get(revisionsFilePath), concat.getBytes(),StandardOpenOption.APPEND);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};

			Thread t = new Thread(r);
			t.start();

			//addPercept(agName, Literal.parseLiteral("revisionFailed"));
			//inst_state--;
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
			//inst_state--;
		}
		else if (action.getFunctor().equals("skip_steps")) {
			try {
				int len = (int)((NumberTerm) action.getTerm(0)).solve();
				System.out.println("Skipping "+len+" step(s)");
			} catch (NoValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//inst_state--;skip-
			return true;
		}
		else if (action.getFunctor().equals("explore")) {
			System.out.println("Exploring...");
			//inst_state--;
			return true;
		}
		else if (action.getFunctor().equals("wait")) {
			System.out.println("Waiting...");
			//inst_state--;
			return true;
		}
		else if (action.getFunctor().equals("changeInst")) {
			
			String file = (action.getTerm(0)).toString();
			
			/*
			 * It's possible that you have reached here and there is no need to change 
			 * because the file is the same as what is in place so check this first 
			 * only change if different
			 */
			System.out.println("Checking if the solution is currently in place since this is possible");

			
			//We need to use the f
			boolean exists = new File(files_directory+file).exists();
			
			if(exists)
			{
				
				try {
					if(compareFiles(files_directory+inst_file,files_directory+file))
					{
						System.out.println("An identical solution is active, no need to update");				
					}
					else {
						
						
						System.out.println("Setting up new institution:");
						
						String add="";
						
						String act = (action.getTerm(1)).toString();
						
						String issue = (action.getTerm(2)).toString();
						issue = issue.replace("\"","");
						
						if(issue.contains("capacityExceededViol"))
						{
							
							String room=StringUtils.substringBetween(act, ",", ")");
							room = room.replace("\"","");
							
							add = "initially(meeting("+room+"),rooms)\n";
							
							//update institutional modifications with new solution
							instModList.add(new InstMods(act,file,"capacityExceededViol"));
						}
						else if(issue.contains("|"))
						{
							instModList.add(new InstMods(act,file,"earlyBirdViol|additional_perm"));
						}
						else if(issue.contains("earlyBirdViol"))
						{
							instModList.add(new InstMods(act,file,"earlyBirdViol"));
						}
						else if(issue.contains("roomTypeConflictViol"))
						{
							String person=StringUtils.substringBetween(act, "(", ",");
							person = person.replace("\"","");
							
							add = "initially(is_vip("+person+"),rooms)\n";
							
							
							instModList.add(new InstMods(act,file,"roomTypeConflictViol"));
						}
						else if(issue.contains("missing_inroom"))
						{
							//String person=StringUtils.substringBetween(act, "(", ",");
							//person = person.replace("\"","");
							
							//add = "initially(is_vip("+person+"),rooms)\n";
							
							
							instModList.add(new InstMods(act,file,"missing_inroom"));
						}
						else if(issue.contains("deniedEntry"))
						{
							String person=StringUtils.substringBetween(act, "(", ",");
							person = person.replace("\"","");
							
							add = "initially(is_vip("+person+"),rooms)\n";
							
							
							instModList.add(new InstMods(act,file,"deniedEntry"));
						}
						
						
						/*System.out.println("The file to be updated is :"+instFile2Mod);
						if(new File(files_directory+instFile2Mod).exists())
						{
							inst_file = instFile2Mod;  //file
						}*/
						
						
						System.out.println("The file to be updated is : "+file);
						if(new File(files_directory+file).exists())
						{
							inst_file = file;  //file
						}
						
						
						String line = "";
						//String path = "/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf";
						String path = files_directory+"roomsFacts.iaf";
						try {
							
							// Editing the facts file
							List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
					    	for (String line2 : lines) {
					    		//if(!(line2.contains("capacityExceededViol")))
					    		if(!(line2.contains(issue)))	
					    			line+=line2+"\n";

					    	}
							line+=add;
							
							//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), line.toString().getBytes());
							Files.write(Paths.get(files_directory+"roomsFacts.iaf"), line.toString().getBytes());

							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
						
						
						//add a record to the changepoint list
						//instChangePoint.put(inst_state, curInstRuleSet);
						instChangePoint.put(inst_state, file);
						
						//logging the institutional change
						//"synthesiser,filepath,insState,timestep\n";
						String concat = agName+","+(files_directory+file)+","+inst_state+","+super.getStep()+"\n";
						try {
							Files.write(Paths.get(instChangeFilePath), concat.getBytes(),StandardOpenOption.APPEND);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						System.out.println("New institution enabled");
						
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("///The file does not exist, I do not know why the program thinks it does ARGHHH///");
			}
			
			
			
			//inst_state--;
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
			
				addPercept(agent,Literal.parseLiteral("overseer(synthesizer1)"));
				
				//temporarily assigning all agents to the single synthesizer
				addPercept("synthesizer",Literal.parseLiteral("assignee("+agent+")"));
			}
			//initially(perm(enter(agent6,room2)),rooms)
			String line = "";
			//String file = "/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf";
			
			String file = files_directory+"roomsFacts.iaf";
			
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
				//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), line.toString().getBytes());
				Files.write(Paths.get(files_directory+"roomsFacts.iaf"), line.toString().getBytes());
				
				
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
				//file = "/Users/andreasamartin/Documents/InstalExamples/rooms/roomsConf.idc";
				file = files_directory+"roomsConf.idc";
				lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
				line="";
		    	for (String line2 : lines) {
		    		if(line2.contains("Person"))
		    			line+=line2+names+" \n";
		    		else
		    			line+=line2+"\n";

		    	}
		
				//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), add.toString().getBytes(),StandardOpenOption.APPEND);
				//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsConf.idc"), line.toString().getBytes());
				Files.write(Paths.get(files_directory+"roomsConf.idc"), line.toString().getBytes());
				
				
				System.out.println("Agent added");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//inst_state--;
			return true;
		}
		else if (action.getFunctor().equals("updateState")) {
			System.out.println("I will update the fact file if necessary.");
			String act = (action.getTerm(0)).toString();
			
			//String mode = (action.getTerm(1)).toString(); //delete (del) or add
			String room=StringUtils.substringBetween(act, ",", ")");
			room = room.replace("\"","");
			
			String add = "initially(meeting("+room+"),rooms)\n";
			
			String line = "";
			//String path = "/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf";
			String path = files_directory+"roomsFacts.iaf";
			boolean contains = false;
			try {
				
				// Editing the facts file
				List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
		    	for (String line2 : lines) {
		    		if(line2.contains(add))
		    		{
		    			contains = true;
		    		}  
		    		line+=line2+"\n";

		    	}
		    	
		    	if(!contains)
		    	{
		    		line += add;
					
					//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), line.toString().getBytes());
					Files.write(Paths.get(files_directory+"roomsFacts.iaf"), line.toString().getBytes());

				
		    	}
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Fact file updated");
			//inst_state--;
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
		
		//Decided to do this increment only when an action updates the institution, this should solve my problem
		//inst_state++;
		return true; // the action was executed with success
	}
	
	private String compileXHAIL()
	{
	//	String cmd = "python3 /Users/andreasamartin/InstalStable/istable/code/instal-stable/compiler.py compile_ial test.txt";
		//String cmd = "python3 /Users/andreasamartin/Desktop/Sharing_Virtual/compiler.py compile_ial /Users/andreasamartin/Documents/InstalExamples/rooms/outDict.txt";
		
		String cmd = "python3 "+ files_directory+"compiler.py compile_ial "+files_directory+"outDict.txt";
		
		
		//String output;
		try {
			return Processes.runShellCmdRes(cmd);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private boolean compareFiles(String file1_path,String file2_path) throws IOException
	{
		boolean comp = FileUtils.contentEquals(new File(file1_path),new File(file2_path));
		//FileUtils.co
	
		return comp;
	}

	private ArrayList<String> decodeString(String prob, String atmpt, String reason)
	{
		ArrayList<String> response= new ArrayList<String>();;
	
		String toAdd = "";
		String modeToAdd = "";
		String problem = "";
		//get room in both cases "room1" or room 
		//String room = StringUtils.substringBetween(atmpt, "\"", "\"");
		String room = StringUtils.substringBetween(atmpt, ",", ")");
		room = room.replaceAll("\"", "");
		
		String person = StringUtils.substringBetween(atmpt, "(", ",");
		person = person.replaceAll("\"", "");
		
		
		//to handle this type of problem
		//prob				atmpt					reason
		//restrictedAccess, enter(vipAgent,room2), allowedAccess(vip))
		//capacityExceededViol, A, noViol(holdsat(meeting))
		//request(earlyBirdViol, A, noViol(none)
		//request(roomTypeConflictViol, A, noViol(vip))
		
		if(prob.contains("capacity"))
		{
			problem = prob+"("+room+")";
			
			modeToAdd = "\n\n#modeb holdsat(meeting(+location),$inst, +instant).\n" + 
					"#modeb not holdsat(meeting(+location), $inst, +instant).\n";
			
			
			if(reason.contains("(")) {
				//noViol(holdsat(meeting))
				int count = StringUtils.countMatches(reason, "(");
				if(count>1)
				{
					reason = StringUtils.substringBetween(reason, "(", "))");
					//toAdd = reason+"("+room+"),rooms,"+when+")";
					toAdd = reason+"("+room+")";
				}else {
					reason = StringUtils.substringBetween(reason, "(", ")");
					//toAdd = reason+",rooms,"+when+")";
					toAdd = reason;
				}
				
			}	
			
			
			
		}
		else if(prob.contains("restrict"))
		{
			 int i = atmpt.indexOf("(");
			 String temp = atmpt.substring(0,i);
			 problem = atmpt.replace(temp, prob); 
			 toAdd = atmpt.replace(temp, "occurred(arrive"); 
			 problem ="occurred("+problem;
			 //toAdd = toAdd + ")";
			 
			if(reason.contains("vip"))
			{
				modeToAdd = "\n\n#modeb holdsat(is_vip(+person),$inst, +instant).\n" + 
						"#modeb not holdsat(is_vip(+person), $inst, +instant).\n";
			}
		}
		else if(prob.contains("denied"))
		{
			 int i = atmpt.indexOf("(");
			 String temp = atmpt.substring(0,i);
			 problem = atmpt.replace(temp, prob); 
			 //toAdd = atmpt.replace(temp, "occurred(arrive"); 
			 
			 //example occurred(arrive(vipAgent4,room2),rooms,1).
			 toAdd ="holdsat(restrictAccess(";
			 //problem ="occurred("+problem;
			
			 //problem ="occurred("+prob+"(vipAgent";
			 problem ="occurred("+prob;
			 
			 //toAdd = toAdd + ")";
			 
			if(reason.contains("vip"))
			{
				modeToAdd = "\n\n#modeb holdsat(is_vip(+person),$inst, +instant).\n" + 
						"#modeb not holdsat(is_vip(+person), $inst, +instant).\n";
			}
		}
		else if(prob.contains("earlyBird") & prob.contains("additional"))
		{		
			 //perm(enter(Ag,Rom)) 
			problem = prob;//StringUtils.substringBefore(prob,"|")+  "|perm(enter";
			 
			//problem = "perm(leave";
				
			modeToAdd = "\n\n#modeb holdsat(permExit(+person,+location),$inst,+instant).\n";
			
			//toAdd = "holdsat(in_room("+person+","+room+")";	
		}
		else if(prob.contains("earlyBird"))
		{		
			//.send(O,tell,request(earlyBirdViol, A, noViol(none)));		
			
			//do this when not using local search, when using local search, no change required
			 //problem = prob+"("+person+","+room+")";
			 
			 problem = prob;
		}
		else if(prob.contains("TypeConflict"))
		{
			problem = prob+"("+room+")";
			
			if(reason.contains("vip"))
			{
				modeToAdd = "\n\n#modeb holdsat(is_vip(+person),$inst, +instant).\n" + 
						"#modeb not holdsat(is_vip(+person), $inst, +instant).\n";
			}
			
			toAdd = "holdsat(is_vip("+person+")";			
			
		}
		else if(prob.contains("missing"))
		{
			problem = "perm(leave";
			
			modeToAdd = "\n\n#modeh initiated(in_room(+person,+location),+inst,arrive(+person2,+location2),+instant).\n"; 
			//#modeh initiated(in_room(+person,+location),+inst,arrive(+person2,+location2),+instant).

			
			toAdd = "holdsat(in_room("+person+","+room+")";			
			
		}
		
		
		response.add(problem);
		response.add(toAdd);
		response.add(modeToAdd);
		
		return response;
	}
	
	private void Updatefile(String file, String update, String add, String remove)
	{
		//System.out.println("I have trying to update "+file +" by adding "+add + " and removing "+remove);
		
		String line = "";
		try {
			
			// Editing the facts file
			List<String> lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
	    	for (String line2 : lines) {

	    		if(!remove.contentEquals("") && !(line2.contains(remove)))
	    			line+=line2+"\n";
	    		//else
	    		//	System.out.println("I am removing "+line2);
	    	}
	    	if(!add.contentEquals("")) {
	    		//System.out.println("I am adding "+add);
	    		line+=add;
	    	}
	    		
	    	
	    	//Files.write(Paths.get(files_directory+"log"+LocalTime.now()), line.toString().getBytes());
			
	    	//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), add.toString().getBytes(),StandardOpenOption.APPEND);
			
	    	Files.write(Paths.get(file), line.toString().getBytes());
		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Map<String, Literal[]> senseInstitution(String ag, String act, String avoid) {
		Map<String, Literal[]> m = new HashMap<String, Literal[]>();

		System.out.println("Inst file being used is: "+inst_file);
		System.out.println("I am trying to avoid: "+avoid);
		
		boolean viol = false;
		String room = "";
		String temp ="";
		String action = StringUtils.substringBetween(act, "observed(", ")");
		action = action+")";
		System.out.println("I should use action to get room and person: "+action);
		if(avoid.contains("capacity"))
		{
			room=StringUtils.substringBetween(action, ",", ")");
			avoid = avoid + "("+room+ ")";
			//Updatefile("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf","","initially(meeting("+room+"),rooms)\n", "capacityExceededViol");
			Updatefile(files_directory+"roomsFacts.iaf","","initially(meeting("+room+"),rooms)\n", "capacityExceededViol");

		}
		else if(avoid.contains("early"))
		{
			room=StringUtils.substringBetween(action, ",", ")");
			String person = StringUtils.substringBetween(action, "(", ",");
			avoid = avoid + "("+person+","+room+ ")";
			//Updatefile("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf","","", "earlyBirdViol");
			Updatefile(files_directory+"roomsFacts.iaf","","", "earlyBirdViol");

		}
		else if(avoid.contains("roomType"))
		{
			room=StringUtils.substringBetween(action, ",", ")");
			String person = StringUtils.substringBetween(action, "(", ",");
			avoid = avoid + "("+room+ ")";
			//Updatefile("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf","","initially(is_vip("+person+"),rooms)\n", "roomTypeConflictViol");
			Updatefile(files_directory+"roomsFacts.iaf","","initially(is_vip("+person+"),rooms)\n", "roomTypeConflictViol");
			
		}
		else if(avoid.contains("denied"))
		{
			room=StringUtils.substringBetween(action, ",", ")");
			String person = StringUtils.substringBetween(action, "(", ",");
			//System.out.println("person is "+person);
			avoid = "occurred("+avoid + "("+person+","+room+ "))";
			//Updatefile("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf","","initially(is_vip("+person+"),rooms)\n", "restrictAccess("+person+","+room+")");
			Updatefile(files_directory+"roomsFacts.iaf","","initially(is_vip("+person+"),rooms)\n", "restrictAccess("+person+","+room+")");
			
		}
		else if(avoid.contains("|"))
		{
			//avoid = "";	
			room=StringUtils.substringBetween(action, ",", ")");
			String person = StringUtils.substringBetween(action, "(", ",");
			avoid = "earlyBirdViol("+person+","+room+ ")";
			temp = "perm(enter("+person+","+room+ ")";
			//Updatefile("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf","","", "earlyBirdViol");
			Updatefile(files_directory+"roomsFacts.iaf","","", "earlyBirdViol");

			
		}
		
		System.out.println("I am trying to avoid after change: "+avoid);
		//Need to be generic with these updates, using information passed rather than fixed
	/*	if(avoid.contains("capacityExceededViol"))
		{
			Updatefile("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf","","initially(meeting("+room+"),rooms)\n", "capacityExceededViol");

		}
		if(avoid.contains("roomTypeConflictViol"))
		{

			String person = StringUtils.substringBetween(act, "(", ",");
			Updatefile("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf","","initially(is_vip("+person+"),rooms)\n", "roomTypeConflictViol");

		}
		else if(avoid.contains("earlyBirdViol"))
		{
			// Updatefile(String file, String update, String add, String remove)
			Updatefile("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf","","", "earlyBirdViol");

			
		}
		
		*/
		try {
			//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsQuery.iaq"),act.getBytes());
			Files.write(Paths.get(files_directory+"roomsQuery.iaq"),act.getBytes());


			//String cmd = "/usr/local/bin/docker run -v /Users/andreasamartin/Documents/InstalExamples/rooms:/workdir instal-stable solve $* -i /workdir/"+ inst_file +" -f /workdir/roomsFacts.iaf -d /workdir/roomsConf.idc -q /workdir/roomsQuery.iaq -j /workdir/out.json -v";
			String cmd = instal_path+local_inst_path+":/workdir instal-stable solve $*"+ "-i /workdir/"+ inst_file +" -f /workdir/roomsFacts.iaf -d /workdir/roomsConf.idc -q /workdir/roomsQuery.iaq -j /workdir/out.json -v";
			
			
			String output = Processes.runShellCmdRes(cmd);
			
			ArrayList<Literal> inst_sense = new ArrayList<Literal>();
			
			//what occurred this timestep
			//getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","occurred",1);
			getJSONObjectFromFile(files_directory+"out.json","occurred",1);
			
			String occurred = strRet.toString();
		
		//	getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","observed",1);
		//	String observed = strRet.toString();

			System.out.println("Sense - what occurred: "+occurred);
			
			
			//returning what occurred
			for(String s : occurred.split("\n"))
			{
				inst_sense.add(Literal.parseLiteral(s));
				System.out.println("occurred: "+s);
				if(avoid.contains("occurred") && s.contentEquals(avoid))
				{
					viol = true;
				}
			}
			
			if(avoid.contains("occurred"))
			{
				avoid = "Cannotfind";
			}
			//getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","holdsat",1);
			getJSONObjectFromFile(files_directory+"out.json","holdsat",1);

	
			String[] percepts = strRet.toString().split("\n");

			int count = StringUtils.countMatches(strRet.toString(), ag);
			//System.out.println("Count "+count);
			int c=0;
			
			//boolean entered = false;
			
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
					System.out.println("There is a violation");
					viol=true; //indicates a violation of the
				
					inst_sense.add(Literal.parseLiteral(var));
				}
				if(var.contentEquals("in_room"+str))
				//if (var.matches("in_room"+str))
				{
					System.out.println("Found it");
					inst_sense.add(Literal.parseLiteral(var));
					entered=true;
				}
				if(!temp.isBlank() & var.contains(temp))
				{
					viol = true;
				}
//				else
//					System.out.println("Matches didn't work");

				
			}


			//check if agent is in the room first
			if (viol && entered)
			{
				inst_sense.add(Literal.parseLiteral("revisionUnacceptable"));
				
			}
			else if (!viol && !entered)
			{
				inst_sense.add(Literal.parseLiteral("revisionUnacceptable"));
				
			}
			else if (!viol && entered)
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
		
		// Create a HashMap object called violations to store the violations
	    HashMap<String, Boolean> violations = new HashMap<String, Boolean>();

	    // Add keys and values (violation, false)
	    violations.put("capacityExceeded", false);
	    violations.put("earlyBird", false);
	    violations.put("TypeConflict", false);

		try {
			//how to call local instal without files or how to get files from these variables and placed in the appropriate place
			//	Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), strRet.toString().getBytes());
			System.out.println("#######"+current_action+"########");
			
			//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsQuery.iaq"), current_action.getBytes());
			Files.write(Paths.get(files_directory+"roomsQuery.iaq"), current_action.getBytes());

//	    	System.out.println("Query file: ");
//	    	List<String> lines = Files.readAllLines(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsQuery.iaq"), Charset.defaultCharset());
//	    	for (String line : lines) {
//	    	System.out.println("line read: " + line);
//	    	
//	    	}
			
			System.out.println("///Instal file to be run: " + inst_file + "/////");
			
			String fileContents = Files.readString(Paths.get(files_directory+"roomsFacts.iaf"), Charset.defaultCharset());
			
			//String cmd = "/usr/local/bin/docker run -v /Users/andreasamartin/Documents/InstalExamples/rooms:/workdir instal-stable solve $* -i /workdir/"+ inst_file +" -f /workdir/roomsFacts.iaf -d /workdir/roomsConf.idc -q /workdir/roomsQuery.iaq -j /workdir/out.json -v";
			String cmd = instal_path+local_inst_path+":/workdir instal-stable solve $*"+ " -i /workdir/"+ inst_file +" -f /workdir/roomsFacts.iaf -d /workdir/roomsConf.idc -q /workdir/roomsQuery.iaq -j /workdir/out.json -v";
			// /usr/local/bin/docker run -v institution:/workdir instal-stable solve $*  -i /workdir/roomsInst.ial -f /workdir/roomsFacts.iaf -d /workdir/roomsConf.idc -q /workdir/roomsQuery.iaq -j /workdir/out.json -v
			//cmd="pwd";
			//  /Users/andreasamartin/eclipse-workspace/roomsMAS
			
			//System.out.println("CMD----"+cmd);
			//Processes.runShellCmd(cmd);

			String output = Processes.runShellCmdRes(cmd);
			//System.out.println("Command output: " + output);

//			System.out.println("Output file: ");
//	    	lines = Files.readAllLines(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json"), Charset.defaultCharset());
//	    	for (String line2 : lines) {
//	    	System.out.println("line read: " + line2);
//	    	}
			
			if(current_action.contains("leave(baseAgent") || current_action.contains("leave(boldAgent"))
			{
				String test = Files.readString(Paths.get(files_directory+"out.json"), Charset.defaultCharset());
				
				//Files.write(Paths.get(files_directory+"test_"+inst_state), test.toString().getBytes());
				
			}
			
			
			
			//String instalOut = "/Users/andreasamartin/Documents/InstalExamples/rooms/out.json";
			String instalOut = files_directory+"out.json";
			
			
			//what occurred this timestep
			//getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","occurred",1);
			//String occurred = strRet.toString();
			
			String occurred = getJSONObjectFromFileStringRtr(instalOut,"occurred",1);
			
			
			//System.out.println("\nwhat occurred "+strRet.toString());
			
			
			//getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","observed",1);
			//String observed = strRet.toString();
			
			String observed = getJSONObjectFromFileStringRtr(instalOut,"observed",1);
			//System.out.println("\nwhat is observed "+strRet.toString());

			//get the new facts sorted and saved to file for next run
			//getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","holdsat",0);
			String stateFacts = getJSONObjectFromFileStringRtr(instalOut,"holdsat",0);
			
			// ### I'm suspicious about what facts are being stored so I am trying this.
			
			/*The facts that should be stored for each state is not the resulting facts but the initial facts*/
			//stateList.put(inst_state, new State(occurred,observed,stateFacts));
			//String stateToCheck = "State "+inst_state+"\n"+occurred+"\n"+observed+"\n"+current_action+"\n"+stateFacts+"\n/////////\n";
			
			stateList.put(inst_state, new State(occurred,observed,fileContents));
			String stateToCheck = "State "+inst_state+"\n"+occurred+"\n"+observed+"\nAction attempted:"+current_action+"\n"+fileContents+"\n/////////\n";
			
			String stateToCheck2 = "State "+inst_state+"\n"+occurred+"\n"+observed+"\nAction attempted:"+current_action+"\n"+stateFacts+"\n/////////\n";
			
			
			//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/StateCheck"), stateToCheck.toString().getBytes(),StandardOpenOption.APPEND);
			//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/StateCheck2"), stateToCheck2.toString().getBytes(),StandardOpenOption.APPEND);

			
			//get feedback for the agents as percepts
			//System.out.println("\npotentially agent output "+strRet.toString());
			//getJSONObjectFromFile("/Users/andreasamartin/Documents/InstalExamples/rooms/out.json","holdsat",1);
			
			String stateFactsAgents = getJSONObjectFromFileStringRtr(instalOut,"holdsat",1);
			
			//add to the collection of states after each agent's action
			//stateList.put(inst_state, new State(occurred,observed,facts_store.get(inst_state-1)));

			//stateList.put(inst_state, new State(occurred,observed,factsPerState.get(inst_state-1)));

			
			//factsPerState
			//String[] percepts = strRet.toString().split("\n");
			String[] percepts = stateFactsAgents.split("\n");

			//System.out.println("\npotentially agent output Begin \n");
			//System.out.println(percepts.toString());
			//int count = StringUtils.countMatches(strRet.toString(), ag);
			int count = StringUtils.countMatches(stateFactsAgents, ag);
			//System.out.println("Count "+count);
			int c=0;
			boolean viol = false;
			Literal[] inst_sensors = new Literal[count+1]; //add +1 to array size if capacity percept being added
			
			ArrayList<Literal> inst_sense = new ArrayList<Literal>();
			
			String roomCap="";
			ArrayList<String> issueRooms = new ArrayList<String>();
			String inroom="";
			boolean entered = false;
			for (String var : percepts)
			{
				//System.out.println("PERCEPTS - "+var);
				if(var.contains("capacityExceeded"))
				{
					violations.replace("capacityExceeded",true);
					viol=true; //indicates a violation of the
					roomCap=var; //stores the entire percept, maybe just store the name by putting the line from below
					issueRooms.add(var);
				}
				if (var.contains(ag) && !var.matches("(.*)"+ag+"[0-9]+(.*)"))
				{
					//System.out.println("PERCEPTS - "+var);
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
				
				if(var.contains("earlyBird") && var.contains(ag))
				{
					violations.replace("earlyBird",true);
				}
				if(var.contains("TypeConflict"))
				{
					violations.replace("TypeConflict",true);
					issueRooms.add(var);
				}
				
			}

			//checking if the capacity exceeded fluent is set and sets agent percept if they are in the room
			
			if(violations.get("capacityExceeded"))
			{
				for(String str : issueRooms)
				{
					if(str.contains("capacityExceeded"))
					{
						roomCap=StringUtils.substringBetween(str, "(", ")"); //find the name of the room which has exceeded capacity
						
						
						if(inroom.contains(roomCap)) //check if this agent is in the room
						{
							inst_sensors[c] = Literal.parseLiteral("roomCapacityExceeded");
							inst_sense.add(Literal.parseLiteral("roomCapacityExceeded"));
						}
						else
						{
							inst_sensors[c] = Literal.parseLiteral("roomCapacityOkay");
							//inst_sensors[c] = Literal.parseLiteral(roomCap);
							//inst_sense.add(Literal.parseLiteral(roomCap));
							//if(ag.equals("baseAgent"))
							//inst_sense.add(Literal.parseLiteral("capacityExceededFor("+roomCap+")"));
							//inst_sense.add(Literal.parseLiteral("houstonWeHaveAProblem"));
						}
						
						inst_sense.add(Literal.parseLiteral("capacityExceededFor("+roomCap+")"));
					}
					
				}
				
				
			}
			else if (violations.get("earlyBird"))
			{
				inst_sense.add(Literal.parseLiteral("earlyBirdViol"));
			}
			else if (violations.get("TypeConflict"))
			{
				for(String str : issueRooms)
				{
					if(str.contains("TypeConflict"))
					{
						roomCap=StringUtils.substringBetween(str, "(", ")"); //find the name of the room which has exceeded capacity
						
						
						if(inroom.contains(roomCap)) //check if this agent is in the room
						{
							//inst_sensors[c] = Literal.parseLiteral("typeConflictInRoom");
							inst_sense.add(Literal.parseLiteral("typeConflictInRoom"));
						}
						
						inst_sense.add(Literal.parseLiteral("typeConflictFor("+roomCap+")"));
					}
					
				}
			}

			//check if agent is in the room first
			/*if (viol)
			{
				//need to iterate all rooms with capacity problem if there is a violation to see if 
				// the current agent needs the capacityExceeded percept to be added
				
				for(String str : issueRooms)
				{
					roomCap=StringUtils.substringBetween(str, "(", ")"); //find the name of the room which has exceeded capacity
					
					
					if(inroom.contains(roomCap)) //check if this agent is in the room
					{
						inst_sensors[c] = Literal.parseLiteral("roomCapacityExceeded");
						inst_sense.add(Literal.parseLiteral("roomCapacityExceeded"));
					}
				
				}
				
				inst_sense.add(Literal.parseLiteral("capacityExceededFor("+roomCap+")"));

					
			}else
			{
				inst_sensors[c] = Literal.parseLiteral("roomCapacityOkay");
			}
			*/
			
			//check if the agent wasn't allowed in the room
			//could be problematic given array size, maybe use an array list then turn it into an arrray. 
//			if (!(var.contains("in_room")) && current_action.contains("enter"))
//				inst_sensors[c] = Literal.parseLiteral("roomCapacityOkay");
			//System.out.println("What is happening /// "+entered);
			//
			//if (!entered && current_action.contains("enter") && !occurred.contains("restrictedAccess"))
			if (!entered && current_action.contains("enter") && !occurred.contains("deniedEntry"))
			{
				//deniedEntry(P,L)
				inst_sense.add(Literal.parseLiteral("prob(enter)"));
				
			}
			
			
			//check if agent was denied entry because of vip room, I know this is the only reason for now
			if (!entered && occurred.contains("deniedEntry") && current_action.contains("enter"))
			{
				inst_sense.add(Literal.parseLiteral("deniedEntry(vip_room)"));
				
			}
			
			/*if (!entered && occurred.contains("restrictedAccess") && current_action.contains("enter"))
			{
				inst_sense.add(Literal.parseLiteral("restrictedAccess(vip_room)"));
				
			}
			*/
			
			//inst_sense.add(Literal.parseLiteral("extraPercept"));
			
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
			jason.mas2j.MAS2JProject project = new jason.mas2j.MAS2JProject().parse(mas2jfile);

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
		StringBuilder includes = new StringBuilder();
		
		ArrayList<String> facts = new ArrayList<String>();
		int count=0;
		config.append("Person: ");
		strRet.append("\n");
		
		//includes.append("\nlocation(room1).\nlocation(room2).\nlocation(room3).\n"+
		includes.append("\nlocation(room1).\nlocation(room2).\n"+
				"holdsat(max(room1,2),rooms,0).\n" +
				"holdsat(max(room2,2),rooms,0).\n");   // +
				//"holdsat(max(room3,2),rooms,0).\n" +
				//"holdsat(vip_room(room1),rooms,0).\n" +
				//"holdsat(vip_room(room2),rooms,0).\n");
		
		strRet.append("initially(max(room1,2),rooms)\n" +
				"initially(max(room2,2),rooms)\n");   //+
				//"initially(max(room3,2),rooms)\n" +
				//"initially(vip_room(room1),rooms)\n" +
				//"initially(vip_room(room2),rooms)\n");
		
		ArrayList<String> allAgents = new ArrayList<String>();
		//agentsBySynthesizerInMAS
		//agentsInMAS
		int countSynthesisers =0;
		for (String ag : getNamesAgents()) {
			agentsInMAS.add(ag);
			if(ag.contains("synthesizer"))
			{
				agentsBySynthesizerInMAS.put(ag,new ArrayList<String>());
			}
				
		}
		for (String ag : getNamesAgents()) {
			config.append(ag + " ");
			includes.append("person("+ag + ").\n");
			if(count%2==0)
			{
				//facts.add("initially(role("+ag+", x), rooms)");
				strRet.append("initially(role("+ag+", x), rooms)\n");

				includes.append("holdsat(role("+ag+",x),rooms,0).\n");
				
				addPercept(ag,Literal.parseLiteral("role(x)"));
				addPercept(ag,Literal.parseLiteral("bold("+count+")"));
			}else
			{
				//facts.add("initially(role("+ag+", y), rooms)");
				strRet.append("initially(role("+ag+", y), rooms)\n");
				
				includes.append("holdsat(role("+ag+",y),rooms,0).\n");
				
				addPercept(ag,Literal.parseLiteral("role(y)"));
				addPercept(ag,Literal.parseLiteral("bold("+count+")"));
			}
			count++;
			//addPercept(ag,Literal.parseLiteral("overseer(synthesizer)"));
			
			//identifying overseer and assignee at startup so agents can know who to contact
			if(!(ag.contains("synthesizer")) && !(ag.contains("oracle")) && !(ag.contains("coordinator")))
			{
				if(count%2==0)
				{
				//temporarily setting all agents' overseer to the single synthesizer.
				addPercept(ag,Literal.parseLiteral("overseer(synthesizer1)"));
				agentsBySynthesizerInMAS.get("synthesizer1").add(ag);
				
				//temporarily assigning all agents to the single synthesizer
				//addPercept("synthesizer1",Literal.parseLiteral("assignee("+ag+")"));
				}
				else
				{
					//temporarily setting all agents' overseer to the single synthesizer.
					addPercept(ag,Literal.parseLiteral("overseer(synthesizer2)"));
					agentsBySynthesizerInMAS.get("synthesizer2").add(ag);
					//temporarily assigning all agents to the single synthesizer
					//addPercept("synthesizer2",Literal.parseLiteral("assignee("+ag+")"));
					
				}
			}
			if(ag.contains("vip"))
			{
				strRet.append("initially(is_vip("+ag+"), rooms)\n");
				
				includes.append("holdsat(is_vip("+ag+"),rooms,0).\n");
				
				addPercept(ag,Literal.parseLiteral("is_vip(me)"));
				
			}

		}
		//agentsInMAS
		config.append("\nRole: x y\n" +
				//"Location: room1 room2 room3\n" +
				"Location: room1 room2\n" +
				"Number: 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20");
		


		try {
			//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsConf.idc"), config.toString().getBytes());
			//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), strRet.toString().getBytes());
			Files.write(Paths.get(files_directory+"roomsConf.idc"), config.toString().getBytes());
			Files.write(Paths.get(files_directory+"roomsFacts.iaf"), strRet.toString().getBytes());
			
			
			//String path = "/Users/andreasamartin/Desktop/Sharing_Virtual/narTemp";
			String path = files_directory+"narTemp";
			List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
			
			String content="";
	    	
			for (String line : lines) {
	    		if(line.contains("<<include>>"))
	    			content+=includes.toString()+" \n";
	    		else
	    			content+=line+"\n";
	    	}
			//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/narX"), content.getBytes());
			Files.write(Paths.get(files_directory+"narX"), content.getBytes());
			

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
	
	private String checkExistingModsLive(String act, String prob)
	{
		prob=prob.replace("\"", "");
		String ret="none";
		for(InstMods ins: instModList)
		{
			String ac = ins.getAction();
			if(StringUtils.substringBefore(ac,"(").equals(StringUtils.substringBefore(act,"(")))
			{
				if(ins.getProblem().equals(prob))
					return ins.getInstFile();
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
	
	private String reviseInstitutionXHAIL(String tracefile, String modesfile, String examplefile, String aspfile, String preludefile, String output, String dictfile, String revise, String narrativefile)
	{
		/*
		 * String out = reviseInstitutionXHAIL(files_directory+"trace"+trace_count+"_X.txt", files_directory+"modesX", files_directory+"example"+trace_count+"_X.txt", files_directory+"toRevise",temp_files_directory+"instalPrelude3.lp", xhail_output, files_directory+"outDict.txt",revised_output,files_directory+"narX");
							
		 * reviseInstitutionXHAIL("/Users/andreasamartin/Desktop/Sharing_Virtual/trace31", "/Users/andreasamartin/Desktop/Sharing_Virtual/modes", "/Users/andreasamartin/Desktop/Sharing_Virtual/nar312.lp", "/Users/andreasamartin/Desktop/Sharing_Virtual/asp_revRole.lp", "/Users/andreasamartin/Desktop/Sharing_Virtual/instalPrelude3.lp", xhail_output, "/Users/andreasamartin/Documents/InstalExamples/rooms/outDict.txt",revised_output, "/Users/andreasamartin/Documents/InstalExamples/rooms/narX" );
							
		  reviseInstitutionXHAIL(files_directory+"trace"+trace_count+"_X.txt", files_directory+"modesX", "/Users/andreasamartin/Desktop/Sharing_Virtual/nar312.lp", files_directory+"toRevise",temp_files_directory+"instalPrelude3.lp", xhail_output, files_directory+"outDict.txt",revised_output,files_directory+"narX");
							
		 */
		
		System.out.println(("Run XHAIL to commence revision on input files...."));
		
		//String cmd = "python3 /Users/andreasamartin/Desktop/Sharing_Virtual/xhail.py "+aspfile+ " "+ preludefile+ " "+ tracefile+ " "+ modesfile+ " "+examplefile+ " "+narrativefile+ " > "+output ;
		String cmd = "python3 " +files_directory+"xhail.py "+aspfile+ " "+ preludefile+ " "+ tracefile+ " "+ modesfile+ " "+examplefile+ " "+narrativefile+ " > "+output ;
		
		
		String processOut="";
		try {
			processOut = Processes.runShellCmdRes(cmd) + "\n";
			//return Processes.runShellCmdRes(cmd);
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(("XHAIL revision complete. Analyse results...."));
		
		//cmd = "python3 /Users/andreasamartin/Desktop/Sharing_Virtual/analyseXhail.py"+" -i  /Users/andreasamartin/Desktop/Sharing_Virtual/rooms_testaug11.ial -d "+dictfile+" -x "+output+ " -o " + revise;
		cmd = "python3 /Users/andreasamartin/Desktop/Sharing_Virtual/analyseXhail.py"+" -i "+(files_directory+inst_file) + " -d "+dictfile+" -x "+output+ " -o " + revise;
				
		try {
			processOut += Processes.runShellCmdRes(cmd);
			//return Processes.runShellCmdRes(cmd);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(("Analysis complete. Updated revision file created...."));
		/*
		 * 

#subprocess.call("python3 analyseXhail.py"+" -i /Users/andreasamartin/Documents/InstalExamples/rooms/rooms_testaug11.ial"+" -d /Users/andreasamartin/Documents/InstalExamples/rooms/outDict.txt"+" -x out3"+ " -o rooms_revised.ial", shell=True)
subprocess.call("python3 analyseXhail.py"+" -i rooms_testaug11.ial -d "+dictfile+" -x "+xhailoutfile+ " -o rooms_revised.ial", shell=True)

print("Analysis complete.")
		 * */
		
		
		return processOut;
		
	}
	
	private boolean getDecisionOracle()
	{
		return true;
	}
	
	private boolean getDecisionOracleLive(String filePath)
	{
		boolean approve = true;
		String line = "";
		String path = files_directory+ filePath;
		
		String oracleFilePath = domainConf.get("oraclefile").toString();
		try {
			
			
			List<String> lines = Files.readAllLines(Paths.get(oracleFilePath), Charset.defaultCharset());
			String firstLine = Files.readAllLines(Paths.get(path), Charset.defaultCharset()).get(0);
			//String [] multiple = firstLine.split("|");
			//if(multiple.length>1)
			if(firstLine.contains("|"))
			{
				String [] multiple = firstLine.split("|");
				for (String line1 : multiple) {
		    		
		    		if((lines.contains(line1)))
		    		{
		    			approve = false;
		    			break;
		    		}
		    		if(line1.contains("deleteRule") && lines.contains("deleteRule(all)"))
		    		{
		    			approve = false;
		    			break;
		    		}

		    	}
				
			}
			else
			{
				if((lines.contains(firstLine)))
	    			approve = false;
				else if(firstLine.contains("deleteRule") && lines.contains("deleteRule(all)"))
	    		{
	    			approve = false;

	    		}
			}
			
	    	/*for (String line2 : lines) {
	    		//simple for now
	    		if((line2.contains("deleteRule")))
	    			approve = false;

	    	}*/

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return approve;
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
					String str = new JsonExtractor().extract(ob.get(i),sbb).toString();
					str = new JsonExtractor().parseStr(str,flag);
					
					//System.out.println("before: "+str+"\n");
					str = str.replaceFirst("\\(","");
					
					if(str.chars().filter(ch -> ch == '(').count() > str.chars().filter(ch -> ch == ')').count() )
					{
						str =str+")";
					}
					
					//System.out.println("after: "+str+"\n");
					strRet.append(str+"\n");

					//System.out.println(request+": "+new JsonExtractor().parseStr(str,flag)+"\n");
					//System.out.println(request+": "+str+"\n");
				}
			}
			else if(obj instanceof JSONObject)
			{
				JSONObject ob = (JSONObject)state.get("holdsat");
				JSONArray ar = new JsonExtractor().extractHoldsat(ob);
				for(int i=0;i<ar.length();i++)
				{

					JSONArray arr = (JSONArray)ar.get(i);
					for(int j=0;j<arr.length();j++)
					{
						StringBuilder sbb = new StringBuilder("");

						//System.out.println(new JsonExtractor().extract(arr.get(j),sbb).toString());
						String str = new JsonExtractor().extract(arr.get(j),sbb).toString();
						strRet.append(new JsonExtractor().parseStr(str,flag)+"\n");

						//System.out.println(new JsonExtractor().parseStr(str,flag));

					}


				}
				if (flag==0)
				{
					//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), strRet.toString().getBytes());
					Files.write(Paths.get(files_directory+"roomsFacts.iaf"), strRet.toString().getBytes());
					
					//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts1.txt"), strRet.toString().getBytes());
					facts_store.add(strRet.toString());
					
					/*changed to this structure hashmap because I believe the inst_state key is causing some issues 
					 * with the strange events occurring
					 * I had to add 1 to the inst_state as well because I believe that the state I am going to search for will be
					 * state minus one of what it should be
					 * I am not 100% positive at this time. ARGGHHHHH
					*/
					factsPerState.put(inst_state, strRet.toString());
					
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
	
	/*A variation of the method that returns a string rather than using the publically accessible stringbuilder*/
	public String getJSONObjectFromFileStringRtr(String file,String request,int flag)
	{
		StringBuilder retStr=new StringBuilder();
		try {
			
			JSONTokener token = new JSONTokener(new FileReader(file));

			JSONArray jArr = new JSONArray (token);
			JSONObject object = (JSONObject)jArr.get(1);


			JSONObject state = (JSONObject) object.get("state");

			Object obj = (Object)state.get(request);
			if (obj instanceof JSONArray)
			{
				JSONArray ob = (JSONArray)obj;

				for(int i=0;i<ob.length();i++)
				{
					StringBuilder sbb = new StringBuilder("");
					String str = new JsonExtractor().extract(ob.get(i),sbb).toString();
					str = new JsonExtractor().parseStr(str,flag);
					
					str = str.replaceFirst("\\(","");
					
					if(str.chars().filter(ch -> ch == '(').count() > str.chars().filter(ch -> ch == ')').count() )
					{
						str =str+")";
					}
					
					retStr.append(str+"\n");

				}
			}
			else if(obj instanceof JSONObject)
			{
				JSONObject ob = (JSONObject)state.get("holdsat");
				JSONArray ar = new JsonExtractor().extractHoldsat(ob);
				for(int i=0;i<ar.length();i++)
				{

					JSONArray arr = (JSONArray)ar.get(i);
					for(int j=0;j<arr.length();j++)
					{
						StringBuilder sbb = new StringBuilder("");
						
						String str = new JsonExtractor().extract(arr.get(j),sbb).toString();
						retStr.append(new JsonExtractor().parseStr(str,flag)+"\n");

					}


				}
				if (flag==0)
				{
					//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts.iaf"), retStr.toString().getBytes());
					Files.write(Paths.get(files_directory+"roomsFacts.iaf"), retStr.toString().getBytes());
					//Files.write(Paths.get("/Users/andreasamartin/Documents/InstalExamples/rooms/roomsFacts1.txt"), strRet.toString().getBytes());
					facts_store.add(retStr.toString());
					
					/*changed to this structure hashmap because I believe the inst_state key is causing some issues 
					 * with the strange events occurring
					 * I had to add 1 to the inst_state as well because I believe that the state I am going to search for will be
					 * state minus one of what it should be
					 * I am not 100% positive at this time. ARGGHHHHH
					*/
					factsPerState.put(inst_state, retStr.toString());
					
				}

			}
			else
				System.out.println("ERROR - no JSON in file");

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return retStr.toString();

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
