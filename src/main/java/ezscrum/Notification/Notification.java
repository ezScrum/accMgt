package ezscrum.Notification;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Notification {

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
            return sb.toString();
        }
        else{
            return "Connection Error: Notification service";
        }
    }

    public String GetSubscriptionStatus(String username, String firebaseToken)throws IOException,JSONException {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("token", firebaseToken);
        return SendApi(json, "http://localhost:5000/notify/notifyLogon");
    }

    public String Subscribe(String username, String firebaseToken)throws IOException,JSONException{
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("token", firebaseToken);
        return SendApi(json, "http://localhost:5000/notify/subscript");
    }
}
