package io.smileyjoe.putio.tv.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;

public interface Base {
    Context getBaseContext();
    void onResume();

    ArrayList<BroadcastReceiver> registeredReceivers = new ArrayList<>();

    default void registerReceiver(String type, Broadcast.Listener listener) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                listener.onReceive(context, intent);
            }
        };

        getBaseContext().registerReceiver(receiver, new IntentFilter(type));

        registeredReceivers.add(receiver);
    }

    default void onPause() {
        registeredReceivers.forEach(receiver -> getBaseContext().unregisterReceiver(receiver));
        registeredReceivers.removeAll(registeredReceivers);
    }

}
