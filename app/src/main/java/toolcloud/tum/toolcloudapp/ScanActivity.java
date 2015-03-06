package toolcloud.tum.toolcloudapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//TODO maybe this is for delete
public class ScanActivity extends Activity implements View.OnClickListener {


    private static final String TAG = "ToolCloud";
    private Button scanBtn, fetchService, resetBtn, captureEventBtn;
    private TextView formatTxt, contentTxt, resultTxt, toolContent;
    private String locationCode, toolCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        scanBtn = (Button) findViewById(R.id.scan_button);
        resetBtn = (Button) findViewById(R.id.reset_button);
        fetchService = (Button) findViewById(R.id.fetch_button);
        captureEventBtn = (Button) findViewById(R.id.capture_event);
        formatTxt = (TextView) findViewById(R.id.scan_format);
        contentTxt = (TextView) findViewById(R.id.scan_content);
        resultTxt = (TextView) findViewById(R.id.result_text);
        toolContent = (TextView) findViewById(R.id.tool_content);
        // add scrolling ability to the result view.
        resultTxt.setMovementMethod(new ScrollingMovementMethod());
        scanBtn.setOnClickListener(this);
        fetchService.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        captureEventBtn.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_scan) {
            Intent intent = new Intent("toolcloud.tum.toolcloudapp.ScanMachineActivity");
            startActivityForResult(intent, 0);
            return true;
        }else   if (id == R.id.menu_create) {
            Intent intent = new Intent("toolcloud.tum.toolcloudapp.CreateAggEventActivity");
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scan_button) {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            startActivityForResult(intent, 0);
        } else if (v.getId() == R.id.fetch_button) {
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
        } else if (v.getId() == R.id.reset_button) {
            resetCodes();
        } else if (v.getId() == R.id.capture_event) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
            String timeZone = new SimpleDateFormat("Z").format(calendar.getTime());
            timeZone = timeZone.substring(0, 3) + ":" + timeZone.substring(3, 5);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            String timeString = sdf.format(calendar.getTime());
            Log.d(TAG, "time" + timeString +locationCode+"-"+toolCode);

            HTTPAsyncTaskRunner runner = new HTTPAsyncTaskRunner(locationCode, toolCode, timeString, timeZone, "OBSERVE");
            runner.execute();
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                if (locationCode == null) {
                    // this scan is for location
//                    formatTxt.setText("Code: " + format);
                    contentTxt.setText("Location code: " + contents);
                    locationCode = contents;
                } else if (toolCode == null) {
                    toolContent.setText("TOOL code: " + contents);
                    toolCode = contents;
                }

                Log.i("ToolCloud", "contents: " + contents + " format: " + format);

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Log.i("ToolCloud", "Cancelled");
            }
        }
    }

    private void resetCodes() {
        toolCode = null;
        locationCode = null;
        toolContent.setText("");
        contentTxt.setText("");
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        public final static String QUERY_URL = "http://217.110.56.76:80/epc-evo/query?wsdl";
        public static final String QUERY_NAMESPACE = "urn:epcglobal:epcis-query:xsd:1";
        public static final String QUERY_SOAP_ACTION_PREFIX = "/";
        private static final String QUERY_METHOD = "Poll";
        private String resp;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Loading contents..."); // Calls onProgressUpdate()
            try {
                // SoapEnvelop.VER11 is SOAP Version 1.1 constant
                //TODO should change the request to fetch for some id.
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                SoapObject request = new SoapObject(QUERY_NAMESPACE, QUERY_METHOD);
                request.addProperty("queryName", "SimpleEventQuery");
                // parameters tags
                SoapObject parameters = new SoapObject();
                SoapObject parameter1 = new SoapObject("", "param");
                parameter1.addProperty("name", "eventCountLimit");
                parameter1.addProperty("value", "1");

                SoapObject parameter2 = new SoapObject("", "param");
                parameter2.addProperty("name", "orderBy");
                parameter2.addProperty("value", "recordTime");

                parameters.addSoapObject(parameter1);
                parameters.addSoapObject(parameter2);

                PropertyInfo parametersInfo = new PropertyInfo();
                parametersInfo.setName("params");
                parametersInfo.setValue(parameters);
                parametersInfo.setType(parameters.getClass());


                request.addProperty(parametersInfo);
                //bodyOut is the body object to be sent out with this envelope
                envelope.bodyOut = request;
                envelope.setAddAdornments(false);
                envelope.implicitTypes = true;
                HttpTransportSE transport = new HttpTransportSE(QUERY_URL);
                transport.debug = true;

                try {
                    List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
                    headerList.add(new HeaderProperty("Authorization", "Basic " + org.kobjects.base64.Base64.encode("epcis:L1wrenceJ".getBytes())));

                    transport.call(QUERY_NAMESPACE + QUERY_SOAP_ACTION_PREFIX + QUERY_METHOD, envelope, headerList);

                    // Logging the raw request and response (for debugging purposes)
//                    Log.d(TAG, "HTTP REQUEST:\n" + transport.requestDump);
                    Log.d(TAG, "HTTP RESPONSE:\n" + transport.responseDump);
                } catch (IOException e) {

                    e.printStackTrace();
                } catch (XmlPullParserException e) {

                    e.printStackTrace();
                }


                //bodyIn is the body object received with this envelope
                if (envelope.bodyIn != null) {
                    if (envelope.bodyIn instanceof SoapFault) {
                        String str = ((SoapFault) envelope.bodyIn).faultstring;
                        Log.i("ToolCloud", str);
                        resp = str;
                    } else {
                        // this is in jason and the dump is in xml
                        SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
                        resp = String.valueOf(transport.responseDump).substring(393);

                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            // In this example it is the return value from the web service
            resultTxt.setText(result);

        }

        /**
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        /**
         * @see android.os.AsyncTask
         */
        @Override
        protected void onProgressUpdate(String... text) {
            resultTxt.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }

    }

    private class HTTPAsyncTaskRunner extends AsyncTask<String, String, String> {
        private String locationCode, toolCode, timeString, timeZoneOffset, action;


        private HTTPAsyncTaskRunner(String locationCode, String toolCode, String timeString, String timeZoneOffset, String action) {
            this.locationCode = locationCode;
            this.toolCode = toolCode;
            this.timeString = timeString;
            this.timeZoneOffset = timeZoneOffset;
            this.action = action;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL urlToRequest = new URL("http://217.110.56.76:80/epc-evo/capture");
                HttpURLConnection urlConnection =
                        (HttpURLConnection) urlToRequest.openConnection();
                String authorization = new String(org.kobjects.base64.Base64.encode(("epcis:L1wrenceJ").getBytes()));
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
                        //System.out.println(read);
                        sb.append(read);
                        read = br.readLine();

                    }

                    Log.d(TAG, statusCode + ":" + urlConnection.getResponseMessage() + ":" + sb);

                }


                urlConnection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();

                return "event failed";
            }

            return "event posted";
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            // In this example it is the return value from the web service
            resultTxt.setText(result);

        }

        private String getXML() {
//            if (locationCode!=null && !locationCode.isEmpty()){
//                if (toolCode!=null && !toolCode.isEmpty()) {
//
//                }
//
//            }
//

            String s_xml = "<EPCISDocument ><EPCISBody><EventList><ObjectEvent> <eventTime>" + this.timeString + "</eventTime> <eventTimeZoneOffset>" + this.timeZoneOffset + "</eventTimeZoneOffset> <epcList><epc>" + this.toolCode + "</epc></epcList><action>" + this.action.toUpperCase() + "</action> <bizStep>urn:epcglobal:cbv:bizstep:receiving</bizStep> <disposition>urn:epcglobal:cbv:disp:in_progress</disposition> <readPoint><id>" + this.locationCode + "</id></readPoint></ObjectEvent></EventList></EPCISBody></EPCISDocument>";

            Log.d(TAG, "posting:" + s_xml);

            return s_xml;

        }
    }


}
