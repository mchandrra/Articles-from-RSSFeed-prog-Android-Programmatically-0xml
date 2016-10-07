package chandrra.com.pcrssfeedprogrammatically;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainActivityArticles extends AppCompatActivity {

    View view;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(params);
        recyclerView.setId(R.id.recyclerP);

        Toolbar toolbar =  createToolbar();

        toolbar.setTitle("PC RSS Feed Articles");
        setSupportActionBar(toolbar);
        linearLayout.addView(toolbar);
        linearLayout.addView(recyclerView);

        setContentView(linearLayout);
/**
 * Checking if device is connected
 */
        if (isConnected(getApplicationContext())) {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerP);
            ReadPCRssFeed readPCRssFeed = new ReadPCRssFeed(this, recyclerView);
            readPCRssFeed.execute();
        } else {
            showConnectionDialog();
        }
    }

    /**
     * To check if device is a Tablet.
     * @param context
     * @return
     */
    public boolean isTablet (Context context) {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            //return "Tablet";
            return true;
        }else{
            //return "Mobile";
            return false;
        }

    }

    /**
     * To check if device is connected to Internet
     * @param context
     * @return
     */
    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Shows dialog if not connected to internet
     */
    private void showConnectionDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Do you want to connect to WiFi?")
                .setTitle("No Internet access");

        // Add the buttons
        builder.setPositiveButton("Connect to WiFi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                finish();
            }
        });
        // Set other dialog properties
        //Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Creating Toolbar programmatically
     * @return
     */

    public Toolbar createToolbar() {
        Toolbar toolbar = new Toolbar(this);
        LinearLayout.LayoutParams toolbarParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,150);
        toolbar.setLayoutParams(toolbarParams);
        toolbar.setBackgroundColor(Color.BLUE);
        int toolBarBackground = getResources().getColor(R.color.colorPrimary);
        toolbar.setBackgroundColor(toolBarBackground);
        toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setTitle("Hello");
        toolbar.setVisibility(View.VISIBLE);
        return toolbar;
    }
}
