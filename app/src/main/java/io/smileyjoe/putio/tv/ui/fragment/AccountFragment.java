package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.FragmentAccountBinding;
import io.smileyjoe.putio.tv.object.Account;
import io.smileyjoe.putio.tv.object.FragmentType;
import io.smileyjoe.putio.tv.util.Format;

public class AccountFragment extends BaseFragment<FragmentAccountBinding> {

    @Override
    protected FragmentAccountBinding inflate(LayoutInflater inflater, ViewGroup container, boolean savedInstanceState) {
        return FragmentAccountBinding.inflate(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateAccount();
    }

    private void populateAccount() {
        Account.get(getContext(), account -> {
            mView.textAccountUsername.setText(account.getUserName());
            mView.textUsageAvailable.setText(getString(R.string.text_usage_available, Format.size(getContext(), account.getDiskAvailable())).toUpperCase(Locale.ROOT));
            mView.progressUsage.setMax(Math.toIntExact(account.getDiskSize() / 1000000));
            mView.progressUsage.setProgress(Math.toIntExact(account.getDiskUsed() / 1000000));
        });
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mView.imageSettings.setOnClickListener(listener);
    }

    @Override
    public FragmentType getType() {
        return FragmentType.ACCOUNT;
    }

    @Override
    public View getFocusableView() {
        return mView.imageSettings;
    }
}
