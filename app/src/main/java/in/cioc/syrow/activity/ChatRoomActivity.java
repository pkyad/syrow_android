package in.cioc.syrow.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebMessage;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import cz.msebera.android.httpclient.Header;
import in.cioc.syrow.Backend;
import in.cioc.syrow.R;
import in.cioc.syrow.adapter.ChatRoomThreadAdapter;
import in.cioc.syrow.app.Config;
import in.cioc.syrow.helper.MyPreferenceManager;
import in.cioc.syrow.helper.Utility;
import in.cioc.syrow.model.ChatThread;
import in.cioc.syrow.model.MediaMessage;
import in.cioc.syrow.model.Message;
import in.cioc.syrow.model.User;
import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.EventDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.InvocationDetails;
import io.crossbar.autobahn.wamp.types.Registration;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;

public class ChatRoomActivity extends AppCompatActivity {

    private String TAG = ChatRoomActivity.class.getSimpleName();
    static Session session;
    Client client1;
    CompletableFuture<ExitInfo> exitInfoCompletableFuture;
    public static String chatRoomId, path, base64="", userChooseTask, millSec;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private TextView userName;
    private View offLineAndOnLine;
    private ImageView btnSend, btnAttach, userImage;
    private AsyncHttpClient client;
    boolean thread = true;
    private static final int READ_REQUEST_CODE = 42;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1,  choose;
    Bitmap bitmap,bitmap1;
    Context context;
    public static final long INTERVAL = 1000 * 25;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    TimerTask timerTask;

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    String companyID = "1", msgPk="";
    MyPreferenceManager manager;
    String Rating_feedback="";
    private Uri filepath;

    public static String audio_vide0="";
    WebView webView;
    File file;
    String arr [];
    ImageView call_Cancel;
    String html;
    String audioTrue="https://socket.syrow.com/" + ChatRoomActivity.millSec + "?audio_video=audio&windowColor=96231d&agent=true";
    String videoTrue="https://socket.syrow.com/" + ChatRoomActivity.millSec + "?audio_video=video&windowColor=96231d&agent=true";
    String iframe;
    MenuItem register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        this.context = ChatRoomActivity.this;
        this.context = context;
        sp = context.getSharedPreferences("registered_status", Context.MODE_PRIVATE);
        spe = sp.edit();
        manager = new MyPreferenceManager(context);

        client = new AsyncHttpClient();
        choose=1;
        inputMessage = findViewById(R.id.message);
        btnSend = findViewById(R.id.btn_send);
        btnAttach = findViewById(R.id.btn_attach);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userName = findViewById(R.id.user_name);
        userImage = findViewById(R.id.user_image);
        offLineAndOnLine = findViewById(R.id.off_on_line);

        Intent intent = getIntent();
        chatRoomId = intent.getStringExtra("chat_room_id");

//        getSupportActionBar().setTitle("Syrow");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_arrow_back_white_24dp, getApplicationContext().getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackForm();
            }
        });


        if (mTimer != null)
            mTimer.cancel();
        else
            mTimer = new Timer(); // recreate new timer
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, INTERVAL);

        if (chatRoomId == null) {
            Toast.makeText(getApplicationContext(), "Chat room not found!", Toast.LENGTH_SHORT).show();
            //finish();
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        messageArrayList = new ArrayList<>();


        // self user id is to identify the message owner
        String selfUserId = "3333";

        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, selfUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("");
            }
        });

        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        millSec = sp.getString("millSec", null);

        if (millSec == null){
            resetUID();
        }

        fetchChatThread();
        session = new Session();
        session.addOnJoinListener(this::demonstrateSubscribe);
        //client1 = new Client(session, "ws://wamp.cioc.in:8090/ws", "default");
        client1 = new Client(session, "ws://ws.syrow.com:8080/ws", "default");
        exitInfoCompletableFuture = client1.connect();

        if (Build.VERSION.SDK_INT >= 11) {
            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(
                                        recyclerView.getAdapter().getItemCount() - 1);
                            }
                        }, 100);
                    }
                }
            });
        }



        client.get(Backend.url + "/api/support/customerProfile/?service=" + companyID , new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    JSONObject compProfile = response.getJSONObject(0);

                    String firstMessage = compProfile.getString("firstMessage");
                    userName.setText(compProfile.getString("name"));


                    Date todaysDate = new Date();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                    Message message = new Message();
                    message.setUid(millSec);
                    message.setSentByAgent(true);
                    message.setMessage(firstMessage);
                    message.setUser("00");
                    message.setCreated(df.format(todaysDate));
                    messageArrayList.add(message);
                    mAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(ChatRoomActivity.this, "onFailure "+thread, Toast.LENGTH_SHORT).show();
            }

        });
