package boardgameProcess.helper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Rest {
    private final static Logger LOGGER = Logger.getLogger(Rest.class.getName());
    private final static Rest INSTANCE = new Rest();

    private CloseableHttpClient httpclient;
    private final static String apiEndpoint = "http://localhost:5000/";

    private Rest() {
        httpclient = HttpClients.createDefault();
    }

    public static Rest getInstance() {
        return Rest.INSTANCE;
    }

    public String post(String resource, ArrayList<NameValuePair> parameters) {
        String url = apiEndpoint + resource;
        HttpPost request = new HttpPost(url);
        String responseContent = null;

        try {
            if (parameters != null)
                request.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));

            CloseableHttpResponse response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                throw new Exception("HTTP error code" + response.getStatusLine());
            else {
                HttpEntity entity = response.getEntity();
                responseContent = inputStreamToString(entity.getContent());
                EntityUtils.consume(entity);
                response.close();

                LOGGER.info("\n\n\n" + Rest.class.getName() + " - post request SUCCESSFUL\n\n\n");
            }
        } catch (Exception e) {
            LOGGER.severe("\n\n\n" + Rest.class.getName() + " - post request FAILURE\n");
            e.printStackTrace();
        }

        return responseContent;
    }

    private String inputStreamToString(InputStream inputStream) {
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}