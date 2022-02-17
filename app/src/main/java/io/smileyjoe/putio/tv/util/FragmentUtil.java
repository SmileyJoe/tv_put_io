package io.smileyjoe.putio.tv.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Arrays;

public class FragmentUtil {

    public static void hideFragment(FragmentManager manager, Fragment... fragments) {
        FragmentTransaction transaction = manager.beginTransaction();
        Arrays.stream(fragments).forEach(fragment -> {
            if (fragment != null) {
                transaction.hide(fragment);
            }
        });
        transaction.commit();
    }

    public static void showFragment(FragmentManager manager, Fragment... fragments) {
        FragmentTransaction transaction = manager.beginTransaction();
        Arrays.stream(fragments).forEach(fragment -> {
            if (fragment != null) {
                transaction.show(fragment);
            }
        });
        transaction.commit();
    }

}
