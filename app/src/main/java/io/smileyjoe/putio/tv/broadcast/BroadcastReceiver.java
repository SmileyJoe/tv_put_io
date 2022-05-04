package io.smileyjoe.putio.tv.broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;

public interface BroadcastReceiver {
    Context getContext();
    void registerReceiver();

    ArrayList<android.content.BroadcastReceiver> registeredReceivers = new ArrayList<>();

    default void registerReceiver(String type, Broadcast.Listener listener) {
        android.content.BroadcastReceiver receiver = new android.content.BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                listener.onReceive(context, intent);
            }
        };

        getContext().registerReceiver(receiver, new IntentFilter(type));

        registeredReceivers.add(receiver);
    }

    default void deregisterReceiver() {
        registeredReceivers.forEach(receiver -> {
            try {
                getContext().unregisterReceiver(receiver);
            } catch (IllegalArgumentException e){
                // do nothing, receiver has been deregistered already //
            }
        });
        registeredReceivers.removeAll(registeredReceivers);
    }

}
