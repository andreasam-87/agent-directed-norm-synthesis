package instal;

import java.util.*;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

public class InstalModel {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	String base_url = "http://127.0.0.1:5000"; //need to make this reasonable
    int model_id;
    
    public InstalGrounding newGrounding(Map<String, String[]> types, String[] facts) {
        return new InstalGrounding(this, types, facts);
    }
    
    public InstalModel(String url) {
    	String[] url_lines = url.split("/");
    	base_url = "http://"+url_lines[2];
    	model_id = Integer.parseInt(url_lines[4]);
    }
   
    public InstalModel(String[] institutions, String[] logic_programs, String[] bridges, String[] facts) {
        // POST /model/

        // Sets model_id to the id returned
    	OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, InstalModel.makeJSON(institutions, logic_programs, bridges, facts));
        Request request = new Request.Builder()
            .url(base_url + "/model/")
            .post(body)
            .build();
        try (Response response = client.newCall(request).execute()) {
        	JSONObject json = new JSONObject(response.body().string());
        	//System.out.println(json);
        	model_id = json.getInt("id");
        } catch (Exception e) {
        	System.out.println("Problem with POST /model/");
        	System.out.println(e);
        }
    }
    public InstalModel(String[] institutions, String[] logic_programs, String[] bridges) {
    	this(institutions, logic_programs, bridges, new String[] {});
    }
    
    public static String makeJSON(String[] institutions, String[] logic_programs, String[] bridges, String[] facts)
    {
    	// TODO
    	JSONObject j = new JSONObject();
    	j.put("institutions",institutions);
    	j.put("logic_programs", logic_programs);
    	j.put("bridges", bridges);
    	j.put("facts",facts);
    	return j.toString();
    }


    public String get_url() {
        return base_url + "/model/" + model_id + "/";
    }
}