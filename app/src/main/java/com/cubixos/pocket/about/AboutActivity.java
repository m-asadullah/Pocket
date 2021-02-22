package com.cubixos.pocket.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.cubixos.pocket.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class AboutActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppBarLayout appBarLayout;
    ImageView imageview_logo;
    AboutAdapter aboutAdapter;
    ViewPager viewPager;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //
        appBarLayout = findViewById(R.id.appbar_layout_about);
        toolbar = findViewById(R.id.toolbar_about);
        toolbar.setTitle("About us");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //
        TabLayout tabLayout = findViewById(R.id.tab_layout_about);
        tabLayout.addTab(tabLayout.newTab().setText("About"));
        tabLayout.addTab(tabLayout.newTab().setText("Services"));
        tabLayout.addTab(tabLayout.newTab().setText("Updates"));
        viewPager = findViewById(R.id.view_pager_about);
        aboutAdapter = new AboutAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(aboutAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        //
        imageview_logo = findViewById(R.id.image_view_about);
        imageview_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotation_a);
                imageview_logo.startAnimation(startRotateFrontAnimation);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_about_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Cubixos");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "For further information  \nClick this link to visit website \nhttps://bit.ly/cubixos");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
