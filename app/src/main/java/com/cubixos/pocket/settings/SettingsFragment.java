package com.cubixos.pocket.settings;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.cubixos.pocket.accessories.dialogs.DialogJoin;
import com.cubixos.pocket.accessories.dialogs.DialogRating;
import com.cubixos.pocket.accessories.dialogs.DialogResetApp;
import com.cubixos.pocket.accessories.dialogs.DialogUpdateApp;
import com.cubixos.pocket.accessories.dialogs.DialogVersionApp;
import com.cubixos.pocket.accessories.misc.FeedbackEmail;
import com.cubixos.pocket.accessories.misc.NotificationsSettings;
import com.cubixos.pocket.accessories.misc.ShareApp;
import com.cubixos.pocket.accessories.misc.WebViewStart;
import com.cubixos.pocket.accessories.notifications.NotificationRefreshApp;
import com.cubixos.pocket.accessories.notifications.NotificationUpdateApp;
import com.cubixos.pocket.oldpack.AccountActivity;
import com.cubixos.pocket.R;
import com.cubixos.pocket.about.AboutActivity;
import com.cubixos.pocket.onboardingscreen.OnBoardingActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    
    GoogleApiClient googleApiClient;
    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseRemoteConfig remoteConfig;
    //
    String currentUserId;
    String currentUserEmail;
    //
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        //
        firebaseAuth = FirebaseAuth.getInstance();
        //Check signed in or not
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserEmail = firebaseAuth.getCurrentUser().getEmail();
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), onConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //
        Preference accountPreference = findPreference("settings_account");//username in title - email in summary
        Preference notificationsPreference = findPreference("settings_notifications");
        SwitchPreference syncDataPreference = findPreference("sync_data");
        SwitchPreference downloadFilesPreference = findPreference("download_files");

        Preference versionAppPreference = findPreference("version_app");
        Preference versionAndroidPreference = findPreference("version_android");
        Preference checkUpdatePreference = findPreference("app_check_update");
        Preference clearCachePreference = findPreference("app_clear_cache");
        Preference resetPreference = findPreference("app_reset");
        Preference sharePreference = findPreference("app_share");
        Preference infoPreference = findPreference("app_info");
        Preference helpPreference = findPreference("app_help");
        Preference policyPreference = findPreference("app_policy");
        Preference appsCodixosPreference = findPreference("apps_cubixos");
        Preference aboutPreference = findPreference("app_about");
        Preference introPreference = findPreference("app_intro");
        Preference feedbackPreference = findPreference("about_feedback");
        Preference ratingPreference = findPreference("app_rating");
        //Account
        accountPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(firebaseUser!=null){
                    //if signed - show account details
                    Intent intentAccount = new Intent(getActivity(), AccountActivity.class);
                    startActivity(intentAccount);
                }else {
                    //if not signed - show dialog to join
                    DialogJoin.showDialogJoin(getActivity(), getContext());
                }
                return true;
            }
        });
        //Account Summary line
        if(firebaseUser!=null){//if signed in
            accountPreference.setSummary(currentUserEmail);
        }else {//if not signed in
            accountPreference.setSummary("You are not Logged In.");
        }
        //Notifications
        notificationsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(), "Notifications must be enabled to stay up-to-date",Toast.LENGTH_SHORT).show();
                NotificationsSettings.showNotificationsSettings(getActivity(), getContext());
                return true;
            }
        });
        //Switches
        syncDataPreference.setEnabled(true);//Preference Active
        syncDataPreference.setChecked(true);////Preference Checked Switch ON
        downloadFilesPreference.setEnabled(false);//Preference Disable
        //
        //listPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
        //editTextPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
        //WebView - Cubixos
        //Android Version
        String versionAndroid = Build.VERSION.RELEASE;
        String versionAndroidBuild = Build.VERSION.CODENAME;
        versionAndroidPreference.setSummary(versionAndroid+" - "+versionAndroidBuild);
        // App Version
        String version = "1.0";
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionAppPreference.setSummary(version);
        versionAppPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogVersionApp.showVersionDialog(getActivity(), getContext());
                return true;
            }
        });
        //Check Update
        checkUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                checkUpdate();
                return true;
            }
        });
        //Clear Cache
        clearCachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                NotificationRefreshApp.showNotificationRefreshApp(getActivity(), getContext());
                return true;
            }
        });
        //Reset
        resetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Clear cache and data of app from phone internal storage of phone - Reset Completely
                DialogResetApp.showDialogResetApp(getActivity(), getContext());
                return true;
            }
        });
        //Share
        sharePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Clear cache and data of app from phone internal storage of phone - Reset Completely
                ShareApp.saveApp(getActivity(), getContext());
                return true;
            }
        });
        //Info
        infoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intentInfo = new Intent(getActivity(), InfoActivity.class);
                startActivity(intentInfo);
                return true;
            }
        });
        //Help
        helpPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intentHelp = new Intent(getActivity(), HelpActivity.class);
                startActivity(intentHelp);
                return true;
            }
        });
        //Policy
        policyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                WebViewStart.startWebView(getActivity(), getContext(), "http://policies.google.com/privacy?hl=en-GB");
                return true;
            }
        });
        //Apps from Cubixos
        appsCodixosPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //
                return true;
            }
        });
        aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intentApps = new Intent(getActivity(), AboutActivity.class);
                startActivity(intentApps);
                return true;
            }
        });
        introPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intentApps = new Intent(getActivity(), OnBoardingActivity.class);
                startActivity(intentApps);
                return true;
            }
        });
        ratingPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogRating.showDialogRating(getActivity(), getContext());
                return true;
            }
        });
        feedbackPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(FeedbackEmail.intentFeedbackEmail(getActivity(), getContext()));
                return true;
            }
        });
    }
    //
    private void checkUpdate() {
        HashMap<String, Object> defaultsRate = new HashMap<>();
        defaultsRate.put("new Version Code", String.valueOf(getVersionCode()));
        defaultsRate.put("new App Url", "");
        defaultsRate.put("new App Size", "");
        defaultsRate.put("new App Description", "");
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(10)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(defaultsRate);
        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    final String new_version_code = remoteConfig.getString("new_version_code");
                    String new_version_url = remoteConfig.getString("new_version_url");
                    String new_version_size = remoteConfig.getString("new_version_size");
                    String new_version_description = remoteConfig.getString("new_version_description");
                    if (Integer.parseInt(new_version_code) > getVersionCode()) {
                        DialogUpdateApp.showUpdateDialog(getActivity(), getContext(), new_version_url, new_version_size, new_version_description );
                        NotificationUpdateApp.notificationSettingUpdateYes(getActivity(), getContext());
                    } else {
                        NotificationUpdateApp.notificationSettingUpdateNo(getActivity(), getContext());
                    }
                }
            }
        });
    }
    //App version
    private int getVersionCode() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("VersionCode", "NameNotFoundException: " + e.getMessage());
        }
        return Objects.requireNonNull(packageInfo).versionCode;
    }
}