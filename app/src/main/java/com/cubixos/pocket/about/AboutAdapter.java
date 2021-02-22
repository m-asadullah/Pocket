package com.cubixos.pocket.about;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class AboutAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public AboutAdapter(FragmentManager fm, int NoofTabs){
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                FragmentAbout home = new FragmentAbout();
                return home;
            case 1:
                FragmentServices about = new FragmentServices();
                return about;
            case 2:
                FragmentUpdates contact = new FragmentUpdates();
                return contact;
            default:
                return null;
        }
    }
}