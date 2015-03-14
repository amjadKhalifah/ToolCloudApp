package toolcloud.tum.toolcloudapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import toolcloud.tum.toolcloudapp.component.GPSTracker;
import toolcloud.tum.toolcloudapp.model.ToolCloudObject;
import toolcloud.tum.toolcloudapp.xml.ObjectXMLHandler;


public class CreateAggEventActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ToolCloud";
    private String parentCode, childCode;
    private ToolCloudObject parentObject, childObject;
    private Button parentScanBtn, childScanBtn, captureEventBtn;
    TextView parentObjectName, parentObjectType, childObjectName, childObjectType;
    private boolean parentScan = true;
    private boolean isAggregation = true;
    private TextView title, validationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_agg_event);
        title = (TextView)findViewById(R.id.title);
        validationMessage = (TextView)findViewById(R.id.validationMessage);



        parentScanBtn = (Button) findViewById(R.id.parent_scan_button);
        childScanBtn = (Button) findViewById(R.id.child_scan_button);

        parentObjectName = (TextView) findViewById(R.id.ParentObjectName);
        parentObjectType = (TextView) findViewById(R.id.ParentObjectType);
        childObjectName = (TextView) findViewById(R.id.ChildObjectName);
        childObjectType = (TextView) findViewById(R.id.ChildObjectType);

        captureEventBtn = (Button) findViewById(R.id.capture_event);
        parentScanBtn.setOnClickListener(this);
        childScanBtn.setOnClickListener(this);
        captureEventBtn.setOnClickListener(this);
        Bundle b = getIntent().getExtras();
        if (!b.getBoolean("isAggregation")) {
            title.setText(getResources().getString(R.string.dashboard_dissaggregate));
            isAggregation = false;
            captureEventBtn.setText("Disaggregate");
        }else{
            title.setText(getResources().getString(R.string.dashboard_aggregate));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_agg_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        validationMessage.clearComposingText();
        if (v.getId() == R.id.parent_scan_button) {
            parentScan = true;
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            startActivityForResult(intent, 0);
        } else if (v.getId() == R.id.child_scan_button) {
            parentScan = false;
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            startActivityForResult(intent, 0);
        } else if (v.getId() == R.id.capture_event) {

            if (isAggregation) {
                String validationResult = validateScans();
                if (!validationResult.isEmpty()) {
                    validationMessage.setText(Html.fromHtml("<font color='red'>" + validationResult + "</font>"));
                    return;
                }
            }
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
            String timeZone = new SimpleDateFormat("Z").format(calendar.getTime());
            timeZone = timeZone.substring(0, 3) + ":" + timeZone.substring(3, 5);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            String timeString = sdf.format(calendar.getTime());
            Log.d(TAG, "time" + timeString + parentCode + "-" + childCode);

            String action = isAggregation ? "ADD":"DELETE";
            GPSTracker gps = new GPSTracker(this);
            String locationStr="";
            if(gps.canGetLocation()) { // gps enabled} // return boolean true/false

                double lat = gps.getLatitude(); // returns latitude
                double lon =    gps.getLongitude(); // returns longitude
//                Log.d("ToolCloud", lat +","+lon);
                locationStr = lat +","+lon;
            }
            else {
                gps.showSettingsAlert();
            }
                gps.stopUsingGPS();

            CreateEventAsyncTaskRunner runner = new CreateEventAsyncTaskRunner(parentCode, childCode, timeString, timeZone, action,locationStr);
            runner.execute();
        }

    }

    private String validateScans() {
        if(parentObject==null || childObject==null)
        {
            return "Please scan the parent and the child objects.";

        }
        if (childObject.getType().equals("machine")) {
            return "Cannot aggregate machine as a child object.";
        }
        if (parentObject.getType().equals("tool")) {
            return "Cannot aggregate tool as a parent object.";
        }
        if (childObject.getType().equals(parentObject.getType())) {
            return "Cannot aggregate objects of the same type.";
        }
        if (parentObject.getType().equals("intake") && !childObject.getType().equals("tool") ) {
            return "Child object should be tool in case of a parent intake";
        }

        if (parentObject.getType().equals("intake") && parentObject.isAggregatedAsParent() ) {
            return "Intake already aggregated as a parent";
        }
        if (childObject.getType().equals("tool") && childObject.isAggregatedAsChild() ) {
            return "Tool already aggregated as a child";
        }

        if (childObject.getType().equals("intake") && childObject.isAggregatedAsChild() ) {
            return "Intake already aggregated as a child";
        }
        return "";
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                if (parentScan) {
                    parentCode = contents;
                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.setCode(parentCode);
                    runner.execute();
                } else {
                    childCode = contents;
                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.setCode(childCode);
                    runner.execute();
                }

                Log.i("ToolCloud", "contents: " + contents + " format: " + format);

            } else if (resultCode == RESULT_CANCELED) {
                Log.i("ToolCloud", "Cancelled");
            }
        }
    }

    public String getEpcisURL() {
        String query_url = "http://" ; //"http://217.110.56.76:80/epc-evo/capture"
        // read SharedPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String server_ip = sharedPref.getString("epcis_IP", "");
        String server_port = sharedPref.getString("epcis_port", "");
        String server_path = sharedPref.getString("epcis_path", "");
        query_url+=server_ip+":"+server_port+server_path;
        Log.d("ur",query_url);
        return query_url;
    }
    public String getAuthenticationString() {
        String auth  ;//"epcis:L1wrenceJ"
        // read SharedPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String user = sharedPref.getString("epcis_user", "");
        String password = sharedPref.getString("epcis_password", "");
        auth=user+":"+password;
        Log.d("auth",auth);
        return auth;
    }
    public String getServerURL() {
        String query_url = "http://" ;
        // read SharedPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String server_ip = sharedPref.getString("server_IP", "");
        String server_port = sharedPref.getString("server_port", "");
        String server_path = sharedPref.getString("server_path", "");
        query_url+=server_ip+":"+server_port+server_path;

        return query_url;
    }
    private class CreateEventAsyncTaskRunner extends AsyncTask<String, String, String> {
        private String locationCode, toolCode, timeString, timeZoneOffset, action,locationStr;


        private CreateEventAsyncTaskRunner(String locationCode, String toolCode, String timeString, String timeZoneOffset, String action,String locationStr) {
            this.locationCode = locationCode;
            this.toolCode = toolCode;
            this.timeString = timeString;
            this.timeZoneOffset = timeZoneOffset;
            this.action = action;
            this.locationStr = locationStr;

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                URL urlToRequest = new URL(getEpcisURL());
                HttpURLConnection urlConnection =
                        (HttpURLConnection) urlToRequest.openConnection();
                String authorization = new String(org.kobjects.base64.Base64.encode((getAuthenticationString()).getBytes()));
                urlConnection.setRequestProperty("Authorization", "Basic " + authorization);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setDefaultUseCaches(false);

                urlConnection.setRequestProperty("Content-Type", "application/xml");
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(getXML());
                out.close();


                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {


                    InputStream in = new BufferedInputStream(urlConnection.getErrorStream());
                    InputStreamReader is = new InputStreamReader(in);
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(is);
                    String read = br.readLine();

                    while (read != null) {
                        sb.append(read);
                        read = br.readLine();

                    }
                    Log.d(TAG, statusCode + ":" + urlConnection.getResponseMessage() + ":" + sb);
                    return urlConnection.getResponseMessage() +":" + sb;
                }


                urlConnection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();

                return "failed";
            }

            return "posted";
        }

        @Override
        protected void onPostExecute(String result) {

           if (result.equals("posted")) {
               if (isAggregation) {
                   Toast.makeText(getApplicationContext(), "Aggregation created successfully", Toast.LENGTH_LONG).show();
               } else {
                   Toast.makeText(getApplicationContext(), "Disaggregation created successfully", Toast.LENGTH_LONG).show();
               }
           }else{
               Toast.makeText(getApplicationContext(), result.substring(0,102), Toast.LENGTH_LONG).show();
           }
        }

        private String getXML() {
            if (locationStr.isEmpty()){

                locationStr = locationCode;
            }
            String s_xml = "<EPCISDocument ><EPCISBody><EventList><AggregationEvent> <eventTime>" + this.timeString + "</eventTime> <eventTimeZoneOffset>" + this.timeZoneOffset + "</eventTimeZoneOffset> <parentID>" + locationCode + "</parentID> <childEPCs><epc>" + this.toolCode + "</epc></childEPCs><action>" + this.action.toUpperCase() + "</action> <bizStep>urn:epcglobal:cbv:bizstep:receiving</bizStep> <disposition>urn:epcglobal:cbv:disp:in_progress</disposition> <readPoint><id>" + this.locationStr + "</id></readPoint></AggregationEvent></EventList></EPCISBody></EPCISDocument>";

            Log.d(TAG, "posting:" + s_xml);

            return s_xml;

        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, ToolCloudObject> {
        public static final String QUERY_PATH = "machine/identify/";
        private String resp;
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        protected ToolCloudObject doInBackground(String... params) {
            publishProgress("Loading contents..."); // Calls onProgressUpdate()
            String result = "";
            InputStream in = null;
            // HTTP Get
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(getServerURL() + QUERY_PATH+ code);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertStreamToString(in);
                return parseXML(response);
            } catch (Exception e) {
                Log.d("ToolCloud", e.getMessage());
//                Toast.makeText(getApplicationContext(), "Error communicating with server " + e.getMessage(), Toast.LENGTH_LONG);
                return new ToolCloudObject("Error connecting to serve", "Error connecting to server", "undefined");
            } finally {
                urlConnection.disconnect();
            }

        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ToolCloudObject result) {
            if (result.getType().equals("undefined")) {
                parentObjectName.setText(Html.fromHtml("<font color='red'>" + result.getName() + "</font>"));
            } else {
                if (parentScan) {
                    parentObject = result;
                    parentObjectType.setText(getResources().getString(R.string.object_type) + " " + result.getType());
                    parentObjectName.setText(getResources().getString(R.string.object_name) + " " + result.getName());
                } else {
                    childObject = result;
                    childObjectType.setText(getResources().getString(R.string.object_type) + " " + result.getType());
                    childObjectName.setText(getResources().getString(R.string.object_name) + " " + result.getName());
                }

                captureEventBtn.setClickable(true);
            }
        }

        /**
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }


        private ToolCloudObject parseXML(String xmlInput) {
            try {

                /** Handling XML */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();

                ObjectXMLHandler myXMLHandler = new ObjectXMLHandler();
                xr.setContentHandler(myXMLHandler);
                InputSource inStream = new InputSource();

                inStream.setCharacterStream(new StringReader(xmlInput));

                xr.parse(inStream);

                ToolCloudObject toolCloudObject = myXMLHandler.getToolCloudObject();

                return toolCloudObject;
            } catch (Exception e) {
                Log.w("ToolCloud", e);
                return null;
            }
        }

    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


}
