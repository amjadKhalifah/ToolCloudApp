package toolcloud.tum.toolcloudapp;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import toolcloud.tum.toolcloudapp.model.Event;
import toolcloud.tum.toolcloudapp.xml.EventXMLHandler;

// TODO delete maybe
public class ScanObjectActivity extends Activity {
    Button scanBtn;
    TextView scanMachineTextView;
    private String machineCode;
    private ListView resultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_machine);
        resultListView = (ListView) findViewById(R.id.result_listView);
        scanMachineTextView = (TextView) findViewById(R.id.scan_machine_textView);
        scanBtn = (Button) findViewById(R.id.scan_button);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                startActivityForResult(intent, 0);
            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                machineCode = contents;
                Log.i("ToolCloud", "contents: " + contents + " format: " + format);
//                readEvents(machineCode);
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute();

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
                Log.i("ToolCloud", "Cancelled");
            }
        }
    }


    // TODO this method will read the data from service: either objectevent with read point or aggregation event with parent id
    // params, update, result
    private class AsyncTaskRunner extends AsyncTask<String, String, List<Event>> {
        public final static String QUERY_URL = "http://217.110.56.76:80/epc-evo/query?wsdl";
        public static final String QUERY_NAMESPACE = "urn:epcglobal:epcis-query:xsd:1";
        public static final String QUERY_SOAP_ACTION_PREFIX = "/";
        private static final String QUERY_METHOD = "Poll";
        private String resp;

        @Override
        protected List<Event> doInBackground(String... params) {
            publishProgress("Loading contents..."); // Calls onProgressUpdate()
            List<Event> result = new ArrayList<>();
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
                // TODO externalize
                parameter1.addProperty("value", "10");

                SoapObject parameter2 = new SoapObject("", "param");
                parameter2.addProperty("name", "orderBy");
                parameter2.addProperty("value", "recordTime");


                SoapObject stringParameter = new SoapObject("", "");
                stringParameter.addProperty("string", machineCode);
                SoapObject parameter3 = new SoapObject("", "param");
                parameter3.addProperty("name", "MATCH_parentID");
                parameter3.addProperty("value", stringParameter);


                //TODO change here if we want to use the other option of service usage
                SoapObject parameter4 = new SoapObject("", "param");
                parameter4.addProperty("name", "eventType");
                parameter4.addProperty("value", "AggregationEvent");



                SoapObject stringParameter2 = new SoapObject("", "");
                stringParameter2.addProperty("string", "ADD");
                SoapObject parameter5 = new SoapObject("", "param");
                parameter5.addProperty("name", "EQ_action");
                parameter5.addProperty("value", stringParameter2);

                parameters.addSoapObject(parameter1);
                parameters.addSoapObject(parameter2);
                parameters.addSoapObject(parameter3);
                parameters.addSoapObject(parameter4);
                parameters.addSoapObject(parameter5);

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
                    Log.d("ToolCloud", "HTTP REQUEST:\n" + transport.requestDump);
//                    Log.d(TAG, "HTTP RESPONSE:\n" + transport.responseDump);
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
//                        resp = String.valueOf(transport.responseDump).substring(393);
                        String response = String.valueOf(transport.responseDump);
                        Log.i("ToolCloud", response + "--");
                        result = parseXML(response);


                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
                resp = e.getMessage();
            }
            return result;
        }

        /**
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(List<Event> result) {
            // execution of result of Long time consuming operation
            // In this example it is the return value from the web service

            ListAdapter listProvider = new ArrayAdapter<Event>(getApplicationContext(),  android.R.layout.simple_list_item_1, result);
            resultListView.setAdapter(listProvider);

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
//            resultTxt.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


        private List<Event> parseXML(String xmlInput) {

//            String parsedData = "";

            try {

                Log.w("ToolCloud", "Start");
                /** Handling XML */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();

                EventXMLHandler myXMLHandler = new EventXMLHandler();
                xr.setContentHandler(myXMLHandler);
                InputSource inStream = new InputSource();
                Log.w("ToolCloud", "Parse1");

                inStream.setCharacterStream(new StringReader(xmlInput));
                Log.w("ToolCloud", "Parse2");

                xr.parse(inStream);
                Log.w("ToolCloud", "Parse3");

                ArrayList<Event> eventsList = myXMLHandler.getEventsList();
//                for (int i = 0; i < eventsList.size(); i++) {
//                    Event event = eventsList.get(i);
//                    parsedData = parsedData + "----->\n" + event.toString();
//
//                }
//

                Log.w("ToolCloud", "Done");
                return eventsList;
            } catch (Exception e) {
                Log.w("ToolCloud", e);
                return null;
            }
        }

    }
}
