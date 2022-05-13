package io.smileyjoe.putio.tv.action.video;

import com.google.gson.JsonObject;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.video.VideoCache;

public interface ConvertAction extends Action {

    void updateActionConvert(String title);

    @Override
    default void setupActions() {
        if (getVideo().isConverting()) {
            addAction(ActionOption.CONVERT, getString(R.string.action_converting), null, true);
        } else if (getVideo().isConverted()) {
            addAction(ActionOption.CONVERT, getString(R.string.action_play_mp4), null, true);
        } else {
            addAction(ActionOption.CONVERT, true);
        }
    }

    @Override
    default void handleClick(ActionOption option) {
        if (option == ActionOption.CONVERT) {
            if (getVideo().isConverting()) {
                play(getVideo(), true);
            } else if (getVideo().isConverted()) {
                play(getVideo(), true, true);
            } else {
                Putio.Convert.start(getContext(), getVideo().getPutId(), new Response() {
                    @Override
                    public void onSuccess(JsonObject result) {
                        getVideo().setConverting(true);
                        VideoCache.getInstance().update(getVideo());
                        updateActionConvert(getString(R.string.action_converting));
                        play(getVideo(), true);
                    }
                });
            }
        }
    }

}
