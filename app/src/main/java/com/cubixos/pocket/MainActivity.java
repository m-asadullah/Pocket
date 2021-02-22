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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cubixos.pocket.accessories.dialogs.DialogJoin;
import com.cubixos.pocket.accessories.misc.AppPermissionManager;
import com.cubixos.pocket.accessories.misc.CheckInternet;
import com.cubixos.pocket.adapters.PocketAdapter;
import com.cubixos.pocket.models.Pocket;
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

public class MainActivity extends AppCompatActivity {

    Activity activity;
    Context context;
    PocketAdapter adapterA;
    PocketAdapter adapterB;
    PocketAdapter adapterC;
    PocketAdapter adapterD;
    PocketAdapter adapterE;
    PocketAdapter adapterF;
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
    ImageView imageViewUser;

    RecyclerView recyclerViewA;
    RecyclerView recyclerViewB;
    RecyclerView recyclerViewC;
    RecyclerView recyclerViewD;
    RecyclerView recyclerViewE;
    RecyclerView recyclerViewF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        activity = MainActivity.this;
        context = getApplicationContext();
        //
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseAuth != null) {
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
                            if (CheckInternet.isNetwork(context)) {
                                Picasso.get()
                                        .load(user.getProfilePicUrl())
                                        .placeholder(R.drawable.empty_profile_pic)
                                        .error(R.drawable.ic_warning_sign_board)
                                        .into(imageViewUser);
                            } else {
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
            } else {
                DialogJoin.showDialogJoin(activity, context);
            }
        } else {
            DialogJoin.showDialogJoin(activity, context);
        }
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar = findViewById(R.id.toolbar_main_all);
        }
        appBarLayout = findViewById(R.id.appbar_layout_main_all);
        textViewToolbarTitle = findViewById(R.id.text_view_toolbar_title_main_all);
        setSupportActionBar(toolbar);
        imageViewUser = findViewById(R.id.image_view_user_photo);
        imageViewUser.setOnClickListener(view -> {
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);
        });
        imageViewUser.setOnLongClickListener(view -> {
            //
            return false;
        });
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_main_all);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorBlack, R.color.colorGreyDark);
            adapterA.notifyDataSetChanged();
            adapterB.notifyDataSetChanged();
            adapterC.notifyDataSetChanged();
            adapterD.notifyDataSetChanged();
            adapterE.notifyDataSetChanged();
            adapterF.notifyDataSetChanged();
        });
        fab = findViewById(R.id.fab_main_all);
        fab.setOnClickListener(view -> {
            Intent intentUpload = new Intent(activity, UploadActivity.class);
            startActivity(intentUpload);
        });
        recyclerViewA = findViewById(R.id.recycler_view_a);
        recyclerViewB = findViewById(R.id.recycler_view_b);
        recyclerViewC = findViewById(R.id.recycler_view_c);
        recyclerViewD = findViewById(R.id.recycler_view_d);
        recyclerViewE = findViewById(R.id.recycler_view_e);
        recyclerViewF = findViewById(R.id.recycler_view_f);
        setUpRecyclerViewA();
        setUpRecyclerViewB();
        setUpRecyclerViewC();
        setUpRecyclerViewD();
        setUpRecyclerViewE();
        setUpRecyclerViewF();

        //Permission for storage
        AppPermissionManager.permissionStorage(activity, context);
    }

    private void setUpRecyclerViewA() {
        collectionReference = firebaseFirestore.collection("MScBotany");
        Query query = collectionReference.whereEqualTo("studySubject", "SubjectA").orderBy("timeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Pocket> options = new FirestoreRecyclerOptions.Builder<Pocket>()
                .setQuery(query, Pocket.class)
                .build();
        adapterA = new PocketAdapter(options);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        recyclerViewA.setLayoutManager(gridLayoutManager);
        recyclerViewA.setAdapter(adapterA);
        adapterA.notifyDataSetChanged();
    }

    private void setUpRecyclerViewB() {
        collectionReference = firebaseFirestore.collection("MScBotany");
        Query query = collectionReference.whereEqualTo("studySubject", "SubjectB").orderBy("timeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Pocket> options = new FirestoreRecyclerOptions.Builder<Pocket>()
                .setQuery(query, Pocket.class)
                .build();
        adapterB = new PocketAdapter(options);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        recyclerViewB.setLayoutManager(gridLayoutManager);
        recyclerViewB.setAdapter(adapterB);
    }

    private void setUpRecyclerViewC() {
        collectionReference = firebaseFirestore.collection("MScBotany");
        Query query = collectionReference.whereEqualTo("studySubject", "SubjectC").orderBy("timeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Pocket> options = new FirestoreRecyclerOptions.Builder<Pocket>()
                .setQuery(query, Pocket.class)
                .build();
        adapterC = new PocketAdapter(options);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        recyclerViewC.setLayoutManager(gridLayoutManager);
        recyclerViewC.setAdapter(adapterC);
    }

    private void setUpRecyclerViewD() {
        collectionReference = firebaseFirestore.collection("MScBotany");
        Query query = collectionReference.whereEqualTo("studySubject", "SubjectD").orderBy("timeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Pocket> options = new FirestoreRecyclerOptions.Builder<Pocket>()
                .setQuery(query, Pocket.class)
                .build();
        adapterD = new PocketAdapter(options);
        recyclerViewD = findViewById(R.id.recycler_view_d);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        recyclerViewD.setLayoutManager(gridLayoutManager);
        recyclerViewD.setAdapter(adapterD);
    }

    private void setUpRecyclerViewE() {
        collectionReference = firebaseFirestore.collection("MScBotany");
        Query query = collectionReference.whereEqualTo("studySubject", "SubjectE").orderBy("timeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Pocket> options = new FirestoreRecyclerOptions.Builder<Pocket>()
                .setQuery(query, Pocket.class)
                .build();
        adapterE = new PocketAdapter(options);
        recyclerViewE = findViewById(R.id.recycler_view_e);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        recyclerViewE.setLayoutManager(gridLayoutManager);
        recyclerViewE.setAdapter(adapterE);
    }

    private void setUpRecyclerViewF() {
        collectionReference = firebaseFirestore.collection("MScBotany");
        Query query = collectionReference.whereEqualTo("studySubject", "SubjectF").orderBy("timeStamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Pocket> options = new FirestoreRecyclerOptions.Builder<Pocket>()
                .setQuery(query, Pocket.class)
                .build();
        adapterF = new PocketAdapter(options);
        recyclerViewF = findViewById(R.id.recycler_view_f);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        recyclerViewF.setLayoutManager(gridLayoutManager);
        recyclerViewF.setAdapter(adapterF);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterA.startListening();
        adapterB.startListening();
        adapterC.startListening();
        adapterD.startListening();
        adapterE.startListening();
        adapterF.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterA.stopListening();
        adapterB.stopListening();
        adapterC.stopListening();
        adapterD.stopListening();
        adapterE.stopListening();
        adapterF.stopListening();
    }
    //Email verified check
    private void checkVerifiedEmail() {
        firebaseUser = firebaseAuth.getCurrentUser();
        boolean isVerified = false;
        if (firebaseUser != null) {
            isVerified = firebaseUser.isEmailVerified();
        }
        if (isVerified) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
            //If verified email
            Toast.makeText(context, "Pull down to refresh", Toast.LENGTH_SHORT).show();
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