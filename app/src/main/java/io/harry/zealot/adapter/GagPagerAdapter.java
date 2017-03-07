package io.harry.zealot.adapter;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.harry.zealot.fragment.GagFragment;

public class GagPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<GagFragment> gagFragments;

    public <T> GagPagerAdapter(FragmentManager fragmentManager, List<T> gags) {
        super(fragmentManager);

        gagFragments = new ArrayList<>();
        //TODO gagPagerAdapter test needed
        for (T gag : gags) {
            GagFragment gagFragment = null;
            if (gag instanceof Uri) {
                gagFragment = GagFragment.newInstance((Uri) gag);
            } else if (gag instanceof Integer) {
                gagFragment = GagFragment.newInstance((Integer) gag);
            }

            gagFragments.add(gagFragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return gagFragments.get(position);
    }

    @Override
    public int getCount() {
        return gagFragments.size();
    }
}
