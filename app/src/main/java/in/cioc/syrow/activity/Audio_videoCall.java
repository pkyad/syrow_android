package in.cioc.syrow.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import in.cioc.syrow.R;

public class Audio_videoCall extends AppCompatActivity {
    WebView webView;
    String arr [];
    String html;
    String audioTrue="https://socket.syrow.com/" + ChatRoomActivity.millSec + "?audio_video=audio&windowColor=96231d&agent=true";
    String videoTrue="https://socket.syrow.com/" + ChatRoomActivity.millSec + "?audio_video=audio&windowColor=96231d&agent=true";
    String iframe;


    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;

    private PermissionRequest myRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_video_call);
        getSupportActionBar().hide();
        webView = findViewById(R.id.Audio_Video);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccess(true);

       // webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new WebAppInterface(this),"Platform_id");




       /* webView.setWebChromeClient(new WebChromeClient(){
            // Need to accept permissions to use the camera
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
       webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                Audio_videoCall.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        request.grant(request.getResources());
                        Toast.makeText(Audio_videoCall.this, " permission", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                Log.e("cancel","permission canceled");
                Toast.makeText(Audio_videoCall.this, "declined permission", Toast.LENGTH_SHORT).show();
            }
        });*/
        checkAudioVideo();

    }
    private class WebAppInterface {

        public WebAppInterface(Context context) {
        }

        @JavascriptInterface
        public void openCamera() {
            //check permissions and open camera intent;
        }
    }



    public void checkAudioVideo() {
       String ar=ChatRoomActivity.millSec;

        if (ChatRoomActivity.audio_vide0.equals("audio")) {

            html = "https://socket.syrow.com/"+ar+"?audio_video=audio&windowColor=96231d&agent=false";
           // iframe ="<iframe width=\"450\" height=\"260\" style=\"border: 1px solid #cccccc;\" src=\"https://socket.syrow.com/"+ChatRoomActivity.millSec+"?audio_video=audio&windowColor=96231d&agent=false\" ></iframe>";
          //  webView.loadData(iframe,"text/html",null);
            webView.loadUrl(html);
            ChatRoomActivity.session.publish("uniqueKey.service.support.agent", ar, "AC", arr, 1, audioTrue);

        } else {

            html = "https://socket.syrow.com/"+ar+"?audio_video=video&windowColor=96231d&agent=false";
          //  iframe ="<iframe width=\"450\" height=\"260\" style=\"border: 1px solid #cccccc;\" src=\"https://socket.syrow.com/"+ChatRoomActivity.millSec+"?audio_video=video&windowColor=96231d&agent=false\" ></iframe>";
           // webView.loadData(iframe,"text/html", null);
            webView.loadUrl(html);
            ChatRoomActivity.session.publish("uniqueKey.service.support.agent", ar, "VCS", arr, 1, videoTrue);

            }
    }

}
