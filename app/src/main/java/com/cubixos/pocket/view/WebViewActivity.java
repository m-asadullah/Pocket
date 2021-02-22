package com.cubixos.pocket.view;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cubixos.pocket.accessories.misc.CheckInternet;
import com.cubixos.pocket.R;
import com.google.android.material.appbar.AppBarLayout;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebViewActivity extends AppCompatActivity {

    String TAG = "WebViewActivity";

    Toolbar toolbar;
    AppBarLayout appBarLayout;
    WebView webView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    ProgressBar progressBarHorizontal;
    FrameLayout frameLayout;

    String urlShare;
    String urlDefault;
    String urlTitle;
    String urlIntentGet;
    URL webURLIntent;

    TextView textViewTitleWeb;
    TextView textViewLinkWeb;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        //
        appBarLayout = findViewById(R.id.appbar_layout_web);
        toolbar = findViewById(R.id.toolbar_web);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //
        textViewTitleWeb = findViewById(R.id.text_view_title_web);
        textViewTitleWeb.setVisibility(View.GONE);

        textViewLinkWeb = findViewById(R.id.text_view_link_web);
        textViewLinkWeb.setText(getIntent().getStringExtra("webUrl"));
        //
        progressBar = findViewById(R.id.progress_bar_web);
        progressBarHorizontal = findViewById(R.id.progress_bar_horizontal_web);
        frameLayout = findViewById(R.id.frame_layout_web);
        //
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri uridata = intent.getData();
        //
        //final String webURL = getIntent().getStringExtra("webUrl");
        String webURL = getIntent().getStringExtra("webUrl");
        //urlShare = webURL;
        urlDefault = "https://shoutout.wix.com/so/27NBmsJJ6";
        //
        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        progressBarHorizontal.setMax(100);
        //url = "https://cubixos.com/math";
        //
        if (uridata != null) {
            try {
                webURLIntent = new URL(uridata.getScheme(), uridata.getHost(), uridata.getPath());
                Toast.makeText(getApplicationContext(), "Link:\n"+webURLIntent+"\n"+uridata.getScheme()+"\n"+uridata.getHost()+"\n"+uridata.getPath()+"\n"+webURLIntent.toString(),Toast.LENGTH_SHORT).show();
            }catch (MalformedURLException e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error:\n"+e.getMessage() ,Toast.LENGTH_SHORT).show();
            }
            webView.loadUrl(webURLIntent.toString());
        }else if (webURL != null) {
            webView.loadUrl(webURL); // load a web page in a web view
        }else {
            webView.loadUrl(urlDefault);
        }
        //
        /**
        if (webURL != null) {
            webView.loadUrl(webURL); // load a web page in a web view
        }else {
            webView.loadUrl(urlDefault);
        }**/
        progressBarHorizontal.setProgress(0);
        webView.setWebViewClient(new MyWebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                textViewTitleWeb.setVisibility(View.GONE);
                textViewLinkWeb.setVisibility(View.VISIBLE);
                textViewLinkWeb.setText(view.getUrl());
                textViewLinkWeb.setText(view.getUrl());
                urlShare = url;
                urlTitle = view.getTitle();
                progressBarHorizontal.setProgress(0);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                textViewTitleWeb.setVisibility(View.VISIBLE);
                textViewTitleWeb.setText(view.getTitle());
                textViewLinkWeb.setVisibility(View.VISIBLE);
                textViewLinkWeb.setText(view.getUrl());
                urlShare = url;
                urlTitle = view.getTitle();
                progressBarHorizontal.setProgress(100);
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                Toast.makeText(getApplicationContext(),"commit visible",Toast.LENGTH_SHORT).show();
                //urlShare = url;
                //urlTitle = view.getTitle();
                super.onPageCommitVisible(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress){
                frameLayout.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
                if (progress == 100){
                    frameLayout.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, progress);
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                //for downloading directly through download manager
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                //This three lines will do your work
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(url);     // which is "http://website.com"
                request.addRequestHeader("Cookie", cookie);
                //
                request.allowScanningByMediaScanner();
                Environment.getExternalStorageDirectory();
                getApplicationContext().getFilesDir().getPath(); //which returns the internal app files directory path
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Uri.parse(url).getLastPathSegment());//set filename original from path last segment
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
            }
        });
        swipeRefreshLayout = findViewById(R.id.swiperefresh_layout_web);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,R.color.colorBlack,R.color.colorGreyDark);
                if (urlShare != null) { // replace webURL by urlShare
                    webView.loadUrl(urlShare);
                } else {
                    //webView.loadUrl(urlDefault);
                    Toast.makeText(getApplicationContext(), "Wait webpage is loading",Toast.LENGTH_SHORT).show();
                    //webView.loadUrl(urlShare);
                }
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            frameLayout.setVisibility(View.VISIBLE);
            //
            if(url.startsWith("intent://") && url.contains("scheme=http")){
                url = Uri.decode(url);
                webView.setVisibility(View.GONE);//webview gone when intent starts...
                progressBar.setVisibility(View.VISIBLE);
                String bkpUrl = null;
                Pattern regexBkp = Pattern.compile("intent://(.*?)#");
                Matcher regexMatcherBkp = regexBkp.matcher(url);
                if (regexMatcherBkp.find()) {
                    bkpUrl = regexMatcherBkp.group(1);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+bkpUrl));
                    startActivity(intent);
                    //load as a URL in own webview
                    //webView.loadUrl("http://"+bkpUrl);
                    return true;
                }else{
                    return false;
                }
            }
            webView.setVisibility(View.VISIBLE);//webview gone when intent stops...
            progressBar.setVisibility(View.GONE);
            //
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_web_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Website: "+urlTitle);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, urlShare);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;

        } else if (id == R.id.menu_web_refresh) {
            if (CheckInternet.isNetwork(getApplicationContext())){
                webView.loadUrl(urlShare);
            }else {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_twotone_perm_scan_wifi_24)
                        .setTitle("Network Error")
                        .setMessage("Internet Connection is not available. Check your Data connection or WiFi")
                        .setCancelable(false)
                        .setPositiveButton("Check", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Go to WiFi Setting - programmatically
                                Context context = getApplicationContext();
                                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                wifiManager.setWifiEnabled(true);
                                final Intent intentWifi = new Intent(Intent.ACTION_MAIN, null);
                                intentWifi.addCategory(Intent.CATEGORY_LAUNCHER);
                                final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                                intentWifi.setComponent(cn);
                                intentWifi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity( intentWifi);
                            }
                        })
                        //set negative button
                        .setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //dismiss
                                Toast.makeText(getApplicationContext(),"There should be stable internet connection", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        })
                        .show();
            }
            return true;

        }else if (id == R.id.menu_web_copy) {
            // Creates a new text clip to put on the clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("website link", urlShare);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(),"Website link copied\n"+urlShare, Toast.LENGTH_SHORT).show();
            return true;

        }else if (id == R.id.menu_web_open_with) {
            Intent openWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlShare));
            Intent intentChooser = Intent.createChooser(openWebIntent, "Open with");
            if (openWebIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(intentChooser);
            }
            return true;

        }else if (id == R.id.menu_web_save_as_pdf) {
            createWebPagePrint(webView, urlTitle);
            return true;

        }else if (id == R.id.menu_web_print) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Print - Web
    public  void createWebPagePrint(WebView webView, String webTitle) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
        String jobName = getString(R.string.app_name) + "Website";
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A3);
        PrintJob printJob = printManager.print(jobName, printAdapter, builder.build());
        //
        if(printJob.isCompleted()){
            Toast.makeText(getApplicationContext(), "Print Complete", Toast.LENGTH_LONG).show();
        }
        else if(printJob.isFailed()){
            Toast.makeText(getApplicationContext(), "Print Failed", Toast.LENGTH_LONG).show();
        }
        // Save the job object for later status checking
    }

}