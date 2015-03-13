package toolcloud.tum.toolcloudapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import toolcloud.tum.toolcloudapp.model.DetailsResult;
import toolcloud.tum.toolcloudapp.model.Intake;
import toolcloud.tum.toolcloudapp.model.Machine;
import toolcloud.tum.toolcloudapp.model.Tool;


public class MachineDetailsActivity extends Activity {
    private DetailsResult result;
    private TextView mId, mName, mDer, mCompany, mCad;
    private ListView contentsListView, toolListView;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_machine_details);

        title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.dashboard_scan));
        result = DetailsResult.getInstance();
        Log.d("ToolCloud", "set the result instance");
        mId = (TextView) findViewById(R.id.IdValue);
        mName = (TextView) findViewById(R.id.NameValue);
        mDer = (TextView) findViewById(R.id.DerValue);
        mCompany = (TextView) findViewById(R.id.CompanyValue);
        mCad = (TextView) findViewById(R.id.CadValue);
        contentsListView = (ListView) findViewById(R.id.contents_listView);
        toolListView = (ListView) findViewById(R.id.tools_listView);

        mCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!result.getMachine().getCad().isEmpty() && result.getMachine().getCad() != null) {
                    Intent intent = new Intent(getApplicationContext(), PDFViewerActivity.class);
                    Bundle b = new Bundle();
                    b.putString("link", result.getMachine().getCad()); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    startActivity(intent);
                }
            }
        });
        setMachineValues();
        setIntakesValues();
        setToolsValues();

    }


    private void setIntakesValues() {
        ListAdapter listProvider = new ArrayAdapter<Intake>(getApplicationContext(), R.layout.list_item, result.getIntakes());
        contentsListView.setAdapter(listProvider);
        contentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;
                Log.d("ToolCloud", "clicked " + position);

                Intent intent = new Intent(getApplicationContext(), IntakeDetailsActivity.class);
                Bundle b = new Bundle();
                b.putInt("intake", position); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
            }
        });
    }

    private void setToolsValues() {
        ListAdapter listProvider = new ArrayAdapter<Tool>(getApplicationContext(), R.layout.list_item, result.getTools());
        toolListView.setAdapter(listProvider);
        toolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;
                Log.d("ToolCloud", "clicked " + position);
                // ListView Clicked item value

                Intent intent = new Intent(getApplicationContext(), IntakeDetailsActivity.class);
                Bundle b = new Bundle();
                b.putInt("intake", -1); //Your id
                b.putInt("tool", position); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
//                finish();
            }
        });
    }

    private void setMachineValues() {
        Machine machine = result.getMachine();
        if (machine != null) {
            mId.setText(machine.getMachineId());
            mName.setText(machine.getName());
            mDer.setText(machine.getDer());
            mCompany.setText(machine.getCompanyId());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_machine_details, menu);
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
}
