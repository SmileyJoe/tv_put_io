package io.smileyjoe.putio.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

import java.util.Locale;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.databinding.FragmentAccountBinding;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
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

    private void populateAccount(){
        Putio.Account.info(getContext(), new Response() {
            @Override
            public void onSuccess(JsonObject result) {
                Account account = Account.fromApi(result);
                mView.textAccountUsername.setText(account.getUserName());
                mView.textUsageAvailable.setText(getString(R.string.text_usage_available, Format.size(getContext(), account.getDiskAvailable())).toUpperCase(Locale.ROOT));
                mView.progressUsage.setMax(Math.toIntExact(account.getDiskSize()/1000000));
                mView.progressUsage.setProgress(Math.toIntExact(account.getDiskUsed()/1000000));
            }
        });
    }

    public void setOnClickListener(View.OnClickListener listener){
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
