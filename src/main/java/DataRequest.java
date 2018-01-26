import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean McKenna on 10/6/2017.
 */

public class DataRequest {

    private String instrument;
    private long start;
    private long end;
    private String granularity;

    //Instrument is the currency pair that you want
    //granularity is the resolution of the data e.g. "M1" for 1 minute bars
    public DataRequest(String instrument, long start, long end, String granularity) {
        this.instrument = instrument;
        this.start = start;
        this.end = end;
        this.granularity = granularity;
    }

    //GETTERS
    public String getInstrument() {return instrument;}
    public long getStart() {return start;}
    public long getEnd() {return end;}
    public String getGranularity() {return granularity;}

    //REQUEST LOGIC
    public List<Rate> makeRequest() throws IOException {
        //System.out.println(startTime+", "+endTime);

        String sURL = "https://api-fxtrade.oanda.com/v3/instruments/"+instrument+"/candles?price=M&granularity="+granularity+"&from="+start+"&to="+end;

        HttpGet httpGet = new HttpGet(sURL);
        httpGet.setHeader("Authorization","Bearer {your own api authorization code}");
        httpGet.setHeader("Accept-Datetime-Format","UNIX");

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream instream = entity.getContent();
        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader(instream)); //Convert the input stream to a json element
        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
        final JsonElement dataElement = rootobj.get("candles");
        final JsonArray valuesArray = dataElement.getAsJsonArray();
        List<Rate> rates = new ArrayList<Rate>(); //array of rates

        for(Object value : valuesArray){
            final JsonObject obj = (JsonObject)value;

            final JsonElement timeElement = obj.get("time");
            long time = (long)timeElement.getAsDouble();

            final JsonElement volumeElement = obj.get("volume");
            int volume = volumeElement.getAsInt();

            float open =0;
            float high =0;
            float low =0;
            float close =0;

            final JsonElement midElement = obj.get("mid");

            final JsonObject midData = midElement.getAsJsonObject();

            final JsonElement openElement = midData.get("o");
            open = openElement.getAsFloat();

            final JsonElement highElement = midData.get("h");
            high = highElement.getAsFloat();

            final JsonElement lowElement = midData.get("l");
            low = lowElement.getAsFloat();

            final JsonElement closeElement = midData.get("c");
            close = closeElement.getAsFloat();

            rates.add(new Rate(time,open,high,low,close,volume));

        }
        instream.close();
        return rates;
    }
}
