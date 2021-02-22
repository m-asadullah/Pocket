package com.cubixos.pocket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cubixos.pocket.accessories.dialogs.DialogJoin;
import com.cubixos.pocket.accessories.misc.AppPermissionManager;
import com.cubixos.pocket.accessories.misc.CheckInternet;
import com.cubixos.pocket.accessories.misc.WebViewStart;
import com.cubixos.pocket.adapters.BotanyAdapter;
import com.cubixos.pocket.models.Botany;
import com.cubixos.pocket.models.User;
import com.cubixos.pocket.oldpack.AuthActivity;
import com.cubixos.pocket.settings.SettingsActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class HistoryActivity extends AppCompatActivity {

    Activity activity;
    Context context;

    private BotanyAdapter adapter;
    //
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    FirebaseStorage firebaseStorage;
    DocumentReference documentReference;
    String currentUserId;
    FloatingActionButton fab;
    //
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    TextView textViewToolbarTitle;
    SwipeRefreshLayout swipeRefreshLayout;
    URL webURLIntent;
    ImageView imageViewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        activity = HistoryActivity.this;
        context = getApplicationContext();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseAuth != null){
            if (firebaseUser != null) {
                //if signed in then do nothing here
                checkVerifiedEmail();
                //if authenticated then load data
                documentReference = firebaseFirestore.collection("User").document(currentUserId);
                documentReference.addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        User user = snapshot.toObject(User.class);
                        if (user.getProfilePicUrl() != null) {
                            //Picasso.get().load(user.getProfilePicUrl()).into(imageViewProfileDialog);
                            if (CheckInternet.isNetwork(context)){
                                Picasso.get()
                                        .load(user.getProfilePicUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .into(imageViewUser);
                            }else{
                                Picasso.get()
                                        .load(user.getProfilePicUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(imageViewUser);
                            }
                        }
                    }
                });
            }else{
                DialogJoin.showDialogJoin(activity, context);
            }
        }else {
            DialogJoin.showDialogJoin(activity, context);
        }
        //
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();//android.action.view.VIEW
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            AppPermissionManager.permissionStorage(activity, context);
            if ("text/plain".equals(type)) {
                String httpTextIntent = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (httpTextIntent != null) {
                    try {
                        webURLIntent = new URL(httpTextIntent);
                        Toast.makeText(context, httpTextIntent,Toast.LENGTH_SHORT).show();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error:\n"+e.getMessage() ,Toast.LENGTH_SHORT).show();
                    }
                    WebViewStart.startWebView(activity, context, webURLIntent.toString().trim());
                }
            } else {
                Toast.makeText(activity, "URL or Link not supported", Toast.LENGTH_SHORT).show();
            }
        }
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar = findViewById(R.id.toolbar_main);
        }
        appBarLayout = findViewById(R.id.appbar_layout_main);
        textViewToolbarTitle = findViewById(R.id.text_view_toolbar_title_main);
        setSupportActionBar(toolbar);
        imageViewUser = findViewById(R.id.image_view_user_photo);
        imageViewUser.setOnClickListener(view -> {
            Intent intent1 = new Intent(context, SettingsActivity.class);
            startActivity(intent1);
        });
        imageViewUser.setOnLongClickListener(view -> {
            //show profile dialog
            return false;
        });
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_main);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,R.color.colorBlack,R.color.colorGreyDark);
            adapter.notifyDataSetChanged();
        });
        fab = findViewById(R.id.fab_math_main);
        fab.setOnClickListener(view -> {
            Intent intentUpload= new Intent(activity, UploadActivity.class);
            startActivity(intentUpload);
        });
        setUpRecyclerView();
        //Permission for storage
        AppPermissionManager.permissionStorage(activity, context);
    }

    private void setUpRecyclerView() {
        collectionReference = firebaseFirestore.collection("MScBotany");
        Query query = collectionReference.orderBy("timeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Botany> options = new FirestoreRecyclerOptions.Builder<Botany>()
                .setQuery(query, Botany.class)
                .build();
        adapter = new BotanyAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    //Email verified check
    private void checkVerifiedEmail() {
        firebaseUser = firebaseAuth.getCurrentUser();
        boolean isVerified = false;
        if (firebaseUser != null) {
            isVerified = firebaseUser.isEmailVerified();
        }
        if (isVerified){
            currentUserId = firebaseAuth.getCurrentUser().getUid();
            //If verified email
            Toast.makeText(context, "Pull down to refresh",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Verify your email. \nPlease ensure that your email address and password are correct.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //finish();
            firebaseAuth.signOut();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}