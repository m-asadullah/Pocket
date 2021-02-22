package com.cubixos.pocket.view;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.cubixos.pocket.accessories.misc.CheckInternet;
import com.cubixos.pocket.MainActivity;
import com.cubixos.pocket.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class VideoViewActivity extends AppCompatActivity {

    private static final int PERMISSION_ACCESS_EXTERNAL_STORAGE_REQUEST = 13;
    private WifiManager wifiManager;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    TextView textViewToolbarTitle;
    TextView textViewToolbarSubtitle;
    VideoView videoView;
    Uri uri; // Path to file
    File file; // File from path
    String filePath;
    //
    String title;
    String fileName;
    String fileSize;
    String fileDuration;
    String person;
    String status;//include or not
    String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        //
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //
        appBarLayout = findViewById(R.id.appbar_layout_video);
        toolbar = findViewById(R.id.toolbar_video);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        textViewToolbarTitle = findViewById(R.id.text_view_toolbar_title_video);
        textViewToolbarSubtitle = findViewById(R.id.text_view_toolbar_subtitle_video);
        textViewToolbarTitle.setText(getIntent().getStringExtra("videoTitle"));
        textViewToolbarSubtitle.setText(getIntent().getStringExtra("videoPerson"));
        //
        title= getIntent().getStringExtra("videoTitle");
        fileName = getIntent().getStringExtra("videoFileName");
        fileSize = getIntent().getStringExtra("videoSize");
        fileDuration = getIntent().getStringExtra("videoDuration");
        person = getIntent().getStringExtra("videoPerson");
        status = getIntent().getStringExtra("status");
        description = getIntent().getStringExtra("videoDescription");
        //
        filePath = Environment.getExternalStorageDirectory() + File.separator + "Math/Videos/" + fileName;
        //uri = Uri.parse(filePath);
        file = new File(filePath);
        //
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_ACCESS_EXTERNAL_STORAGE_REQUEST);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_ACCESS_EXTERNAL_STORAGE_REQUEST);
                }
            }
        }else{
            //Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
        }
        //
        videoView = findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.fromFile(file));
        videoView.setMediaController(new MediaController(this));
        //controller.setVisibility(View.GONE);
        videoView.requestFocus();
        videoView.start();
    }
    //
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video, menu);
        return true;
    }
    private boolean isChecked = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.menu_video_screen_on_switch);
        checkable.setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_video_screen_on_switch) {
            //isChecked = !item.isChecked();
            item.setChecked(isChecked);
            if(isChecked = !item.isChecked()){
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }else{
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            }
            return true;
        } else if (id == R.id.menu_video_share) {
            shareVideo();
            return true;

        } else if (id == R.id.menu_video_open) {
            openVideo();
            return true;

        } else if (id == R.id.menu_video_details) {
            showDialogDetailsVideo();
            return true;

        } else if (id == R.id.menu_video_error) {
            if (CheckInternet.isNetwork(getApplicationContext())){
                showDialogErrorVideo();
            }else {
                Toast.makeText(getApplicationContext(),"If file is not viewing then download it again to fix it.",Toast.LENGTH_SHORT).show();
            }
            return true;

        }else if (id == R.id.menu_video_report) {
            //
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Dialog - Error
    private void showDialogErrorVideo() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_pdf_error);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_bell);
        mediaPlayer.start();
        TextView textViewSize = dialog.findViewById(R.id.text_view_pdf_error_size);
        textViewSize.setText(fileSize);
        LinearLayout linearLayoutButtons = dialog.findViewById(R.id.linear_layout_buttons_dialog);
        ProgressBar progressBar = dialog.findViewById(R.id.progress_bar_dialog);
        ImageView imageViewSuccessTick = dialog.findViewById(R.id.image_button_success_dialog);
        linearLayoutButtons.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        imageViewSuccessTick.setVisibility(View.GONE);
        //
        dialog.findViewById(R.id.image_button_notifier_dialog).startAnimation(startRotateFrontAnimation);
        dialog.findViewById(R.id.image_button_close_dialog).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_negative_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_positive_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                if(CheckInternet.isNetwork(getApplicationContext())){
                    linearLayoutButtons.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    imageViewSuccessTick.setVisibility(View.GONE);
                    //Delete Video File
                    try{
                        new Timer().schedule(new TimerTask(){
                            public void run() { VideoViewActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    deleteVideo();
                                    notificationFileError();
                                    Toast.makeText(getApplicationContext(), "Download again that file to wiew. If still not viewing than report it to us.", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    imageViewSuccessTick.setVisibility(View.VISIBLE);
                                    try{
                                        new Timer().schedule(new TimerTask(){
                                            public void run() { VideoViewActivity.this.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    dialog.dismiss();
                                                    finish();//close activity
                                                }
                                            });
                                            }
                                        }, 1000);
                                    }catch (Exception e){
                                        FirebaseCrashlytics.getInstance().recordException(e);
                                    }
                                }
                            });
                            }
                        }, 8000);
                    }catch (Exception e){
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Error:207 - Internet Connection Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
    //delete pdf file from path
    public void deleteVideo(){
        File fdelete = file;
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted");
            } else {
                System.out.println("file not Delete");
            }
        }
    }
    //Dialog - Video Details
    private void showDialogDetailsVideo() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_video_details);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_bell);
        mediaPlayer.start();
        //
        TextView textViewFilename = dialog.findViewById(R.id.text_view_filename_dialog_video_details);
        TextView textViewFileDuration = dialog.findViewById(R.id.text_view_duration_dialog_video_details);
        TextView textViewFileSize = dialog.findViewById(R.id.text_view_size_dialog_video_details);
        TextView textViewTitle = dialog.findViewById(R.id.text_view_heading_dialog_video_details);
        TextView textViewDescription = dialog.findViewById(R.id.text_view_description_dialog_video_details);
        //
        textViewFilename.setText(fileName);
        textViewFileDuration.setText(fileDuration);
        textViewFileSize.setText(fileSize);
        textViewTitle.setText(title);
        textViewDescription.setText(description);
        //
        dialog.findViewById(R.id.image_button_icon_dialog).startAnimation(startRotateFrontAnimation);
        dialog.findViewById(R.id.image_button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.image_button_type_dialog).setVisibility(View.GONE);
        dialog.findViewById(R.id.image_button_type_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "This video is included in syllabus",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.findViewById(R.id.image_button_share_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareVideo();
            }
        });
        dialog.show();
    }
    //Video Share
    private void shareVideo() {
        Uri uriFilePathShare = FileProvider.getUriForFile(VideoViewActivity.this, "com.cubixos.pocket" + ".provider", file);
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("text/email");
        intentShare.putExtra(Intent.EXTRA_SUBJECT, fileName);
        intentShare.putExtra(Intent.EXTRA_TEXT, "For further information  \nClick this link to visit website \nhttps://bit.ly/cubixos");
        intentShare.putExtra(Intent.EXTRA_STREAM, uriFilePathShare);
        intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intentShare, "Share via"));
    }
    //Video Open
    private void openVideo() {
        Uri uriFilePathOpen = FileProvider.getUriForFile(VideoViewActivity.this, "com.cubixos.pocket" + ".provider", file);
        //Intent openVideoIntent = new Intent(Intent.ACTION_VIEW, uriFilePathOpen);//OK
        Intent openVideoIntent = new Intent(Intent.ACTION_VIEW);
        openVideoIntent.setDataAndType(uriFilePathOpen,"video/*");
        openVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intentChooser = Intent.createChooser(openVideoIntent, "Open this video with");
        if (openVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(intentChooser);
        }
    }
    //Notification - File error
    private void notificationFileError(){
        String id = "id_file_error" ;
        String title = "File Error";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.setDescription("Notification appear when any action taken by user corresponding to file.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(intent);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(false)
                .setSound(soundUri)
                .setContentIntent(resultPendingIntent)
                //.setNumber(count)//declare message count here  as a - int
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText("Deleted successfully from storage.\nNow download it again, it requires only "+fileSize+" MBs.");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(6001, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_ACCESS_EXTERNAL_STORAGE_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted.
                Toast.makeText(getApplicationContext(), "Permission was granted ", Toast.LENGTH_LONG).show();
            } else {
                // permission denied.
                Toast.makeText(getApplicationContext(), "Please, Grant Permission to allow to pick video from memory", Toast.LENGTH_LONG).show();
            }
            return;
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}