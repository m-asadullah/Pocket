package com.cubixos.pocket.oldpack;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cubixos.pocket.accessories.misc.CheckInternet;
import com.cubixos.pocket.MainActivity;
import com.cubixos.pocket.R;
import com.cubixos.pocket.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AccountActivity extends AppCompatActivity {

    final int NOTIFY_ID = 1 ; // any integer number
    int count = 1;
    private static final String TAG = "AccountActivity";
    //
    Window window;
    View container;
    //
    Context context;
    GoogleApiClient googleApiClient;
    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    DocumentReference documentReference;
    String currentUserId;
    String stringUserPhoneNumber;
    //
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    //
    ImageView imageViewUser;
    ImageView imageViewVerificationBadge;
    TextView textViewNameA;
    TextView textViewUserNameIDA;
    TextView textViewStatusA;
    //
    TextView textViewName;
    TextView textViewUserNameID;
    TextView textViewStatus;
    TextView textViewEmail;
    TextView textViewPhone;
    TextView textViewVerification;
    TextView textViewProfile;
    TextView textViewLogout;
    TextView textViewReport;
    TextView textViewDelete;
    RelativeLayout relativeLayoutPhoneNo;
    //
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private StorageReference storageReference;
    //
    TextView textViewPhoneNotVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
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
        if(firebaseUser!=null){//if signed in
            Toast.makeText(getApplicationContext(), "User can modify account details",Toast.LENGTH_SHORT).show();
        }else {//if not signed in
            showDialogJoin();
        }
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, onConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //
        window = this.getWindow();
        container = findViewById(R.id.container_account);
        //
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        appBarLayout = findViewById(R.id.appbar_layout_account);
        toolbar = findViewById(R.id.toolbar_account);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //
        imageViewUser = findViewById(R.id.image_view_picture_activity_account);
        imageViewVerificationBadge = findViewById(R.id.image_view_verified_badge);
        imageViewVerificationBadge.setVisibility(View.GONE);
        textViewNameA = findViewById(R.id.text_view_username_activity_account);
        textViewUserNameIDA = findViewById(R.id.text_view_username_id_activity_account);
        textViewStatusA = findViewById(R.id.text_view_status_activity_account);
        //
        textViewName = findViewById(R.id.text_view_name_account);
        textViewStatus = findViewById(R.id.text_view_status_account);
        textViewUserNameID = findViewById(R.id.text_view_username_account);
        textViewEmail = findViewById(R.id.text_view_email_account);
        textViewPhone = findViewById(R.id.text_view_phone_account);
        textViewPhone.setText(stringUserPhoneNumber);
        relativeLayoutPhoneNo = findViewById(R.id.relative_layout_phone_verify);
        textViewPhoneNotVisible = findViewById(R.id.text_view_phone_number_not_visible);
        textViewVerification = findViewById(R.id.text_view_verification_account);

        textViewProfile = findViewById(R.id.text_view_profile_account);
        textViewLogout = findViewById(R.id.text_view_logout_account);
        textViewReport = findViewById(R.id.text_view_report_account);
        textViewDelete = findViewById(R.id.text_view_delete_account);
        //
        userAccountDetails();
        checkVerifiedEmail();
        //
        imageViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(CheckInternet.isNetwork(getApplicationContext())){
                   //Choose image
                   //chooseImage();
                   //Dialog for change picture
                   showAlertDialogChangeProfile();
               }else {
                   Toast.makeText(getApplicationContext(), "Profile picture change only when you connected to internet.", Toast.LENGTH_SHORT).show();
               }
            }
        });
        //
        textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet.isNetwork(getApplicationContext())){
                    showDialogEditAccount("Name",   "Your currently national name");   
                }
            }
        });
        textViewUserNameID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Not allowed to edit @usernameid
            }
        });
        textViewStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet.isNetwork(getApplicationContext())){
                    showDialogEditAccount("Status",   "");
                }
            }
        });
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet.isNetwork(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "Email is not changed right now. Associated once per account", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet.isNetwork(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "Phone number is not changed right now. Associated once per account", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textViewPhoneNotVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAuthPhone = new Intent(AccountActivity.this, AuthPhoneActivity.class);
                startActivity(intentAuthPhone);
            }
        });
        textViewVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet.isNetwork(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "You are good to go", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogProfile();
            }
        });
        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "User should be logged in to gain access to app.", Toast.LENGTH_SHORT).show();
                if (CheckInternet.isNetwork(getApplicationContext())){
                    showDialogLogOut();
                }
            }
        });
        textViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckInternet.isNetwork(getApplicationContext())){
                    Toast.makeText(getApplicationContext(), "You are good to go", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "You are good to go", Toast.LENGTH_SHORT).show();
                if (CheckInternet.isNetwork(getApplicationContext())){
                    showDialogDelete();
                }
            }
        });
        //
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //Toast.makeText(getApplicationContext(), "Signed IN",Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(getApplicationContext(), "No Account associated with this app. Currently you are not signed in.",Toast.LENGTH_SHORT).show();
                }
                //updateUI(user);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
    //Account - Edit
    private void showDialogEditAccount(String dialogTitle, String dialogDescription) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        //int iconDialog = dialogView.findViewById(R.id.image_button_icon_dialog);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_edit_account, null);
        EditText editText = dialogView.findViewById(R.id.edit_text_dialog);
        editText.setHint(dialogTitle);
        //editText.setText(dialogTitle);
        ImageButton imageButtonBell = dialogView.findViewById(R.id.image_button_notifier_dialog);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake_bell);
        imageButtonBell.setAnimation(startRotateFrontAnimation);
        ImageButton imageButtonClose = dialogView.findViewById(R.id.image_button_close_dialog);
        ImageView imageViewIconDialog = dialogView.findViewById(R.id.image_view_icon_dialog);
        //imageViewIconDialog.setImageResource(iconDialog);
        TextView textViewTitleDialog = dialogView.findViewById(R.id.text_view_title_dialog);
        textViewTitleDialog.setText(dialogTitle);
        TextView textViewDescriptionDialog = dialogView.findViewById(R.id.text_view_description_dialog);
        textViewDescriptionDialog.setText(dialogDescription);
        //
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    User user = snapshot.toObject(User.class);
                    if(user.getUserName()!= null){
                        //
                    }else{
                        //
                    }
                }
            }
        });
        //
        if(dialogTitle.equals("Name")){
            imageViewIconDialog.setImageResource(R.drawable.ic_twotone_account_circle_24);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        User user = snapshot.toObject(User.class);
                        if(user.getUserName()!= null){
                            editText.setText(user.getUserName());
                        }else{
                            editText.setText("");
                        }
                    }
                }
            });
            dialogView.findViewById(R.id.button_positive_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String edittext = editText.getText().toString();
                    if (TextUtils.isEmpty(edittext)) {
                        Toast.makeText(getApplicationContext(), "Please enter " + dialogTitle + " to edit.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    documentReference.update("userName", edittext).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            notificationAccountEdit("Name","You have successfully change your name.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Check your internet."+ edittext +" is not updated right now.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else if (dialogTitle.equals("Status")){
            imageViewIconDialog.setImageResource(R.drawable.ic_baseline_insert_emoticon_24);
            dialogView.findViewById(R.id.button_positive_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String edittext = editText.getText().toString();
                    if (TextUtils.isEmpty(edittext)) {
                        Toast.makeText(getApplicationContext(), "Please enter " + dialogTitle + " to edit.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    documentReference.update("status", edittext).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            notificationAccountEdit("Status","You have successfully change your status.");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Check your internet."+ edittext +" is not updated right now.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        //
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        dialogView.findViewById(R.id.button_negative_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
    //
    private void userAccountDetails() {
        //DocumentReference currentUserRef = firebaseFirestore.collection("User").document(currentUserId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    User user = snapshot.toObject(User.class);
                    if (user.getProfilePicUrl() != null) {
                        //Picasso.get().load(user.getProfilePicUrl()).into(imageViewUser);
                        if (CheckInternet.isNetwork(getApplicationContext())){
                            Picasso.get()
                                    .load(user.getProfilePicUrl())
                                    .placeholder(R.drawable.empty_profile_pic)
                                    .error(R.drawable.ic_warning_sign_board)
                                    .into(imageViewUser);
                        }else {
                            Picasso.get()
                                    .load(user.getProfilePicUrl())
                                    .placeholder(R.drawable.empty_profile_pic)
                                    .error(R.drawable.ic_warning_sign_board)
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .into(imageViewUser);
                        }
                    }
                    if(user.getUserName() != null){
                        textViewName.setText(user.getUserName());
                        textViewNameA.setText(user.getUserName());
                    }
                    if(user.getStatus()!= null){
                        textViewStatus.setText(user.getStatus());
                        textViewStatusA.setText(user.getStatus());
                    }
                    if(user.getUserNameId()!= null){
                        textViewUserNameID.setText(user.getUserNameId());
                        textViewUserNameIDA.setText(user.getUserNameId());
                    }
                    if (user.getEmailAddress()!= null){
                        textViewEmail.setText(user.getEmailAddress());
                    }else {
                        textViewEmail.setText(firebaseUser.getEmail());//discard next time
                    }
                }
            }
        });
    }
    //Profile Dialog
    private void showDialogProfile() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_profile);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        //
        ImageView imageViewProfileDialog = dialog.findViewById(R.id.image_view_dialog_profile);
        ImageView imageViewVerification = dialog.findViewById(R.id.image_view_verified_badge);
        imageViewVerification.setVisibility(View.GONE);
        TextView textViewUsernameDialog = dialog .findViewById(R.id.text_view_username_dialog_profile);
        TextView textViewUsernameIdDialog = dialog .findViewById(R.id.text_view_usernameid_dialog_profile);
        TextView textViewEmailDialog = dialog .findViewById(R.id.text_view_email_dialog_profile);
        TextView textViewPhoneDialog = dialog .findViewById(R.id.text_view_phone_dialog_profile);
        TextView textViewStatusDialog = dialog .findViewById(R.id.text_view_status_dialog_profile);
        ImageButton imageButtonClose = dialog.findViewById(R.id.image_button_close_dialog);
        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.image_view_security).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Screen shot
                takeScreenshot(dialog);
                notificationAccountProfile();
            }
        });
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    User user = snapshot.toObject(User.class);
                    if (user.getProfilePicUrl() != null) {
                        //Picasso.get().load(user.getProfilePicUrl()).into(imageViewProfileDialog);
                        Picasso.get()
                                .load(user.getProfilePicUrl())
                                .placeholder(R.drawable.empty_profile_pic)
                                .error(R.drawable.ic_warning_sign_board)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .into(imageViewProfileDialog);
                    }
                    if(user.getUserName()!= null){
                        textViewUsernameDialog.setText(user.getUserName());
                    }else{
                        textViewUsernameDialog.setVisibility(View.GONE);
                    }
                    if(user.getUserNameId()!= null){
                        textViewUsernameIdDialog.setText(user.getUserNameId());
                    }else{
                        textViewUsernameIdDialog.setVisibility(View.GONE);
                    }
                    if(user.getStatus()!= null){
                        textViewStatusDialog.setText(user.getStatus());
                    }else{
                        textViewStatusDialog.setVisibility(View.GONE);
                    }
                    if (user.getEmailAddress()!= null){
                        textViewEmailDialog.setText(user.getEmailAddress());
                    }else{
                        textViewEmailDialog.setVisibility(View.GONE);
                    }
                    //
                    if (firebaseUser != null){
                        String firebaseuserno =  firebaseUser.getPhoneNumber();
                        if (firebaseuserno == null){
                            //userPhoneNumber() is equal to null
                            //textViewVerification.setText("Verified Email only");
                            imageViewVerification.setVisibility(View.GONE);
                            textViewPhoneDialog.setVisibility(View.GONE);
                        }else if(firebaseuserno.equals("")){
                            //userPhoneNumber() is equal to empty string
                            //textViewVerification.setText("Verified Email only");
                            imageViewVerification.setVisibility(View.GONE);
                            textViewPhoneDialog.setVisibility(View.GONE);
                        }else if(firebaseuserno.equals("null")){
                            //userPhoneNumber() is equal to empty string
                            //textViewVerification.setText("Verified Email only");
                            imageViewVerification.setVisibility(View.GONE);
                            textViewPhoneDialog.setVisibility(View.GONE);
                        } else {
                            // After all above these two conditions are for empty/null
                            // userPhoneNumber() is equal to not null &
                            // but this below is given here should be phonenumber 03331234567
                            imageViewVerification.setVisibility(View.VISIBLE);
                            textViewPhoneDialog.setVisibility(View.VISIBLE);
                            textViewPhoneDialog.setText(firebaseuserno);
                        }
                    }else {
                        imageViewVerification.setVisibility(View.GONE);
                        textViewPhoneDialog.setVisibility(View.GONE);
                    }
                }
            }
        });
        dialog.show();
    }
    //TimeStamp
    long timeStamp = System.currentTimeMillis();
    String messageId = Long.toString(timeStamp);
    //Calender - Date Time
    Calendar calendar = Calendar.getInstance();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssa");
    String datetime = sdf.format(calendar.getTime());
    //
    //Screenshot of dialog
    private void takeScreenshot(Dialog dialog) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Math/"+ firebaseUser.getDisplayName()+"_"+datetime+".jpg"; //"/data/data/com.rohit.test/test.jpg"; // use your desired path
            // create bitmap screen capture
            View v1 = dialog.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(filePath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }
    //Join
    private void showDialogJoin() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_join);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_bell);
        mediaPlayer.start();
        //
        dialog.findViewById(R.id.image_button_notifier_dialog).startAnimation(startRotateFrontAnimation);
        dialog.findViewById(R.id.image_button_close_dialog).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });
        dialog.findViewById(R.id.button_join_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }
    // Email - Checking email is verified or Not
    private void checkVerifiedEmail() {
        firebaseUser = firebaseAuth.getCurrentUser();
        boolean isVerified = false;
        if (firebaseUser != null) {
            isVerified = firebaseUser.isEmailVerified();
        }
        if (isVerified){
            currentUserId = firebaseAuth.getCurrentUser().getUid();
            //textViewVerification.setText("Verified Email");
            //
            if (firebaseUser != null){
                String firebaseuserno =  firebaseUser.getPhoneNumber();
                if (firebaseuserno == null){
                    //userPhoneNumber() is equal to null
                    //textViewVerification.setText("Verified Email only");
                    imageViewVerificationBadge.setVisibility(View.GONE);
                    relativeLayoutPhoneNo.setVisibility(View.VISIBLE);
                    textViewPhone.setVisibility(View.GONE);
                }else if(firebaseuserno.equals("")){
                    //userPhoneNumber() is equal to empty string
                    //textViewVerification.setText("Verified Email only");
                    imageViewVerificationBadge.setVisibility(View.GONE);
                    relativeLayoutPhoneNo.setVisibility(View.VISIBLE);
                    textViewPhone.setVisibility(View.GONE);
                } else {
                    // After all above these two conditions are for empty/null
                    // userPhoneNumber() is equal to not null &
                    // but this below is given here should be phonenumber 03331234567
                    textViewVerification.setText("Verified Email and Phone Number");
                    imageViewVerificationBadge.setVisibility(View.VISIBLE);
                    relativeLayoutPhoneNo.setVisibility(View.GONE);
                    textViewPhone.setVisibility(View.VISIBLE);
                }
            }else {
                textViewVerification.setText("Verified Email");
            }
            //
        } else {
            Toast.makeText(getApplicationContext(), "Error: - Sign In Error, Verify your email. \nPlease ensure that your email address and password are correct.", Toast.LENGTH_LONG).show();
            textViewVerification.setText("Verify your email address otherwise you can't gain complete access to app.");
            //firebaseAuth.signOut();
        }
    }
    //
    public void showAlertDialogChangeProfile(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        builder.setTitle("Profile Picture")
                .setIcon(R.drawable.ic_twotone_account_circle_24)
                .setMessage(Html.fromHtml("Are you sure, You want to change your profile photo?"))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //send
                        chooseImage();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "When you want to change feel free to change it.",Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog dialog  = builder.create();
        dialog.show();
    }
    //Dialog - Log Out
    private void showDialogLogOut() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_logout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_bell);
        mediaPlayer.start();
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
                    //Delete Section
                    documentReference.update("designation", "Logout");
                    try{
                        new Timer().schedule(new TimerTask(){
                            public void run() {
                                AccountActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        userLogOut();
                                        Toast.makeText(getApplicationContext(), "The account is Logged out.", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        imageViewSuccessTick.setVisibility(View.VISIBLE);
                                        try{
                                            new Timer().schedule(new TimerTask(){
                                                public void run() {
                                                    AccountActivity.this.runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            notificationAccountLogout();
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
    //Dialog - Log Out
    private void showDialogDelete() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_delete);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_bell);
        mediaPlayer.start();
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
                if(CheckInternet.isNetwork(getApplicationContext())){
                    linearLayoutButtons.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    imageViewSuccessTick.setVisibility(View.GONE);
                    //Delete Section
                    documentReference.update("designation", "Delete");
                    try{
                        new Timer().schedule(new TimerTask(){
                            public void run() {
                                AccountActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        userLogOut();
                                        Toast.makeText(getApplicationContext(), "The account will be delete with in 14 professional working days.", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        imageViewSuccessTick.setVisibility(View.VISIBLE);
                                        try{
                                            new Timer().schedule(new TimerTask(){
                                                public void run() {
                                                    AccountActivity.this.runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            notificationAccountDelete();
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
    //Log Out
    private void userLogOut() {
        // Firebase logout
        firebaseAuth.signOut();
        //Google Sign out - only
        //Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                        deleteCache(getApplicationContext());
                        //
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        //
                    }
                }
        );
    }
    //Delete Cache
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            Toast.makeText(context,"Successfully deleted cache of this app from your phone", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context,"Error in  deleting cache. "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    //File Choose
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {
            filePath = data.getData();
            uploadImage();
        }
    }
    //Upload Profile Image
    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            storageReference = firebaseStorage.getReference("Users/").child(currentUserId);
            final StorageReference ref = storageReference.child("Profile/Images/" +datetime+ "AC");
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    notificationAccountImage();
                                    //Picasso.get().load(uri).into(imageViewUser);
                                    Picasso.get()
                                            .load(uri)
                                            .placeholder(R.drawable.empty_profile_pic)
                                            .error(R.drawable.ic_warning_sign_board)
                                            .networkPolicy(NetworkPolicy.OFFLINE)
                                            .into(imageViewUser);
                                    documentReference.update("profilePicUrl", uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                            .getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }
    //Notification - Account Image
    private void notificationAccountImage(){
        String id = "id_account_image" ;
        String title = "Profile Picture";
        String description = "You have successfully change profile picture." ;
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
        Intent intent = new Intent(this, MainActivity.class);
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
        managerCompat.notify(3000, builder.build());
    }
    //Notification - Account Edit
    private void notificationAccountEdit(String titleA, String descriptionA){
        String id = "id_account_edit" ;
        String title = "User " + titleA ;
        String description = descriptionA ;
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        managerCompat.notify(3001, builder.build());
    }
    //Notification - Account Profile
    private void notificationAccountProfile(){
        String id = "id_account_profile" ;
        String title = "Profile" ;
        String description = "Snap save to your storage. Open Math folder..." ;
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
        Intent intent = new Intent(this, MainActivity.class);
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
        managerCompat.notify(3002, builder.build());
    }
    //Notification - Account Logout
    private void notificationAccountLogout(){
        String id = "id_account_logout" ;
        String title = "Logout" ;
        String description = "You have successfully LogOut. " ;
        String channelID = "Account" ;
        String channelDescription = "Notification appear when any action taken to logout corresponding account." ;
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
        Intent intent = new Intent(this, MainActivity.class);
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
        managerCompat.notify(3003, builder.build());
    }
    //Notification - Account Delete
    private void notificationAccountDelete(){
        String id = "id_account_delete" ;
        String title = "Delete" ;
        String description = "You have successfully apply for deletion of account." ;
        String channelID = "Account" ;
        String channelDescription = "Notification appear when any action taken by user to delete corresponding account." ;
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
        Intent intent = new Intent(this, MainActivity.class);
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
        managerCompat.notify(3004, builder.build());
    }
}