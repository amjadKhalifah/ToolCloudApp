package toolcloud.tum.toolcloudapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import toolcloud.tum.toolcloudapp.component.AccordionWidgetDemoActivity;


public class DashboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dashboard);
        /**
         * Creating all buttons instances
         * */
        // Dashboard scan button
        Button btn_scan = (Button) findViewById(R.id.btn_scan);

        // Dashboard aggregate button
        Button btn_aggregate = (Button) findViewById(R.id.btn_aggregate);

        // Dashboard disaggreagte button
        Button btn_disaggreagte = (Button) findViewById(R.id.btn_disagg);

        // Dashboard location button
        Button btn_location = (Button) findViewById(R.id.btn_location);

        // Dashboard Events button
        Button btn_settings = (Button) findViewById(R.id.btn_settings);

        // Dashboard Photos button
        Button btn_about = (Button) findViewById(R.id.btn_about);

        /**
         * Handling all button click events
         * */

        // Listening to News Feed button click
        btn_scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching scan Feed Screen
                Intent i = new Intent(getApplicationContext(), PreScanningActivity.class);
                startActivity(i);
            }
        });

        // Listening agg button click
        btn_aggregate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), CreateAggEventActivity.class);
                Bundle b = new Bundle();
                b.putBoolean("isAggregation",true);
                i.putExtras(b);
//                finish();
                startActivity(i);
            }
        });

        // Listening disagg button click
        btn_disaggreagte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), CreateAggEventActivity.class);
                Bundle b = new Bundle();
                b.putBoolean("isAggregation",false);
                i.putExtras(b);
//                finish();
                startActivity(i);
            }
        });

        // Listening to location button click
        btn_location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
//                Intent i = new Intent(getApplicationContext(), AccordionWidgetDemoActivity.class);
//                startActivity(i);
            }
        });

        // Listening to settings button click
        btn_settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        });

        // Listening to about button click
        btn_about.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(i);
            }
        });
    }



}
