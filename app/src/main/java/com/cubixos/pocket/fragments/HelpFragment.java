package com.cubixos.pocket.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.cubixos.pocket.R;

public class HelpFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_help, rootKey);
    }
}
