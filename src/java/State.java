import org.json.JSONArray;
import org.json.JSONObject;


public class State{
	int id;
	JSONArray occurred;
	JSONObject holdsat; 
	
	
	public State(int id,JSONArray occurred,JSONObject holdsat)
	{
		this.id = id;
		this.occurred = occurred;
		this.holdsat = holdsat;
		
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
	
	
}