package toolcloud.tum.toolcloudapp;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import toolcloud.tum.toolcloudapp.model.DetailsResult;
import toolcloud.tum.toolcloudapp.model.ToolCloudObject;
import toolcloud.tum.toolcloudapp.xml.ObjectXMLHandler;
import toolcloud.tum.toolcloudapp.xml.ResultsHandler;


public class PreScanningActivity extends Activity {

    TextView objectName, objectType, objectId;
    Button objectScanBtn, continueBtn;
    String scannedObject;
    private TextView title;
    // holds the object returned by the service
    ToolCloudObject toolCloudObject;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pre_scanning);
        title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.dashboard_scan));
        objectName = (TextView) findViewById(R.id.objectName);
        objectType = (TextView) findViewById(R.id.objectType);
        objectId = (TextView) findViewById(R.id.objectId);
        objectScanBtn = (Button) findViewById(R.id.scanObjectBtn);
        continueBtn = (Button)findViewById(R.id.continueBtn);
        objectScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanObject();

            }
        });



        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();

            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pre_scanning, menu);
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

    private void scanObject() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        startActivityForResult(intent, 0);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                scannedObject = contents;
                Log.i("ToolCloud", "contents: " + contents + " format: " + format);
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute();

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Log.i("ToolCloud", "Cancelled");
            }
        }
    }

    private void requestData() {
        RequestDataTask runner = new RequestDataTask();
        runner.execute();

    }

    public String getURL() {
       String query_url = "http://" ;
        // read SharedPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String server_ip = sharedPref.getString("server_IP", "");
        String server_port = sharedPref.getString("server_port", "");
        String server_path = sharedPref.getString("server_path", "");
        query_url+=server_ip+":"+server_port+server_path;

        return query_url;
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, ToolCloudObject> {
         public static final String QUERY_PATH = "machine/identify/";
        private String resp;

        @Override
        protected ToolCloudObject doInBackground(String... params) {
            publishProgress("Loading contents..."); // Calls onProgressUpdate()
            String result = "";
            InputStream in = null;
            // HTTP Get
            HttpURLConnection urlConnection = null;
            try {
//                Log.d("ddd",getURL() + QUERY_PATH+ scannedObject);
                URL url = new URL(getURL() + QUERY_PATH+ scannedObject);
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
            toolCloudObject = result;
            if (result.getType().equals("undefined")){
                objectName.setError(result.getName());
            }
            else {
                objectId.setText(getResources().getString(R.string.object_id) + " " + result.getId());
                objectType.append(getResources().getString(R.string.object_type) + " " + result.getType());
                objectName.append(getResources().getString(R.string.object_name) + " " + result.getName());
                continueBtn.setClickable(true);
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

                Log.w("ToolCloud", "Start");
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




    private class RequestDataTask extends AsyncTask<String, String, DetailsResult> {
//        public static final String QUERY_PATH = "/ToolCloud/tc/";
        private String resp;

        @Override
        protected DetailsResult doInBackground(String... params) {
            publishProgress("Loading contents..."); // Calls onProgressUpdate()
            String result = "";
            InputStream in = null;
            String service="";
            // HTTP Get
            HttpURLConnection urlConnection = null;
            try {
                if(toolCloudObject.getType().equals("machine"))
                {
                    service= "machine/details/";
                }else  if(toolCloudObject.getType().equals("intake"))
                {
                    service= "intake/details/";
                } if(toolCloudObject.getType().equals("tool"))
                {
                    service= "tool/details/";
                }

                URL url = new URL(getURL() +  service + toolCloudObject.getId());
                Log.d("ToolCloud", url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                String  response = convertStreamToString(in);
//                Log.d("ToolCloud", response);
                return parseXML(response);
            } catch (Exception e) {
                Log.d("ToolCloud", e.getMessage());
                return null;
//                Toast.makeText(getApplicationContext(), "Error communicating with server " + e.getMessage(), Toast.LENGTH_LONG);
            }
            finally {
                urlConnection.disconnect();
            }

        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(DetailsResult result) {

            Intent intent;
            if (result.getType().equals("machine")) {
                 intent = new Intent(getApplicationContext(), MachineDetailsActivity.class);

            }else if (result.getType().equals("intake")) {
                intent = new Intent(getApplicationContext(), IntakeDetailsActivity.class);
                Bundle b = new Bundle();
                b.putInt("intake",0); //in case the scan was of intake
                intent.putExtras(b);
            }else {// scan a tool
                intent = new Intent(getApplicationContext(), IntakeDetailsActivity.class);
//                Log.d("ToolCloud", "starting tool details"+result);
                Bundle b = new Bundle();
                b.putInt("intake",-1); //in case the scan was of tool
                b.putInt("tool",0);
                intent.putExtras(b);

            }

            startActivity(intent);
//            finish();
        }

        /**
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }


        private DetailsResult parseXML(String xmlInput) {
            try {

                Log.w("ToolCloud", "Start");
                /** Handling XML */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();

                ResultsHandler resultsXMLHandler = new ResultsHandler(toolCloudObject.getType());
                xr.setContentHandler(resultsXMLHandler);
                InputSource inStream = new InputSource();

                inStream.setCharacterStream(new StringReader(xmlInput));

                xr.parse(inStream);

                DetailsResult results = resultsXMLHandler.getResult();
                DetailsResult.setInstance(results);

                return results;
            } catch (Exception e) {
                Log.w("ToolCloud", e);
                return null;
            }
        }

    }








}
