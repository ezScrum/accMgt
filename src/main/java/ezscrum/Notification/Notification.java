package ezscrum.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Notification {

    private String serviceUrl;

    public Notification()throws IOException,JSONException {
        FileReader fr = new FileReader("./Service.json");
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = br.readLine()) != null){
            sb.append(line +"\n");
        }
        JSONObject json = new JSONObject(sb.toString());
        JSONObject service_data = json.getJSONObject("Notification");
        serviceUrl = "http://" + service_data.getString("URL") + ":" + service_data.getString("Port");
    }

    private String SendApi(JSONObject json, String URL)throws IOException,JSONException{
        HttpURLConnection connection = null;
        URL url = new URL(URL);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        OutputStream wr = connection.getOutputStream();
        wr.write(json.toString().getBytes("UTF-8"));
        wr.close();

        StringBuilder sb = new StringBuilder();
        int HttpResult = connection.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            sb.append(br.readLine());
            br.close();
        }
        return sb.toString();

    }

    public String GetSubscriptionStatus(String username, String firebaseToken)throws IOException,JSONException {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("token", firebaseToken);
        return SendApi(json, serviceUrl+"/notify/notifyLogon");
    }

    public String NotifyLogout(String username, String firebaseToken) throws IOException,JSONException{
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("token", firebaseToken);
        return SendApi(json, serviceUrl+"/notify/notifyLogout");
    }

    public String Subscribe(String username, String firebaseToken)throws IOException,JSONException{
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("token", firebaseToken);
        return SendApi(json, serviceUrl+"/notify/subscribe");
    }

    public String CancelSubscribe(String username, String firebaseToken)throws IOException,JSONException{
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("token", firebaseToken);
        return SendApi(json, serviceUrl+"/notify/cancelSubscribe");
    }

    public String SendMessage(String tittle, String body, String eventSource, ArrayList<String> receivers)throws IOException,JSONException{
        JSONArray array = new JSONArray(receivers);
        JSONObject json = new JSONObject();
        json.put("tittle",tittle);
        json.put("body",body);
        json.put("eventSource",eventSource);
        json.put("receivers",array.toString());
        return SendApi(json, serviceUrl+"/notify/send");
    }
}
