package in.cioc.syrow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

public class SyrowWebViewChat extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syrow_web_view_chat);
        getSupportActionBar().setTitle("Syrow");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://socket.syrow.com/");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item_camera) {
            startActivity(new Intent(getApplicationContext(),SyrowWebViewChat.class));
            Toast.makeText(getApplicationContext(),"video call will be available soon",Toast.LENGTH_SHORT).show();

            // Do something
            return true;
        }
        if (id == R.id.action_item_call) {
            startActivity(new Intent(getApplicationContext(),SyrowWebViewChat.class));
            Toast.makeText(getApplicationContext(),"Audio call will be available soon",Toast.LENGTH_SHORT).show();
            // Do something
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
