package com.cubixos.pocket.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cubixos.pocket.R;
import com.cubixos.pocket.accessories.misc.WebViewStart;
import com.google.android.material.appbar.AppBarLayout;

public class InfoActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppBarLayout appBarLayout;
    String version = "1.0";
    TextView textViewVersion;
    Button buttonDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        appBarLayout = findViewById(R.id.appbar_layout_info);
        toolbar = findViewById(R.id.toolbar_info);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //
        textViewVersion = findViewById(R.id.text_view_version_info);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        textViewVersion.setText(version);
        buttonDownload = findViewById(R.id.button_download_info);
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewStart.startWebView(InfoActivity.this, getApplicationContext(),"http://bit.ly/math--app");
            }
        });
    }
}