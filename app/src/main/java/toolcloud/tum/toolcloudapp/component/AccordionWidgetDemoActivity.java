/**
 * Copyright (c) 2011, 2012 Sentaca Communications Ltd.
 */
package toolcloud.tum.toolcloudapp.component;

import android.app.Activity;
import android.os.Bundle;

import toolcloud.tum.toolcloudapp.R;


public class AccordionWidgetDemoActivity extends Activity {
  private static final String TAG = "AccordionWidgetDemoActivity";

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_machine_details);

//    final AccordionView v = (AccordionView) findViewById(R.id.accordion_view);

//    LinearLayout ll = (LinearLayout) v.findViewById(R.id.example_get_by_id);
//    TextView tv = new TextView(this);
//    tv.setText("Added in runtime...");
//    FontUtils.setCustomFont(tv, getAssets());
//    ll.addView(tv);
  }
}