package com.cubixos.pocket.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.cubixos.pocket.R;

public class FragmentAbout extends PreferenceFragmentCompat {
    @Override

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_about, rootKey);

        Preference courseGPCPreference = findPreference("course_gpc");
        Preference courseIUBPreference = findPreference("course_iub");
        Preference contactEmailPreference = findPreference("contact_email");
        Preference appFeaturesPreference = findPreference("app_features");

        courseGPCPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(), "You are good to go!",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        courseIUBPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(), "You are good to go!",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        contactEmailPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                sendEmail();
                return true;
            }
        });
        appFeaturesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(), "You are good to go!",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void sendEmail() {
        Intent openWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:cubixos@yahoo.com"));
        openWebIntent.putExtra(Intent.EXTRA_EMAIL, "Email about Math App");
        openWebIntent.putExtra(Intent.EXTRA_SUBJECT, "Email about Math App");
        openWebIntent.putExtra(Intent.EXTRA_TEXT, "Hi App Developer, \n ");
        Intent intentChooser = Intent.createChooser(openWebIntent, "Contact via");
        if (openWebIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                startActivity(intentChooser);
            }catch (Exception e){
                Toast.makeText(getContext(), "Error: 56 - "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
}