import org.json.JSONArray;
import org.json.JSONObject;


public class State{
	int id;
	JSONArray occurred;
	JSONObject holdsat; 
	String occurredStr;
	String holdsatStr; 
	
	public State(int id,JSONArray occurred,JSONObject holdsat)
	{
		this.id = id;
		this.occurred = occurred;
		this.holdsat = holdsat;
		
	}
	
	public State(int id,String occurred,String holdsat)
	{
		this.id = id;
		occurredStr = occurred;
		holdsatStr = holdsat;
		
	}
	
	public State(String occurred,String holdsat)
	{
		occurredStr = occurred;
		holdsatStr = holdsat;
		
	}
	
	public State(JSONArray occurred,JSONObject holdsat)
	{
		this.occurred = occurred;
		this.holdsat = holdsat;
		
	}
	
	public JSONArray getEvents()
	{
		return occurred;
	}
	
	public JSONObject getFacts()
	{
		return holdsat;
	}
	
	public String getEventsSre()
	{
		return occurredStr;
	}
	
	public String getFactsStr()
	{
		return holdsatStr;
	}
	
}