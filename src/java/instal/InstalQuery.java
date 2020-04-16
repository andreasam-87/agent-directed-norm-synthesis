package instal;

import java.util.Map;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InstalQuery {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public InstalGrounding grounding;
    int query_id;

    public InstalQuery(InstalGrounding grounding, String[] facts, String[] query)
    {
        // POST to /model/{grounding.model.model_id}/grounding/{grounding.grounding_id}/query/

        // Sets query_id to the query_id returned
    	this.grounding = grounding;
    	OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, InstalQuery.makeJSON(facts, query));
        Request request = new Request.Builder()
            .url(grounding.get_url() + "query/")
            .post(body)
            .build();
        try (Response response = client.newCall(request).execute()) {
        	JSONObject json = new JSONObject(response.body().string());
        	//System.out.println(json);
        	query_id = json.getInt("id");
        } catch (Exception e) {
        	System.out.println("Problem with POST /model/n/grounding/n2/query/");
        	System.out.println(e);
        }
    }
    
    public static String makeJSON(String[] facts, String[] query)
    {
    	// TODO
    	JSONObject j = new JSONObject();
    	j.put("facts", facts);
    	j.put("query", query);
    	return j.toString();
    }
    
    int TIMES_TO_TRY = 1000;
    int TIMEOUT = 100;
    public JSONObject getQueryOutput(){
    	int times = TIMES_TO_TRY;
        JSONObject json = null;
    	OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(get_url())
            .get()
            .build();
    	while (times > 0) {
	    	// loops getting then returns the json output of the query
    		times -= 1;
	        try (Response response = client.newCall(request).execute()) {
	        	json = new JSONObject(response.body().string());
	        	query_id = json.getInt("id");
	        } catch (Exception e) {
	        	System.out.println("Problem with GET query");
	        	System.out.println(e);
	        }
	        if (json.getString("status").equals("running")) {
	        	try {
					Thread.sleep(TIMEOUT);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	continue;
	        } else {
	        	break;
	        }
	        
    	}
    	if (!json.getString("status").equals("complete"))
    	{
    		System.out.println("Error getting query "+this.query_id);
    	}
        return json;
    }

    public String get_url() {
        return grounding.get_url() + "query/" + query_id + "/";
    }
}