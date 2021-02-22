package com.cubixos.pocket.oldpack;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cubixos.pocket.R;
import com.cubixos.pocket.models.CountryData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AuthPhoneActivity extends AppCompatActivity {

    Spinner spinner;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    ProgressBar progressBar;
    //
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    DocumentReference documentReference;
    StorageReference storageReference;
    //
    String currentUserId;
    String stringUserPhoneNumber;
    String stringPhoneNumber;
    String verificationId;
    //
    LinearLayout linearLayoutSendCode;
    EditText editTextPhone;
    Button buttonSendCode;
    //
    LinearLayout linearLayoutVerifying;
    EditText editTextOTP;
    Button buttonVerify;
    //
    Button buttonAgain;
    //
    TextView textViewTimeLapse;
    TextView textViewPhoneNumber;
    ImageView imageViewTick;
    //
    private static final long START_TIME_IN_MILLIS = 600000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_phone);
        appBarLayout = findViewById(R.id.appbar_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
            stringUserPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();
        }
        //
        storageReference = firebaseStorage.getReference("Users/").child(currentUserId);
        //
        documentReference = firebaseFirestore.collection("User").document(currentUserId);
        //
        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));
        //Fill - Send Code OTP
        linearLayoutSendCode = findViewById(R.id.linear_layout_body_verify);
        linearLayoutSendCode.setVisibility(View.VISIBLE);
        editTextPhone = findViewById(R.id.edit_text_phone_number);
        buttonSendCode = findViewById(R.id.button_send_code);
        buttonSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
                String number = editTextPhone.getText().toString().trim();
                if (number.isEmpty() || number.length() < 10) {
                    editTextPhone.setError("Valid number is required");
                    editTextPhone.requestFocus();
                    return;
                }
                String phoneNumber = "+" + code + number;
                stringPhoneNumber = "+" + code + number;
                showAlertDialogSendCode(stringPhoneNumber);
            }
        });
        //Auto Parse - Verification Process
        linearLayoutVerifying = findViewById(R.id.linear_layout_body_parse_code);
        linearLayoutVerifying.setVisibility(View.GONE);
        textViewPhoneNumber = findViewById(R.id.text_view_phone_number);
        textViewPhoneNumber.setText(stringPhoneNumber);
        textViewTimeLapse = findViewById(R.id.text_view_time_lapse);
        editTextOTP = findViewById(R.id.edit_text_sms_code);
        progressBar = findViewById(R.id.progress_bar);
        imageViewTick = findViewById(R.id.image_view_tick);
        imageViewTick.setVisibility(View.GONE);
        buttonVerify = findViewById(R.id.button_verify);
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                //start verification
            }
        });
        buttonAgain = findViewById(R.id.button_again);
        buttonAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogNotFound();
            }
        });
    }

    public void showAlertDialogSendCode(String phonenumber){
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthPhoneActivity.this);
        builder.setTitle("Check phone number")
                .setIcon(R.drawable.ic_twotone_phone_iphone_24)
                .setMessage(Html.fromHtml("Are you sure, "+"<b>"+phonenumber+"</b>"+" Your phone number is correct and active?"))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //send Code
                        sendVerificationCode(phonenumber);
                        startTimer();
                        linearLayoutSendCode.setVisibility(View.GONE);
                        linearLayoutVerifying.setVisibility(View.VISIBLE);
                        textViewPhoneNumber.setText(stringPhoneNumber);
                        buttonVerify.setVisibility(View.VISIBLE);
                        buttonVerify.setEnabled(true);
                    }
                })
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Edit your phone number",Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    public void showAlertDialogNotFound(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthPhoneActivity.this);
        builder.setTitle("Error")
                .setMessage("Check your phone number and internet connection. Try again with in few minutes.")
                .setCancelable(false)
                .setPositiveButton("Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Again Fill - Send OTP
                        linearLayoutSendCode.setVisibility(View.VISIBLE);
                        linearLayoutVerifying.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
        //Creating dialog box
        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    //Code Received Verification Starts to server
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        //signInWithCredential(credential);
        linkWithPhoneNumber(credential);
    }
    //Phone Number Linked with email
    private void linkWithPhoneNumber(PhoneAuthCredential credential) {
        firebaseAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            notificationPhoneNumber();
                            //Toast.makeText(getApplicationContext(), "Success User:" + user + "\nVreif" + verificationId, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Successfully verified your number", Toast.LENGTH_SHORT).show();
                            //save Phone number to server firebase
                            documentReference.update("phoneNumber", firebaseUser.getPhoneNumber());
                            //After verification - Intent to Account
                            try {
                                new Timer().schedule(new TimerTask(){
                                    public void run() {
                                        AuthPhoneActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Intent intent = new Intent(AuthPhoneActivity.this, AccountActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }
                                }, 2000);
                            }catch (Exception e){
                                FirebaseCrashlytics.getInstance().recordException(e);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed:\n" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);//temp
                            Intent intent = new Intent(AuthPhoneActivity.this, AccountActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }**/
   //

    //Phone Number send to server
    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                (Activity) TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }
    //Server Send code to phone number and verificationId to app.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }
        //Code Received - Verification Code refer to verifyCode then refer to linkWithPhoneNumber
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editTextOTP.setText(code);
                progressBar.setVisibility(View.GONE);//after verified
                buttonAgain.setVisibility(View.GONE);
                buttonVerify.setEnabled(false);
                imageViewTick.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }
        //Code Verification Failed
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(AuthPhoneActivity.this, "Verification failed:\n"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    //Timer - Countdown
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                //onfinish
                notificationTimeLapse();
                progressBar.setVisibility(View.GONE);
                buttonVerify.setVisibility(View.VISIBLE);
                buttonVerify.setEnabled(true);
            }
        }.start();
        mTimerRunning = true;
        //onstart
    }
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        //onpause
    }
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        //onreset
    }
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textViewTimeLapse.setText(timeLeftFormatted);
    }

    //Notification - Account Image
    private void notificationPhoneNumber(){
        String id = "id_phone_number" ;
        String title = "Phone Number";
        String description = "You have successfully verified your number." ;
        String channelID = "Account" ;
        String channelDescription = "Notification appear when any action taken when information provided by user corresponding to account but error occurred." ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.canShowBadge();
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, AccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id)
                .setContentTitle(title)//Title
                .setSmallIcon(R.mipmap.ic_launcher_round)//Small
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(false)//OnClick Cancel - false
                .setSound(soundUri)//Tone
                .setContentIntent(pendingIntent)//
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(description);//Description
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(7001, builder.build());
    }

    //Notification - Account Image
    private void notificationTimeLapse() {
        String id = "id_auth_phone_time";
        String title = "Time Running out";
        String description = "Verification code is not detected right now.\nDont mess with it, put your active phone number.";
        String channelID = "Account";
        String channelDescription = "Notification appear when any action taken when information provided by user corresponding to account but error occurred.";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelDescription);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.canShowBadge();
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, AuthPhoneActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                .setContentTitle(title)//Title
                .setSmallIcon(R.mipmap.ic_launcher_round)//Small
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(false)//OnClick Cancel - false
                .setSound(soundUri)//Tone
                .setContentIntent(pendingIntent)//
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(description);//Description
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(7002, builder.build());
    }
}