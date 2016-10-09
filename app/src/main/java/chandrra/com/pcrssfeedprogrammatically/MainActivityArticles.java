package chandrra.com.pcrssfeedprogrammatically;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivityArticles extends AppCompatActivity {
    RecyclerView recyclerView;
    Context context = this;
    ArrayList<PCRssFeedItems> pcRssFeedItems;
    CustomAdapter adapter;
    ProgressDialog progressDialog;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //Context context = getApplicationContext();

        CoordinatorLayout coordinatorLayout = new CoordinatorLayout(this);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);

        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(params);
        recyclerView.setId(R.id.recyclerP);

        Toolbar toolbar =  createToolbar();

        toolbar.setTitle("PC RSS Feed Articles");
        setSupportActionBar(toolbar);

        fab = createFAB();
        coordinatorLayout.addView(fab);
        linearLayout.addView(toolbar);
        linearLayout.addView(recyclerView);
        coordinatorLayout.addView(linearLayout);

        setContentView(coordinatorLayout);

/**
 * Checking if device is connected
 */
        if (isConnected(getApplicationContext())) {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerP);
            ReadPCRssFeed readPCRssFeed = new ReadPCRssFeed(this, recyclerView);
            readPCRssFeed.execute();
            //Getting values from async task
            try {
                pcRssFeedItems = readPCRssFeed.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //CustomAdapter
            adapter = new CustomAdapter(context, pcRssFeedItems);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 50, true));
            GridLayoutManager gridLayoutManager;
            /**
             * Checking if device is a tablet and assigning span type for grids so that for
             * tablet 3 cells will be displayed
             */
            if (isTablet(getApplicationContext())) {
                gridLayoutManager = new GridLayoutManager(context, 3);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                    @Override
                    public int getSpanSize(int position) {
                        if (position == 0) {
                            return 3;
                        } else {
                            return 1;
                        }
                    }
                });
            } else {

                gridLayoutManager = new GridLayoutManager(context, 2);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                    @Override
                    public int getSpanSize(int position) {
                        if (position == 0) {
                            return 2;
                        } else {
                            return 1;
                        }
                    }
                });
            }
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.addItemDecoration(new Spacing(20));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();
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
//        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
//            //return "Tablet";
//            return true;
//        }else{
//            //return "Mobile";
//            return false;
//        }
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
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
                ViewGroup.LayoutParams.MATCH_PARENT,dpTopxs(50));
        //TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        toolbar.setLayoutParams(toolbarParams);
        toolbar.setBackgroundColor(Color.BLUE);
        int toolBarBackground = getResources().getColor(R.color.colorPrimary);
        toolbar.setBackgroundColor(toolBarBackground);
        toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setTitle("Hello");
        toolbar.setVisibility(View.VISIBLE);
        return toolbar;
    }
    public FloatingActionButton createFAB() {
        fab = new FloatingActionButton(this);
        fab.setId(R.id.fabProgrammatically);
//        //(FloatingActionButton) findViewById(R.id.fab);
        CoordinatorLayout.LayoutParams lp = new  CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        //lp.anchorGravity = Gravity.TOP | GravityCompat.END;
        lp.gravity = Gravity.BOTTOM | Gravity.END;
        fab.setLayoutParams(lp);
        fab.setSize(FloatingActionButton.SIZE_NORMAL);
        fab.setImageResource(R.drawable.refresh);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor("#D5741B")));
        fab.setUseCompatPadding(true);
//        fab.setBackgroundColor(Color.BLACK);
//fab.bringToFront();

