import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;



public class Couch {

    public static enum Relation {
        Equal ("$eq"),
        NotEqual ("$ne"),
        GreaterThan ("$gt"),
        LessThan("$lt");

        private String str_val;

         Relation (String str) {
            str_val = str;
        }

        @Override
        public String toString() {
             return str_val;
        }
    }

    // Make gettters and setters if you need them....
    private static Couch instance;
    private static String serverAddress = "localhost";
    private static int port =  5984;
    private static String db = "demo";
    private static String protocol = "http";
    private static String user = "admin";
    private static String password = "admin";

    private HttpClient client;
    private CredentialsProvider provider;
    private HttpClientContext context;

    private final String getUrl;
    private final String postUrl;
    private final String findUrl;

    public static Couch getInstance() {
        if (instance == null) {
            instance =  new Couch();
        }
        return instance;
    }


    private Couch () {
        // Basic auth reference: https://www.baeldung.com/httpclient-4-basic-authentication

        getUrl = protocol + "://" +  serverAddress + ":" + port + "/" + db + "/_all_docs";
        postUrl = protocol + "://" +  serverAddress + ":" + port + "/" + db;
        findUrl = protocol + "://" +  serverAddress + ":" + port + "/" + db + "/_find";

        HttpHost targetHost = new HttpHost(serverAddress, port, protocol);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(user, password));

        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());
        context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);


        client = HttpClientBuilder.create().build();

    }


    public boolean storeData (HashMap<String, String> data) {
        // rather than use a hashmap, you can overload this function to create a json payload
        String lazyJson = "{ ";
        for (String key : data.keySet()) {
            lazyJson =  lazyJson +  "\"" + key + "\": " +  "\"" +  data.get(key) + "\", ";
        }
        lazyJson = lazyJson.substring(0, lazyJson.length() -2) + "}";
        try {
            HttpPost post =  new HttpPost(postUrl);
            post.setEntity(new StringEntity(lazyJson,"application/json",
                    "UTF-8"));

            HttpResponse response = client.execute(
                    post, context
            );
            return response.getStatusLine().getStatusCode() == 201;
        }catch (Exception ex) {

        }
        return false;
    }

    public String findData (String key, String value, Relation relation) {

        String lazyJson = "{ \"selector\": { \"" +  key + "\": { \"" +  relation.toString() + "\": \"" + value + "\" }}}";
        System.out.println(lazyJson);

        try {
            HttpPost post =  new HttpPost(findUrl);
            post.setEntity(new StringEntity(lazyJson,"application/json",
                    "UTF-8"));

            HttpResponse response = client.execute(
                    post, context
            );
            return  EntityUtils.toString(response.getEntity());
        }catch (Exception ex) {

        }



        return null;
    }

    public String getData() {
        try {
            HttpResponse response = client.execute(
                    new HttpGet(getUrl), context
            );
            //System.out.println("Resp code: " +  response.getStatusLine().getStatusCode());
            // you'll need to parse this and extract the contents
            return  EntityUtils.toString(response.getEntity());
        }catch (Exception ex) {

        }
        return null;
    }





}

