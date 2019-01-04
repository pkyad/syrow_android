package in.cioc.syrow.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import in.cioc.syrow.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // startActivity(new Intent(this, ChatRoomActivity.class));
        isAudioVideoPermissionGranted();
    }



   private  boolean isAudioVideoPermissionGranted(){
       if(Build.VERSION.SDK_INT>23){
           if(checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS)==PackageManager.PERMISSION_GRANTED
                   &&checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
                   &&checkSelfPermission(Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED
                   &&checkSelfPermission(Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED
                   ){
               return  true;
           }else{
               ActivityCompat.requestPermissions(this, new String[]{
                       Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE ,
                       Manifest.permission.MODIFY_AUDIO_SETTINGS , Manifest.permission.PROCESS_OUTGOING_CALLS,
                       Manifest.permission.RECORD_AUDIO}, 1);
               return false;
           }
       }else{
           return true;
       }
   }
    public void nextPage(View v){
        startActivity(new Intent(this, ChatRoomActivity.class));
    }

}
