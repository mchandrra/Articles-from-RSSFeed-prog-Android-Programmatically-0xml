package chandrra.com.pcrssfeedprogrammatically;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by smallipeddi on 10/7/16.
 *
 * WebView implementation
 * To Load articles when they are clicked.
 */

public class ArticleWebView extends AppCompatActivity {
    WebView webView;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Creating Layouts and adding views programmatically
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(params);
        Toolbar toolbar = createToolbar();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        webView = new WebView(getApplicationContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        webView.setLayoutParams(params2);
        webView.setId(R.id.webviewP);

        Bundle bundle = getIntent().getExtras();
        String link = bundle.getString("link") + "?displayMobileNavigation=0";
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(link);

        TextView textView = createTextView();
        textView.setText(bundle.getString("title"));

        toolbar.addView(textView);
        frameLayout.addView(webView);
        linearLayout.addView(toolbar);
        linearLayout.addView(frameLayout);

        setContentView(linearLayout);



        this.webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                Log.d("link from url","" +url);
                view.loadUrl(url);
                return true;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        this.finish();
        return true;
    }

    /**
     * Creating custom Toolbar
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

    /**
     * Creating Textview Programatically
     * @return
     */
    public TextView createTextView() {
        TextView textView = new TextView(this);
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(Color.WHITE);
        return textView;
    }
}