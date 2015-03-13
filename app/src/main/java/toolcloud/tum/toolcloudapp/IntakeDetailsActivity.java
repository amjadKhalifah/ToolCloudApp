package toolcloud.tum.toolcloudapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import toolcloud.tum.toolcloudapp.component.AccordionView;
import toolcloud.tum.toolcloudapp.model.DetailsResult;
import toolcloud.tum.toolcloudapp.model.Intake;
import toolcloud.tum.toolcloudapp.model.Tool;


public class IntakeDetailsActivity extends Activity {
    private DetailsResult result;
    private TextView inId, inName, inHeight, inLength,inCad, tId, tName, tHeight, tLength, tAggLength,tCad;
    private int intakeSelectedIndex = -1, toolSelectedIndex = -1;
    AccordionView accordion;
    LinearLayout toolContainer;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intake_details);

        title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.dashboard_scan));
        result = DetailsResult.getInstance();
        inId = (TextView) findViewById(R.id.IdValue);
        inName = (TextView) findViewById(R.id.NameValue);
        inHeight = (TextView) findViewById(R.id.HeightValue);
        inLength = (TextView) findViewById(R.id.LengthValue);
        inCad = (TextView)findViewById(R.id.CadValue);
        tId = (TextView) findViewById(R.id.ToolIdValue);
        tName = (TextView) findViewById(R.id.ToolNameValue);
        tHeight = (TextView) findViewById(R.id.ToolHeightValue);
        tLength = (TextView) findViewById(R.id.ToolLengthValue);
        tAggLength = (TextView) findViewById(R.id.ToolAggLengthValue);
        tCad = (TextView)findViewById(R.id.ToolCadValue);
        accordion = (AccordionView)findViewById(R.id.accordion_views);
        toolContainer = (LinearLayout)findViewById(R.id.tool_container);
        Bundle b = getIntent().getExtras();
        if (b.getInt("intake") != -1) {
            intakeSelectedIndex = b.getInt("intake");
            setIntakeValues();

        } else {// this is a tool scanned or clicked from a machine
            Log.d("ToolCloud", "setting tools values");
            toolSelectedIndex = b.getInt("tool");
            setToolValues();

        }





    }


    private void setIntakeValues() {
        if (intakeSelectedIndex != -1) {
           final Intake intake = result.getIntakes().get(intakeSelectedIndex);
            if (intake != null) {
                inId.setText(intake.getIntakeId());
                inName.setText(intake.getName());
                inHeight.setText(intake.getHeight());
                inLength.setText(intake.getLength());
//                inCad.setText(intake.getCad());
              final  Tool tool = intake.getTool();

                inCad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!intake.getCad().isEmpty() && intake.getCad() != null) {
                            Intent intent = new Intent(getApplicationContext(), PDFViewerActivity.class);
                            Bundle b = new Bundle();
                            b.putString("link", intake.getCad()); //Your id
                            intent.putExtras(b); //Put your id to your next Intent
                            startActivity(intent);
                        }
                    }
                });



                if (tool != null) {
                    tId.setText(tool.getToolId());
                    tName.setText(tool.getName());
                    tHeight.setText(tool.getHeight());
                    tLength.setText(tool.getLength());
                    int inLength = Integer.parseInt(intake.getLength());
                    int tLength = Integer.parseInt(tool.getLength());
                    int aggLength = inLength+tLength;
                    tAggLength.setText(""+aggLength);
//                    tCad.setText(tool.getCad());

                    tCad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!tool.getCad().isEmpty() && tool.getCad() != null) {
                                Intent intent = new Intent(getApplicationContext(), PDFViewerActivity.class);
                                Bundle b = new Bundle();
                                b.putString("link", tool.getCad()); //Your id
                                intent.putExtras(b); //Put your id to your next Intent
                                startActivity(intent);
                            }
                        }
                    });


                } else { // case of intake has no tool; hide the toolayout
                    toolContainer.setVisibility(View.GONE);
                }

            }

        }

    }
    private void setToolValues() {// used if the scan was tool
       final Tool tool = result.getTools().get(toolSelectedIndex);
        if (tool != null) {
            tId.setText(tool.getToolId());
            tName.setText(tool.getName());
            tHeight.setText(tool.getHeight());
            tLength.setText(tool.getLength());
//            tCad.setText(tool.getCad());
            hideIntakeLayout();


            tCad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!tool.getCad().isEmpty() && tool.getCad() != null) {
                        Intent intent = new Intent(getApplicationContext(), PDFViewerActivity.class);
                        Bundle b = new Bundle();
                        b.putString("link", tool.getCad()); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                        startActivity(intent);
                    }
                }
            });

        }

    }




    private void hideIntakeLayout() {

        for(int i=0; i<((ViewGroup)accordion).getChildCount(); ++i) {
            View nextChild = ((ViewGroup)accordion).getChildAt(i);
            ((ViewManager)nextChild.getParent()).removeView(nextChild);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intake_details, menu);
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
