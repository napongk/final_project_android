package budgetapp.napkkk.ourbudget2.controller.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by napkkk on 24/11/2560.
 */

public class SectionPageAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentListTitleList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title){
        mFragmentList.add(fragment);
        mFragmentListTitleList.add(title);
    }

    public SectionPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentListTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
