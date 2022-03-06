package io.smileyjoe.putio.tv.ui.fragment.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.object.Account;
import io.smileyjoe.putio.tv.util.Format;

public class SettingsAccountFragment extends SettingsBaseFragment {

    public static GuidedAction getAction(Context context, int id) {
        return new GuidedAction.Builder(context)
                .id(id)
                .title(R.string.settings_title_account)
                .description(R.string.settings_description_account)
                .build();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Account.get(getContext(), account -> {
            List<GuidedAction> actions = new ArrayList<>();

            actions.add(getAction(R.string.settings_title_username, account.getUserName()));
            actions.add(getAction(R.string.settings_title_disk_available, Format.size(getContext(), account.getDiskAvailable())));
            actions.add(getAction(R.string.settings_title_disk_size, Format.size(getContext(), account.getDiskSize())));
            actions.add(getAction(R.string.settings_title_disk_used, Format.size(getContext(), account.getDiskUsed())));
            actions.add(getAction(R.string.settings_title_bandwidth, Format.size(getContext(), account.getBandwidthUsage())));
            actions.add(getAction(R.string.settings_title_expiration, Format.date(account.getExpirationDate())));

            setActions(actions);
        });
    }

    @Override
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
        return getGuidance(R.string.settings_title_account, R.string.settings_description_account);
    }

}
