package toolcloud.tum.toolcloudapp;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;


public class PDFViewerActivity extends Activity {
    WebView pdfViewer;
    String link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pdfviewer);
        pdfViewer= (WebView)findViewById(R.id.pdfView);

        Bundle b = getIntent().getExtras();
        if (b.getString("link") != null) {
            link = b.getString("link");
        }

        pdfViewer.getSettings().setJavaScriptEnabled(true);
        pdfViewer.loadUrl("https://docs.google.com/gview?embedded=true&url="+link);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pdfviewer, menu);
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
