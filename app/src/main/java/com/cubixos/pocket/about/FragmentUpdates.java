package com.cubixos.pocket.about;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.cubixos.pocket.R;

public class FragmentUpdates extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_updates, rootKey);
    }
}