//        fab.setVisibility(view.VISIBLE);
//        fab.show();


        fab.setRippleColor(Color.GREEN);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Implement Refresh here
                 */
                refreshRSSFeed();
                Log.d("clicked", "fab");
            }
        });
        return fab;
    }

    /**
     *
     * Fetching details asynchronously.
     */
    public class ReadPCRssFeed extends AsyncTask<Void, Void, ArrayList<PCRssFeedItems>> {
        Context context;
        String address = "https://blog.personalcapital.com/feed/?cat=3,891,890,68,284";
        RecyclerView recyclerView;
        URL url;

        @Override
        protected void onCancelled(ArrayList<PCRssFeedItems> pcRssFeedItemses) {
            super.onCancelled(pcRssFeedItemses);
        }

        public ReadPCRssFeed(Context context, RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            this.context = context;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading RSS feed...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected ArrayList doInBackground(Void... params) {
            pcRssFeedItems = parseXml(getData());
            return pcRssFeedItems;
        }

        /**
         * Parsing xml feed data
         * @param data
         */
        private ArrayList parseXml(Document data) {
            if (data != null) {
                pcRssFeedItems = new ArrayList<>();
                Element rootElement = data.getDocumentElement();
                Node channel = rootElement.getChildNodes().item(1);
                NodeList items = channel.getChildNodes();
                for (int i = 0; i < items.getLength(); i++) {
                    Node currentchild = items.item(i);
                    if (currentchild.getNodeName().equalsIgnoreCase("item")) {
                        PCRssFeedItems item = new PCRssFeedItems();
                        NodeList itemchilds = currentchild.getChildNodes();
                        for (int j = 0; j < itemchilds.getLength(); j++) {
                            Node current = itemchilds.item(j);
                            if (current.getNodeName().equalsIgnoreCase("title")) {
                                item.setTitle(current.getTextContent());
                            } else if (current.getNodeName().equalsIgnoreCase("description")) {
                                item.setDescription(current.getTextContent());
                            } else if (current.getNodeName().equalsIgnoreCase("pubDate")) {
                                item.setPubDate(current.getTextContent());
                            } else if (current.getNodeName().equalsIgnoreCase("link")) {
                                item.setLink(current.getTextContent());
                            } else if (current.getNodeName().equalsIgnoreCase("media:content")) {
                                String imageUrl = current.getAttributes().item(0).getTextContent();
                                item.setImageUrl(imageUrl);

                            }

                        }
                        pcRssFeedItems.add(item);
                    }
                }



            }
            return pcRssFeedItems;

        }

        /**
         * Getting document from URL
         * @return
         */
        public Document getData() {
            try {
                url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(inputStream);
                return document;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
        /**
         * RecyclerView item decoration - give equal margin around grid item
         */


        private ArrayList result(ArrayList myValue) {
            //handle value
            return myValue;
        }

    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
    }

    /**
     * Update/Refresh RssFeedContent
     */
    public void refreshRSSFeed() {
        ReadPCRssFeed readPCRssFeed = new ReadPCRssFeed(this, recyclerView);
        /**
         * Content can also be updated using notifyDataSetChanged
         */
//        readPCRssFeed.execute();
//        ArrayList<PCRssFeedItems> pcRssFeedItemsRefresh = null;
//        try {
//            pcRssFeedItemsRefresh = readPCRssFeed.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//       recyclerView.invalidate();
//        recyclerView.removeAllViews();
        //pcRssFeedItems.remove(1);
//        pcRssFeedItems.clear();
//        pcRssFeedItems.addAll(pcRssFeedItemsRefresh);
//        adapter.notifyDataSetChanged();
//        progressDialog.dismiss();

        //Working solution by setting a new adapter
        /**
         * Updating Content
         */
        readPCRssFeed = new ReadPCRssFeed(this, recyclerView);
        readPCRssFeed.execute();
        try {
            pcRssFeedItems = readPCRssFeed.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //pcRssFeedItems.remove(1);

        adapter = new CustomAdapter(context, pcRssFeedItems);
        recyclerView.setAdapter(adapter);
        progressDialog.dismiss();
    }
    public int dpTopxs(int dps) {
        //final float scale = getContext().getResources().getDisplayMetrics().density;
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }


}