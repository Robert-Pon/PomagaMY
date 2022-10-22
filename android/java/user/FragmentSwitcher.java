package com.name.social_helper_r_p.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.name.social_helper_r_p.R;

public class FragmentSwitcher extends Fragment {
    ViewPager2 pager;

    ProfileFragment profileFragment = new ProfileFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_switcher, container, false);
        pager = v.findViewById(R.id.pager);
        FragmentSwitcher.pager pager1 = new FragmentSwitcher.pager(getActivity());
        pager.setAdapter(pager1);

        return v;
    }

    public void swipe(int pos){
        pager.setCurrentItem(pos);
    }
    public class pager extends FragmentStateAdapter{

        public pager(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return profileFragment;
                case 1:
                    return settingsFragment;
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