//        resetUID();
       // createWebView();

    }

    private void createWebView() {
        webView= findViewById(R.id.WebView_Syrow);
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
                ChatRoomActivity.this.runOnUiThread(new Runnable() {
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
           // webView.loadData(iframe,"text/html","UTF-8");
            webView.loadUrl(html);
            webView.setVisibility(View.VISIBLE);
            ChatRoomActivity.session.publish("uniqueKey.service.support.agent" , uid, "AC", arr, 1, audioTrue);

        } else {
            html = "https://socket.syrow.com/"+uid+"?audio_video=video&windowColor=96231d&agent=false";
            // iframe ="<iframe id=\"iFrame1\" src=\"https://socket.syrow.com/"+uid+"?audio_video=video&amp;windowColor=3465fc&amp;agent=false\" style=\"width: 50%; height: 50%; border: medium none;\" scrolling=\"no\" allowfullscreen=\"\" allow=\"geolocation; microphone; camera\"></iframe>";
            // webView.loadData(iframe,"text/html", "UTF-8");
            webView.loadUrl(html);
            webView.setVisibility(View.VISIBLE);
            ChatRoomActivity.session.publish("uniqueKey.service.support.agent",uid, "VCS", arr, 1, videoTrue);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.app_bar_menu,menu);
      register= menu.findItem(R.id.cancelCallbtn);
      return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.videoCall:
                Toast.makeText(context, "video call ", Toast.LENGTH_SHORT).show();
                audio_vide0="video";
                createWebView();
                register.setVisible(true);
              //  startActivity(new Intent(getApplicationContext(),Audio_videoCall.class));
                return true;
            case R.id.audioCall:
                audio_vide0="audio";
                register.setVisible(true);
                 createWebView();
              //  startActivity(new Intent(getApplicationContext(),Audio_videoCall.class));
                Toast.makeText(context, "audio call", Toast.LENGTH_SHORT).show();
                return  true;
            case R.id.cancelCallbtn:
                register.setVisible(false);
              //  webView.postWebMessage(new WebMessage("userleft"),Uri.parse(null));
                webView.setVisibility(View.GONE);
            default:
                return  super.onOptionsItemSelected(item);
        }

   }

    public void showFeedbackForm(){

        View v = getLayoutInflater().inflate(R.layout.layout_feedback_rating, null, false);
        Button btnCancel = v.findViewById(R.id.action_btn_cancel);
        Button btnSubmit = v.findViewById(R.id.action_btn_submit);
        RatingBar ratingFeedback = v.findViewById(R.id.rating_bar);
        EditText feedbackText = v.findViewById(R.id.feedback_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(v);
        AlertDialog ad = builder.create();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();

                message.setUid(millSec);
                message.setSentByAgent(true);
                message.setMessage("Chat has been closed");
                messageArrayList.add(message);
                mAdapter.notifyDataSetChanged();
                if (!(message.getMessage().equals("null"))){
                    session.publish("uniqueKey.service.support.agent", millSec, "M", message);
                }
                manager.clear();
                ad.dismiss();
                finish();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedback = feedbackText.getText().toString().trim();
                int rating = (int) ratingFeedback.getRating();
                RequestParams threadParams = new RequestParams();
                threadParams.put("customerRating", rating);
                threadParams.put("customerFeedback",feedback);
                threadParams.put("status","closed");
                Rating_feedback = "Feedback : "+feedback+"\n Rating : "+rating;
               // Toast.makeText(getApplicationContext(), Rating_feedback, Toast.LENGTH_SHORT).show();
               // manager.clear();
                ad.dismiss();
                inputMessage.setEnabled(false);
                btnAttach.setVisibility(View.GONE);

                Message message = new Message();

                message.setUid(millSec);
                message.setSentByAgent(true);
                message.setMessage("Chat has been closed\n\n Rating : "+rating+"\n FeedBack :"+feedback);
                messageArrayList.add(message);
                mAdapter.notifyDataSetChanged();
                if (!(message.getMessage().equals("null"))){
                    session.publish("uniqueKey.service.support.agent", millSec, "M", message);
                }

                finish();
                RequestParams params= new RequestParams();
                params.put("message", Rating_feedback);
                params.put("sentByAgent", false);
                params.put("uid", millSec);
                client.post(Backend.url+"/api/support/supportChat/", params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                       Log.e("feedback","feedback saved");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(getApplicationContext(), "JSONObject error: " + statusCode, Toast.LENGTH_SHORT).show();

                    }
                });
            //chatThread post call
                client.patch(Backend.url+"/api/support/chatThread/"+manager.getChatThreadPK()+"/", threadParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Toast.makeText(context, "Feedback:updated "+rating+"\n"+feedback, Toast.LENGTH_SHORT).show();
                        Log.e("onSuccess", "chatThread- Feedback: "+rating+"\n"+feedback);
                        resetUID();
                        manager.clear();
                        ad.dismiss();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(context, "onFailure- Feedback: "+statusCode+" "+errorResponse, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        ad.show();
    }

    public void resetUID(){
        millSec = Long.toString(Calendar.getInstance().getTimeInMillis());
        spe.putString("millSec" , millSec );
        spe.apply();
    }

    private String add2(List<Integer> args, InvocationDetails details) {
        return millSec;
    }

    public void demonstrateSubscribe(Session session, SessionDetails details) {
        CompletableFuture<Subscription> subFuture = session.subscribe("uniqueKey.service.support.chat." + millSec,
                this::onEvent);
        subFuture.whenComplete((subscription, throwable) -> {
            if (throwable == null) {
                System.out.println("Subscribed to topic " + subscription.topic);
                Toast.makeText(getApplicationContext(), "Subscribed", Toast.LENGTH_SHORT).show();
            } else {
                throwable.printStackTrace();
            }
        });


        CompletableFuture<Registration> regFuture = session.register("uniqueKey.service.support.heartbeat." + millSec , this::add2);
        regFuture.thenAccept(registration ->
                System.out.println("Successfully registered procedure: " + registration.procedure));

    }

    private void onEvent(List<Object> args, Map<String, Object> kwargs, EventDetails details) {

        System.out.println(String.format("Got event: %s", args.get(0)));
        Toast.makeText(getApplicationContext(), "event "+args.get(0), Toast.LENGTH_SHORT).show();

        // add a notification strip here

        Object[] argsMap = ((LinkedHashMap)args.get(1)).entrySet().toArray();


        String agentName = args.get(2).toString();
        String msgTyp = args.get(0).toString();

//        getSupportActionBar().setTitle( agentName.substring(0, 1).toUpperCase() + agentName.substring(1));
        userName.setText(agentName.substring(0, 1).toUpperCase() + agentName.substring(1));


        try{
            if (msgTyp.equals("M") || msgTyp.equals("ML")){
                try {
                    User user = new User( ((LinkedHashMap.Entry <String ,Object> )argsMap[4]).getValue().toString() , "Agent", null);
                }catch(Exception e){
                    e.printStackTrace();
                }

                String msgPK = ((LinkedHashMap.Entry <String ,Object> )argsMap[0]).getValue().toString();

                client.get(Backend.url+"/api/support/supportChat/" + msgPK +"/", new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                        super.onSuccess(statusCode, headers, object);
                        User user = new User("self", "pkyad", null);
                        Message message = new Message();
                        try {
                            message.setPk(object.getString("pk"));
                            message.setUser(object.getString("user"));
                            message.setSentByAgent(object.getBoolean("sentByAgent"));
                            message.setMessage(object.getString("message"));
                            message.setAttachment(object.getString("attachment"));
                            message.setCreated(object.getString("created"));
                            message.setAttachmentType(object.getString("attachmentType"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        messageArrayList.add(message);
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }else{
                //http://syrow.cioc.in/api/support/supportChat/334/
                client.get(Backend.url+"/api/support/supportChat/" + ((LinkedHashMap.Entry <String ,Object> )argsMap[0]).getValue().toString() + "/" , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            Message message = new Message();
                            message.setPk(response.getString("pk"));
                            message.setUser(response.getString("user"));
                            message.setSentByAgent(response.getBoolean("sentByAgent"));
                            message.setMessage(response.getString("message"));
                            message.setAttachment(response.getString("attachment"));
                            message.setCreated(response.getString("created"));
                            message.setAttachmentType(response.getString("attachmentType"));
                            messageArrayList.add(message);

                            mAdapter.notifyDataSetChanged();
                            if (mAdapter.getItemCount() > 1) {
                                // scrolling to bottom of the recycler view
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(ChatRoomActivity.this, "onFailure "+thread, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        Message message = (Message) intent.getSerializableExtra("message");
        String chatRoomId = intent.getStringExtra("chat_room_id");

        if (message != null && chatRoomId != null) {
            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
            }
        }
    }

    private void selectImage() {
        View v = getLayoutInflater().inflate(R.layout.layout_gallery_and_camera, null, false);
        ImageView btnCamera = v.findViewById(R.id.btn_camera);
        ImageView btnGallery = v.findViewById(R.id.btn_gallery);
        ImageView btnDocument = v.findViewById(R.id.document);
        ImageView btnAudio =v.findViewById(R.id.audioResource);
        ImageView btnVideo = v.findViewById(R.id.videoResource);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(v);
        AlertDialog ad = builder.create();
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIntent();
                ad.dismiss();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryIntent();
                ad.dismiss();
            }
        });
        btnDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentIntent();
                ad.dismiss();
            }

        });
        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioIntent();
                ad.dismiss();
            }
        });
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoIntent();
                ad.dismiss();
            }
        });
        ad.show();
    }

    /**
     * Posting a new message in chat room
     * will make an http call to our server. Our server again sends the message
     * to all the devices as push notification
     * */

    private void sendMessage(String msg) {
        String message="";
        if(Rating_feedback.equals("")){
             message = this.inputMessage.getText().toString().trim();
            this.inputMessage.setText("");
        }else {
             message = Rating_feedback;
            this.inputMessage.setText("");
        }

        RequestParams params = new RequestParams();
        if (msg.equals("")) {
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(getApplicationContext(), "Enter a message ", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(message.contains("www.youtube.com")){
                String youtube[] = message.split("=");
                message="https://www.youtube.com/embed/"+youtube[1];
                params.put("message", message);
                params.put("sentByAgent", false);
                params.put("attachmentType", "youtubeLink");
                params.put("uid", millSec);
            }else {
                params.put("message", message);
                params.put("sentByAgent", false);
                params.put("uid", millSec);
            }
        }
        else if (bitmap!=null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                byte[] image = output.toByteArray();
                params.put("attachment", new ByteArrayInputStream(image), msg);
                params.put("sentByAgent", false);
                params.put("attachmentType", "image");
                params.put("uid", millSec);
            } else  {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, output);
                    byte[] image = output.toByteArray();
                    params.put("attachment", new ByteArrayInputStream(image), msg);
                    params.put("sentByAgent", false);
                    params.put("attachmentType", "application");
                    params.put("uid", millSec);
                }




        client.post(Backend.url+"/api/support/supportChat/", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                try {
                    String userId = object.getString("uid");
                    Message message = new Message();
                    message.setPk(object.getString("pk"));
                    message.setUid(object.getString("uid"));
//                    message.setUser(object.getString("user"));
                    message.setSentByAgent(object.getBoolean("sentByAgent"));
                    message.setMessage(object.getString("message"));
                    message.setAttachment(object.getString("attachment"));
                    message.setCreated(object.getString("created"));
                    message.setAttachmentType(object.getString("attachmentType"));
                    messageArrayList.add(message);
                    if (!(message.getMessage().equals("null"))){
                        session.publish("uniqueKey.service.support.agent", userId, "M", message);
                    }else{

                        MediaMessage mm = new MediaMessage();
                        mm.setFilePk(object.getString("pk"));
                        mm.setTyp("image");
                        mm.setUser(object.getString("user"));
                        session.publish("uniqueKey.service.support.agent", userId, "MF", mm);
                    }



//                    (3) […]
//​
//                    0: "1535747475253"
//​
//                    1: "MF"
//​
//                    2: {…}
//​​
//                    filePk: 327
//​​
//                    typ: "image"
//​​
//                    user: 1
//​​



                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.getItemCount() > 1) {
                        // scrolling to bottom of the recycler view
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "JSONObject error: " + statusCode, Toast.LENGTH_SHORT).show();

            }
        });

        RequestParams threadParams = new RequestParams();
        threadParams.put("company", companyID);
        threadParams.put("uid",millSec);

        if (manager.getStatus()){
            client.post(Backend.url+"/api/support/chatThread/", threadParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    manager.setStatus(false);
                    Log.e("onSuccess", "chatThread");
                    try {
                        String pk = response.getString("pk");
                        manager.setChatThreadPK(pk);
                        new ChatThread(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(ChatRoomActivity.this, "onFailure "+thread, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChooseTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChooseTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    documentIntent();
                    Log.e("permisision","document");
                }
                break;
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
       intent.setType("image/*");
       intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }
    private  void documentIntent(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent,"Select document"),SELECT_FILE);
    }
    private void audioIntent(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent,12);
    }
    private void videoIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,21);
    }

    public void multipart(){
        
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE){
                onSelectFromGalleryResult(data);

            Log.e("onActivity result", "document gallery");

        }
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }else if(requestCode == READ_REQUEST_CODE){
            onCaptureDocumentFormat(data);


        }
        else if(requestCode == 12){
            onAudioSelect(data);
            Log.e("audio Path",null);
        }else if(requestCode == 21){
            onVideoSelect(data);
            Log.e("video path",null);
        }
    }

    private void onVideoSelect(Intent data) {

    }
    private void onAudioSelect(Intent data){

    }


    public  void onCaptureDocumentFormat(Intent data){
       bitmap1 = (Bitmap) data.getExtras().get("data");
        path = data.getData().getPath();
        sendMessage(path);
        base64 = bitmapToBase64(bitmap1);
        Log.e("onCaptureDocumentFormat",""+path);
        Toast.makeText(context, ""+path, Toast.LENGTH_SHORT).show();
      file = new File(data.getData().getPath());
      System.out.print(data.getData().getPath());
      Log.e("document path", data.getData().getPath());
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMessage(destination.getAbsolutePath());

        base64 = bitmapToBase64(bitmap);
        Toast.makeText(context, ""+destination.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        Log.e("onCaptureImageResult",""+destination.getAbsolutePath());
    }

    private String bitmapToBase64(Bitmap bitmap) {
        byte[] byteArray = new byte[0];
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 65, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();

        }catch (Exception e){
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            path = data.getData().getPath();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("onSelectFromGalleryResult",""+path);
            Toast.makeText(context, ""+path, Toast.LENGTH_SHORT).show();
        }
        base64 = bitmapToBase64(bitmap);
        sendMessage(path);
    }

    /**
     * Fetching all the messages of a single chat room
     * */
    private void fetchChatThread() {
        client.get(Backend.url+"/api/support/supportChat/?uid=" + millSec, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        User user = new User("self", "pkyad", null);
                        Message message = new Message();
                        message.setPk(object.getString("pk"));
                        message.setUser(object.getString("user"));
                        message.setSentByAgent(object.getBoolean("sentByAgent"));
                        message.setMessage(object.getString("message"));
                        message.setAttachment(object.getString("attachment"));
                        message.setCreated(object.getString("created"));
                        message.setAttachmentType(object.getString("attachmentType"));
                        messageArrayList.add(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 1) {
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
        mAdapter.notifyDataSetChanged();
        if (mAdapter.getItemCount() > 1) {
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
        }
    }

    boolean res=false;

    @Override
    public void onBackPressed() {

        showFeedbackForm();
        if (res)
        super.onBackPressed();
    }
}
