package com.zeninstudios.insightengineer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new HomePageFrag();
            case 1:
                return new SubOnePageFrag();
            case 2:
                return new SubTwoPageFrag();
            case 3:
                return new SubThreePageFrag();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
