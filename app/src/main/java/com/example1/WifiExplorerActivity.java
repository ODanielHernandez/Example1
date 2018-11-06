package com.example1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WifiExplorerActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_explorer);


        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    //check if connected!
                    while (!isConnected(WifiExplorerActivity.this)) {
                        //Wait to connect
                        Thread.sleep(1000);
                    }

                    Intent i = new Intent(WifiExplorerActivity.this, MapsActivity.class);
                    startActivity(i);

                } catch (Exception e) {
                }
            }
        };
        t.start();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }
}
