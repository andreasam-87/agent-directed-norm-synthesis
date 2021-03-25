import org.json.JSONArray;
import org.json.JSONObject;


public class State{
//	int instId=0;
	int id;
	JSONArray occurred;
	JSONArray observed;
	JSONObject holdsat; 
	String occurredStr;
	String observedStr;
	String holdsatStr; 
	
	public State(int id,JSONArray occurred,JSONArray observed,JSONObject holdsat)
	{
		this.id = id;
		this.occurred = occurred;
		this.observed = observed;
		this.holdsat = holdsat;
		
	}
	
	public State(int id,String occurred,String observed,String holdsat)
	{
		this.id = id;
		occurredStr = occurred;
		observedStr = observed;
		holdsatStr = holdsat;
		
	}
	
	public State(String occurred,String observed,String holdsat)
	{
		occurredStr = occurred;
		observedStr = observed;
		holdsatStr = holdsat;
		
	}
	
	public State(JSONArray occurred,JSONArray observed,JSONObject holdsat)
	{
		this.occurred = occurred;
		this.observed = observed;
		this.holdsat = holdsat;
		
	}

//	public void setInstId(int id)
//	{
//		instId = id;
//	}
//	
//	public int getInstId()
//	{
//		return instId;  
//	}
	
	public JSONArray getEvents()
	{
		//include the observed array too
		return occurred;
	}
	
	public JSONObject getFacts()
	{
		return holdsat;
	}
	
	public String getEventsStr()
	{
		return occurredStr+observedStr;
	}
	
	public String getFactsStr()
	{
		return holdsatStr;
	}
	
}