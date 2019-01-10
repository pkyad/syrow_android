package in.cioc.syrow.activity;


import android.annotation.TargetApi;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import in.cioc.syrow.R;

public class Audio_videoCall extends AppCompatActivity {
    WebView webView;
    String arr [];
    ImageView call_Cancel;
    String html;
    String audioTrue="https://socket.syrow.com/" + ChatRoomActivity.millSec + "?audio_video=audio&windowColor=96231d&agent=true";
    String videoTrue="https://socket.syrow.com/" + ChatRoomActivity.millSec + "?audio_video=video&windowColor=96231d&agent=true";
    String iframe;

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;

    private PermissionRequest myRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_video_call);
        getSupportActionBar().hide();
        webView = findViewById(R.id.Audio_Video);
        call_Cancel = findViewById(R.id.cancel_Call);

      /*webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().getAllowContentAccess();
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.setFocusable(true);
        webView.getSettings().getLoadWithOverviewMode();
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); */


        /* webView.setWebChromeClient(new WebChromeClient() {
            // Need to accept permissions to use the camera
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
                Toast.makeText(Audio_videoCall.this, " permission not run", Toast.LENGTH_SHORT).show();
            }
        });*/

      /* webView.setWebChromeClient(new WebChromeClient() {
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
                Log.e("cancel", "permission canceled");
                Toast.makeText(Audio_videoCall.this, "declined permission", Toast.LENGTH_SHORT).show();
            }
        }); */

        call_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                webView.postWebMessage(null,Uri.parse(html));
                startActivity(new Intent(Audio_videoCall.this, ChatRoomActivity.class));
            }
        });


        creatWebView();

    }

    private void creatWebView() {
        setUpWebViewDefaults(webView);
        checkAudioVideo();
        webView.setWebChromeClient(new WebChromeClient() {

            public boolean onConsoleMessage(ConsoleMessage m) {
                Log.d("getUserMedia, WebView", m.message() + " -- From line "
                        + m.lineNumber() + " of "
                        + m.sourceId());

                return true;
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {

                // getActivity().
                Audio_videoCall.this.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        // Below isn't necessary, however you might want to:
                        // 1) Check what the site is and perhaps have a blacklist
                        // 2) Have a pop up for the user to explicitly give permission
                       // if(request.getOrigin().toString().equals("https://marcusbelcher.github.io/") ||
                           //     request.getOrigin().toString().equals("https://webrtc.github.io/")) {
                            request.grant(request.getResources());
                      //  } else {
                         //   request.deny();
                       // }
                    }
                });
            }
        });

    }

    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.clearCache(true);
        webView.clearHistory();
        webView.setWebViewClient(new WebViewClient());
    }

    public void checkAudioVideo() {
        String uid=ChatRoomActivity.millSec;

        if (ChatRoomActivity.audio_vide0.equals("audio")) {
            html = "https://socket.syrow.com/"+uid+"?audio_video=audio&windowColor=96231d&agent=false";
           //iframe ="<iframe id=\"iFrame1\" src=\"https://socket.syrow.com/"+uid+"?audio_video=audio&amp;windowColor=3465fc&amp;agent=false\" style=\"width: 50%; height: 50%; border: medium none;\" scrolling=\"no\" allowfullscreen=\"\" allow=\"geolocation; microphone; camera\"></iframe>";
            //webView.loadData(iframe,"text/html","UTF-8");
           webView.loadUrl(html);
           ChatRoomActivity.session.publish("uniqueKey.service.support.agent" , uid, "AC", arr, 1, audioTrue);

        } else {
            html = "https://socket.syrow.com/"+uid+"?audio_video=video&windowColor=96231d&agent=false";
          //  iframe ="<iframe id=\"iFrame1\" src=\"https://socket.syrow.com/"+uid+"?audio_video=video&amp;windowColor=3465fc&amp;agent=false\" style=\"width: 50%; height: 50%; border: medium none;\" scrolling=\"no\" allowfullscreen=\"\" allow=\"geolocation; microphone; camera\"></iframe>";
          //  webView.loadData(iframe,"text/html", "UTF-8");
             webView.loadUrl(html);
             ChatRoomActivity.session.publish("uniqueKey.service.support.agent",uid, "VCS", arr, 1, videoTrue);
        }
    }

}
