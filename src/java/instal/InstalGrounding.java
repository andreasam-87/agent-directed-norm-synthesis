package instal;

import java.util.*;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class InstalGrounding {
    public InstalModel model;
    int grounding_id;
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	public InstalGrounding(String url) {
    	String[] url_lines = url.split("/");
		grounding_id = Integer.parseInt(url_lines[6]);
		model = new InstalModel(url);
	}
	
    public InstalGrounding(InstalModel model, Map<String, String[]> types, String[] facts)
    {
        // POSTS /model/{model.model_id}/grounding/

        // sets grounding_id to the id returned
    	this.model = model;
    	OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, InstalGrounding.makeJSON(types, facts));
        Request request = new Request.Builder()
            .url(model.get_url() + "grounding/")
            .post(body)
            .build();
        try (Response response = client.newCall(request).execute()) {
        	JSONObject json = new JSONObject(response.body().string());
        	//System.out.println(json);
        	grounding_id = json.getInt("id");
        } catch (Exception e) {
        	System.out.println("Problem with POST /model/n/grounding/");
        	System.out.println(e);
        }
    }
    
    public InstalModel getModel() {
    	return model;
    }
    
    public static String makeJSON(Map<String, String[]> types, String[] facts)
    {
    	// TODO
    	JSONObject j = new JSONObject();
    	j.put("facts",facts);
    	j.put("types", types);
    	return j.toString();
    }

    public InstalQuery newQuery(String[] facts, String[] query) {
        return new InstalQuery(this, facts, query);
    }



    public String get_url() {
        return model.get_url() + "grounding/" + grounding_id + "/";
    }
}