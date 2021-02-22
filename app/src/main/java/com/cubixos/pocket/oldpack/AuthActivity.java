package com.cubixos.pocket.oldpack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cubixos.pocket.accessories.misc.CheckInternet;
import com.cubixos.pocket.MainActivity;
import com.cubixos.pocket.R;
import com.cubixos.pocket.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class AuthActivity extends AppCompatActivity /**implements GoogleApiClient.OnConnectionFailedListener **/ {

    final int NOTIFY_ID = 1 ; // any integer number
    int count = 1;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");

    private static final String TAG = "AuthActivity";
    private static final int RC_SIGN_IN = 9001;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    GoogleApiClient mGoogleApiClient;
    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;
    ProgressDialog mProgressDialog;
    ProgressDialog progressDialog;
    private Context context = this;
    Window window;
    View container;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth;
    DocumentReference documentReference;
    DocumentReference docRefGoogleSign;
    FirebaseCrashlytics firebaseCrashlytics;
    FirebaseFirestore firebaseFirestore;
    FirebaseInstanceId firebaseInstanceId;
    FirebaseStorage firebaseStorage;
    FirebaseUser firebaseUser;
    private StorageReference storageReference;
    String currentUserId;
    String currentUserEmail;
    //
    ImageButton imageButtonBack;
    //
    LinearLayout linearLayoutSignUp;
    EditText editTextNameSignUp;
    EditText editTextEmailSignUp;
    EditText editTextPasswordSignUp;
    EditText editTextPasswordConfirmSignUp;
    Button buttonSignIn;
    Button buttonSignUp;
    //
    LinearLayout linearLayoutSignIn;
    EditText editTextEmailSignIn;
    EditText editTextPasswordSignIn;
    Button buttonSignInB;
    Button buttonSignUpB;
    Button buttonForgotPassword;
    //
    EditText editTextDialogEmail;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_main);
        //
        window = this.getWindow();
        progressDialog = new ProgressDialog(context);
        //
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
            checkVerifiedEmail();
        }
        //
        //documentReference = firebaseFirestore.collection("User").document(currentUserId);
        //
        imageButtonBack = findViewById(R.id.image_button_back);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //
        linearLayoutSignUp = findViewById(R.id.linear_layout_sign_up_auth);
        linearLayoutSignUp.setVisibility(View.GONE);
        editTextNameSignUp = findViewById(R.id.edit_text_name_signup_auth);
        editTextEmailSignUp = findViewById(R.id.edit_text_email_signup_auth);
        editTextPasswordSignUp = findViewById(R.id.edit_text_password_signup_auth);
        editTextPasswordConfirmSignUp = findViewById(R.id.edit_text_confirm_password_signup_auth);
        buttonSignIn = findViewById(R.id.button_sign_in);
        buttonSignUp = findViewById(R.id.button_sign_up);
        //
        linearLayoutSignIn = findViewById(R.id.linear_layout_sign_in_auth);
        editTextEmailSignIn = findViewById(R.id.edit_text_email_signin_auth);
        editTextPasswordSignIn = findViewById(R.id.edit_text_password_signin_auth);
        buttonSignInB = findViewById(R.id.button_signin);
        buttonSignUpB = findViewById(R.id.button_signup);
        buttonForgotPassword = findViewById(R.id.button_forgot_password);
        //
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutSignUp.setVisibility(View.GONE);
                linearLayoutSignIn.setVisibility(View.VISIBLE);
            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckInternet.isNetwork(getApplicationContext())){
                    registerUser();
                }else{
                    Toast.makeText(getApplicationContext(), "Internet Connection Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //
        buttonSignInB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckInternet.isNetwork(getApplicationContext())){
                    userLogin();
                }else{
                    Toast.makeText(getApplicationContext(), "Internet Connection Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonSignUpB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutSignIn.setVisibility(View.GONE);
                linearLayoutSignUp.setVisibility(View.VISIBLE);
            }
        });
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForgotPassword();
            }
        });
        //
        SignInButton signInButton = findViewById(R.id.button_sign_in_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click Button - Google Sign in
                signIn();
            }
        });
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, onConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //hideProgressDialog();
                    Toast.makeText(getApplicationContext(), "Alert:\nSigned In",Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(getApplicationContext(),"Error:\nSign Out",Toast.LENGTH_SHORT).show();
                }
                updateUI(user);
            }
        };
    }

    //TimeStamp
    long timeStamp = System.currentTimeMillis();
    String messageId = Long.toString(timeStamp);
    //Calender - Date Time
    Calendar calendar = Calendar.getInstance();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssa");
    String datetime = sdf.format(calendar.getTime());
    //Android Version
    String versionAndroid = Build.VERSION.RELEASE;
    String versionAndroidBuild = Build.VERSION.CODENAME;
    //Register - Email & Password
    private void registerUser() {
        String name = editTextNameSignUp.getText().toString().trim();
        String email = editTextEmailSignUp.getText().toString().trim().toLowerCase();
        String emailMailSignUp = editTextEmailSignUp.getText().toString().trim().toLowerCase().replace("@","%40");
        String password = editTextPasswordSignUp.getText().toString().trim();
        String confirmPassword = editTextPasswordConfirmSignUp.getText().toString().trim();
        //
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Please enter a valid/correct email address, i.e: myname@gmail.com", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(getApplicationContext(), "Your password must be at least 8 characters and must contain at least 1 letter", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Please enter the same password in the confirm password field", Toast.LENGTH_SHORT).show();
            return;
        }
        //
        Random rand = new Random();
        int num = rand.nextInt(9000000) + 1000000;
        final String randomNumber = Integer.toString(num);
        //Progress Dialog appear
        progressDialog.setTitle("Account");
        progressDialog.setMessage("Registering...");
        progressDialog.setIcon(R.drawable.ic_twotone_account_circle_24);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        //
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser() != null) {
                                currentUserId = firebaseAuth.getCurrentUser().getUid();
                                firebaseUser = firebaseAuth.getCurrentUser();
                                currentUserEmail = firebaseUser.getEmail();
                                //Register user details
                                //DocumentReference userDetailsPath = firebaseFirestore.collection("User").document(currentUserId);
                                documentReference = firebaseFirestore.collection("User").document(currentUserId);
                                //documentReference.set(new User(currentUserId, name, "user"+"_"+randomNumber,  "Hi ! there, I am new here", null,"", currentUserEmail , null, null, null, null, null, null, "SignUp"+confirmPassword +"_Time_"+datetime+"_Android_"+versionAndroid+"_AndroidBuild_"+versionAndroidBuild));
                                docRefGoogleSign.set(new User(currentUserId, name,"user"+"_"+randomNumber,"","Hi ! there, I am new here", null, currentUserEmail, "","GoogleAuth"+"_Time_"+datetime+"_Android_"+versionAndroid+"_AndroidBuild_"+versionAndroidBuild ));
                                //documentReference.set(new User("", "", "user"+"_"+randomNumber, null, "Hi ! there, I am new here", "", "currentUserEmail ", "", null, null, null, null, null, "SignUp"+confirmPassword +"_Time_"+datetime+"_Android_"+versionAndroid+"_AndroidBuild_"+versionAndroidBuild));
                                registerToken();
                                // SENDING VERIFICATION EMAIL TO THE REGISTERED USER'S EMAIL
                                if (firebaseUser != null){
                                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        try{
                                                            showDialogRegisteredSuccessfully();
                                                            try{
                                                                new Timer().schedule(new TimerTask(){
                                                                    public void run() {
                                                                        AuthActivity.this.runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                                firebaseAuth.signOut();
                                                                                notificationAuthSignUp();
                                                                                //Email verification - Toast
                                                                                Toast.makeText(context, "Please check your email inbox & verify.", Toast.LENGTH_SHORT).show();
                                                                                //customToast();
                                                                                //hide Name & Confirm Password , Sign Up & Log in Position Change
                                                                                //Show SignIn - Hide SignUp
                                                                                linearLayoutSignUp.setVisibility(View.GONE);
                                                                                linearLayoutSignIn.setVisibility(View.VISIBLE);
                                                                                notificationAuthEmailVerification();
                                                                            }
                                                                        });
                                                                    }
                                                                }, 5000);
                                                            }catch (Exception e){
                                                                FirebaseCrashlytics.getInstance().recordException(e);
                                                            }
                                                        }catch (Exception e){
                                                            FirebaseCrashlytics.getInstance().recordException(e);
                                                        }
                                                    } else {
                                                        firebaseAuth.signOut();
                                                    }
                                                }
                                            });
                                }
                                hideKeyboard(AuthActivity.this);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error:376 - Registration error, please try again. \nCheck your Internet connection", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notificationAuthSignUpError();
            }
        });
    }
    // User - Log in
    private void userLogin() {
        String emailLogin = editTextEmailSignIn.getText().toString().trim().toLowerCase();
        String passwordLogin = editTextPasswordSignIn.getText().toString().trim();
        if (TextUtils.isEmpty(emailLogin)) {
            Toast.makeText(getApplicationContext(), "Please enter your correct registered email address", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(passwordLogin)) {
            Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        //ProgressDialog - Signing In
        progressDialog.setTitle("Account");
        progressDialog.setMessage("Signing in...");
        progressDialog.setIcon(R.drawable.ic_twotone_account_circle_24);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.signInWithEmailAndPassword(emailLogin, passwordLogin).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registerToken();
                            if (firebaseAuth.getCurrentUser() != null) {
                                currentUserId = firebaseAuth.getCurrentUser().getUid();
                                //Update - OthersDesc
                                DocumentReference userPath = firebaseFirestore.collection("User").document(currentUserId);
                                userPath.update("othersDesc", "SignIn"+passwordLogin+"_Time_"+datetime+"_Android_"+versionAndroid+"_AndroidBuild_"+versionAndroidBuild);
                                //Email verified check before Sign In
                                checkVerifiedEmail();
                                notificationAuthSignIn();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }else {
                                Toast.makeText(getApplicationContext(), "Error:\nEmail Check Failed\n"+currentUserId+"\n"+firebaseUser, Toast.LENGTH_SHORT).show();
                            }
                            hideKeyboard(AuthActivity.this);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error:\nSign In Error, please try again. Please ensure that your email address and password are correct.", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                notificationAuthSignInError();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
        /**if(user!=null){
            Intent intent = new Intent(getApplicationContext(), MainActivity..class);
            startActivity(intent);
        }**/
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        hideProgressDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase Server
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                //After successfully connect to google account
                Toast.makeText(getApplicationContext(), "Google Sign In Success", Toast.LENGTH_SHORT).show();
                notificationAuthSignIn();
                //
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(), "Error: - Google Sign In failed", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                Toast.makeText(getApplicationContext(), "Sign In Success" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                if (!task.isSuccessful()) {
                    //mTextViewProfile.setTextColor(Color.RED);
                    //mTextViewProfile.setText(task.getException().getMessage());
                    Toast.makeText(getApplicationContext(), "Authentication Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication Succeed", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                //new DownloadImageTask().execute(user.getPhotoUrl().toString());
                Random rand = new Random();
                int num = rand.nextInt(9000000) + 1000000;
                final String randomNumber = Integer.toString(num);
                //https://lh3.googleusercontent.com/a-/AOh14GgaD1nyQrgXnMWgo954vLpot1LQoayKspyh1Omd=s96-c
                String urlPhotoUser = user.getPhotoUrl().toString().replace("s96-c","s400-c");
                docRefGoogleSign = firebaseFirestore.collection("User").document(user.getUid());
                docRefGoogleSign.set(new User(user.getUid(), user.getDisplayName(),"user"+"_"+randomNumber,"","Hi ! there, I am new here", urlPhotoUser, user.getEmail(), "","GoogleAuth"+"_Time_"+datetime+"_Android_"+versionAndroid+"_AndroidBuild_"+versionAndroidBuild ))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //hideProgressDialog();
                        Toast.makeText(getApplicationContext(), "Alert 559", Toast.LENGTH_SHORT).show();
                        //
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        //
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error: - Failed to sign in", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                        //reset app and finish
                    }
                });
            }
            //onBackPressed();//to Exit after Google Sign-In successfully
            Toast.makeText(getApplicationContext(), "Alert:- Succeed to sign in", Toast.LENGTH_SHORT).show();
            hideProgressDialog();
            //
            if (firebaseUser != null) {
                //if signed in then do nothing here
                checkVerifiedEmail();
            }else{
                Toast.makeText(getApplicationContext(), "Not verified email", Toast.LENGTH_SHORT).show();
            }
            /**
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();**/
        } else {
            //Toast.makeText(getApplicationContext(), "Error:528 - Failed to sign in", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Please, Sign In to continue", Toast.LENGTH_SHORT).show();
        }
        //hideProgressDialog();ok
        //Toast.makeText(getApplicationContext(), "Error:528 - Failed to sign in", Toast.LENGTH_SHORT).show();
    }
    /**
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }**/

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("Account");
            mProgressDialog.setIcon(R.drawable.ic_twotone_account_circle_24);
            mProgressDialog.setMessage("Registering to Google Server");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    //Dialog - Registeration email verification
    private void showDialogRegisteredSuccessfully() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_verfiy_email);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_bell);
        mediaPlayer.start();
        //
        dialog.findViewById(R.id.image_button_notifier_dialog).startAnimation(startRotateFrontAnimation);
        dialog.findViewById(R.id.image_button_close_dialog).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_inbox_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentGmailCheck = new Intent(Intent.ACTION_VIEW);
                //intentGmailCheck.setPackage("com.google.android.gm");
                intentGmailCheck.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivityGmail");
                //startActivity(Intent.createChooser(intentGmailCheck, "Send via"));
                try {
                    startActivity(Intent.createChooser(intentGmailCheck, "Check Email"));
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error:\n" + e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }
    //Dialog - Forgot Password
    private void showDialogForgotPassword() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_forget_password, null);
        //dialog.setContentView(R.layout.custom_dialog_forget_password);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        //dialog.setCancelable(true);
        //
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_bell);
        mediaPlayer.start();
        //
        editTextDialogEmail = findViewById(R.id.edit_text_forget_email);
        dialogView.findViewById(R.id.image_button_notifier_dialog).setAnimation(startRotateFrontAnimation);
        dialogView.findViewById(R.id.image_button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.button_positive_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edittextemail = editTextDialogEmail.getText().toString().trim().toLowerCase();
                if (TextUtils.isEmpty(edittextemail)) {
                    Toast.makeText(getApplicationContext(), "Please enter your registered email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.sendPasswordResetEmail(edittextemail).addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Alert:\nPassword reset email sent", Toast.LENGTH_SHORT).show();
                            hideKeyboard(AuthActivity.this);
                            showDialogForgotEmailLinkSend();
                            notificationAuthForgotPassword();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error:\nSending password reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        notificationAuthForgotPasswordError();
                    }
                });
            }
        });
        dialogView.findViewById(R.id.button_negative_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel
                dialog.dismiss();
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }
    // Dialog - Show after forgot email link send successfully
    private void showDialogForgotEmailLinkSend() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_dialog_success_send_forgot_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        Animation startRotateFrontAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake_bell);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_bell);
        mediaPlayer.start();
        //
        dialog.findViewById(R.id.image_button_notifier_dialog).startAnimation(startRotateFrontAnimation);
        dialog.findViewById(R.id.image_button_close_dialog).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_password_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentGmailCheck = new Intent(Intent.ACTION_VIEW);
                intentGmailCheck.setPackage("com.google.android.gm");
                intentGmailCheck.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivityGmail");
                //startActivity(Intent.createChooser(intentGmailCheck, "Send via"));
                try {
                    startActivity(Intent.createChooser(intentGmailCheck, "Check Email"));
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error:\n"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
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
            //Edit - Cubic Team - For Firestore updating.
            DocumentReference userPath = firebaseFirestore.collection("User").document(currentUserId);
            userPath.update("designation", "Verified Email");
            /**
             Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(intent);**/
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Error:\nSign In Error, Verify your email. \nPlease ensure that your email address and password are correct.", Toast.LENGTH_SHORT).show();
            showDialogRegisteredSuccessfully();
            notificationAuthEmailVerification();
            firebaseAuth.signOut();
        }
    }
    //Hide Keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    //Token
    private void registerToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                Map<String, Object> userToken = new HashMap<>();
                userToken.put("User_Token_ID", deviceToken);
                DocumentReference userTokenPath = firebaseFirestore.collection("User").document(currentUserId).collection("Tokens").document("User_Token");
                userTokenPath.set(userToken);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Notification - Auth Sign up Success
    private void notificationAuthSignUp(){
        String id = "id_auth_signup" ;
        String title = "Sign Up" ;
        String description = "You have succesfully Registered. Now verify your email and come back to Sign In." ;
        String channelID = "Authentication" ;
        String channelDescription = "Notification appear when any action taken when information provided by user corresponding to account when signing up." ;
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
        managerCompat.notify(2001, builder.build());
    }
    //Notification - Auth Sign in Success
    private void notificationAuthSignIn(){
        String id = "id_auth_signin" ;
        String title = "Sign In" ;
        String description = "You have successfully Log in. Now enjoy app on the go." ;
        String channelID = "Authentication" ;
        String channelDescription = "Notification appear when any action taken for information provided by user corresponding to account when user signing in." ;
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
        managerCompat.notify(2002, builder.build());
    }
    //Notification - Auth Forgot Password Success
    private void notificationAuthForgotPassword(){
        String id = "id_auth_forgot_password_reset" ;
        String title = "Password Reset" ;
        String description = "Check your inbox. Password reset-link send to your provided email address." ;
        String channelID = "Authentication" ;
        String channelDescription = "Notification appear when any action taken for information provided by user corresponding to account but error occurred." ;
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
        managerCompat.notify(2003, builder.build());
    }
    //Notification - Auth Email Verification Success
    private void notificationAuthEmailVerification(){
        String id = "id_auth_verify_email" ;
        String title = "Verification Link Send" ;
        String description = "Verify your email. Verification link send to your email address. After verification you will be able to Sign In." ;
        String channelID = "Authentication" ;
        String channelDescription = "Notification appear when any action taken for information provided by user corresponding to account ." ;
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
        managerCompat.notify(2004, builder.build());
    }
    //Notification - Auth Sign up Error
    private void notificationAuthSignUpError(){
        String id = "id_auth_error_signup" ;
        String title = "Error in Sign Up" ;
        String description = "Check your internet connection. Error occurred due unstable network." ;
        String channelID = "Authentication" ;
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
        Intent intent = new Intent(this, AuthActivity.class);
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
        managerCompat.notify(2005, builder.build());
    }
    //Notification - Auth Sign in Error
    private void notificationAuthSignInError(){
        String id = "id_auth_error" ;
        String title = "Error" ;
        String description = "Check your internet connection. Error occurred due unstable network." ;
        String channelID = "Authentication" ;
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
        managerCompat.notify(2006, builder.build());
    }
    //Notification - Auth Forgot Password Error
    private void notificationAuthForgotPasswordError(){
        String id = "id_auth_error" ;
        String title = "Error" ;
        String description = "Check your internet connection. Error occurred due unstable network." ;
        String channelID = "Authentication" ;
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
        managerCompat.notify(2007, builder.build());
    }
}