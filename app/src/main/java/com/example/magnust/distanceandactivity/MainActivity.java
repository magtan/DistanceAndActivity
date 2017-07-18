package com.example.magnust.distanceandactivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private final static String LOG = "MainActivity";
    private String activity = "STILL";
    private DistanceTracking distanceTracking;
    private boolean gpsFlag;

    private TextView stillText;
    private TextView walkingText;
    private TextView cyclingText;
    private TextView drivingText;
    private TextView mProbText;
    private TextView timerText;
    private Button btn;
    private static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Activity activityContext = (Activity) this;
        stillText = (TextView) findViewById(R.id.stillText);
        walkingText = (TextView) findViewById(R.id.walkingText);
        cyclingText = (TextView) findViewById(R.id.cyclingText);
        drivingText = (TextView) findViewById(R.id.drivingText);
        mProbText = (TextView) findViewById(R.id.mProbText);
        timerText = (TextView) findViewById(R.id.timerText);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);

        distanceTracking = new DistanceTracking(activityContext, this, stillText, walkingText,
                cyclingText, drivingText, mProbText, timerText);
        distanceTracking.setActivity(activity);
        gpsFlag = checkGpsStatus();
        if (gpsFlag) {

        } else {
            alertbox();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn:
                distanceTracking.updateActivity(true);
                break;

            case R.id.btn2:
                distanceTracking.updateActivity(false);
                //timerText.setText("Update canceled");
                break;


            default:
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        distanceTracking.start();
        registerReceiver(distanceTracking.getFenceReceiver(), new IntentFilter(FENCE_RECEIVER_ACTION));

    }

    @Override
    protected void onStop() {
        super.onStop();
        distanceTracking.stop();
        unregisterReceiver(distanceTracking.getFenceReceiver());
    }

    protected boolean checkGpsStatus() {
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }
    protected void alertbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disabled. Activate?")
                .setCancelable(false)
                .setTitle("Gps Status")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 2: { if ((grantResults.length > 0)
                            && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        distanceTracking.initiateLocation();
                    } else {
                        Toast.makeText(this, "The app needs to enable 'location' to do the calculations.",
                                Toast.LENGTH_LONG).show();
                    }
                    return;
                }
        }
    }
}