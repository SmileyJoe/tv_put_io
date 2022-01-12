package io.smileyjoe.putio.tv.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.OnActionClickedListener;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.db.GroupDao;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.network.Tmdb;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.GroupType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;

public class VideoAction {

    public interface Listener {
        Activity getActivity();
        Video getVideo();
        void update(Video video);
        void updateActionGroup(long groupId, @StringRes int verb);
        void updateActionResume();

        default Context getBaseContext(){
            return getActivity().getBaseContext();
        }

        default void resumeVideo(){
            play(getActivity(), getVideo(), true);
        }

        default void playVideo(){
            play(getActivity(), getVideo(), false);
        }

        default void playTrailer(){
            getActivity().startActivity(PlaybackActivity.getIntent(getBaseContext(), getVideo().getYoutubeTrailerUrl()));
        }

        default void refreshData(){
            Tmdb.update(getBaseContext(), getVideo(), updatedVideo -> update(updatedVideo));
        }

//        default void onConvert(){
//            Putio.convertFile(getBaseContext(), getVideo().getPutId(), result -> Putio.getConversionStatus(getBaseContext(), getVideo().getPutId(), new VideoDetailsFragment.OnConvertResponse()););
//        }

        default void addGroupActions(GetGroups.Listener listener){
            VideoAction.GetGroups groupsTask = new VideoAction.GetGroups(getBaseContext(), getVideo(), listener);
            groupsTask.execute();
        }

        default void onGroupActionClicked(long groupId){
            OnGroupClicked task = new OnGroupClicked(groupId, getBaseContext(), getVideo(), this);
            task.execute();
        }

        default void getResumeTime() {
            Putio.getResumeTime(getBaseContext(), getVideo().getPutId(), new OnResumeResponse(getVideo(), this));
        }
    }

    public enum Option {
        UNKNOWN(0, 0, false, -1),
        WATCH(1, R.string.action_watch, true, 0),
        RESUME(2, R.string.action_resume, true, 1),
        CONVERT(3, R.string.action_convert, false, 2),
        TRAILER(4, R.string.action_trailer, false, 3),
        REFRESH_DATA(5, R.string.action_refresh, true, 4);

        private long mId;
        private @StringRes int mTitleResId;
        private boolean mShow;
        private int mPosition;

        Option(long id, int titleResId, boolean show, int position) {
            mId = id;
            mTitleResId = titleResId;
            mShow = show;
            mPosition = position;
        }

        public long getId() {
            return mId;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public boolean shouldShow() {
            return mShow;
        }

        public int getPosition() {
            return mPosition;
        }

        public static Option fromId(long id) {
            for (Option option : Option.values()) {
                if (option.getId() == id) {
                    return option;
                }
            }

            return UNKNOWN;
        }
    }

    public static void play(Activity activity, Video video, boolean shouldResume){
        play(activity, video, null, shouldResume);
    }

    public static void play(Activity activity, Video video, ArrayList<Video> videos, boolean shouldResume){
        if(video.getVideoType() == VideoType.EPISODE){
            if(videos != null && !videos.isEmpty()) {
                activity.startActivity(PlaybackActivity.getIntent(activity.getBaseContext(), videos, video, shouldResume));
                return;
            }
        }

        activity.startActivity(PlaybackActivity.getIntent(activity.getBaseContext(), video, shouldResume));
    }

    public static class OnActionButtonClicked implements View.OnClickListener {
        private Listener mListener;
        private Option mOption;

        public OnActionButtonClicked(Option option, Listener listener) {
            mOption = option;
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            VideoAction.handleActionClick(mOption, mListener);
        }
    }

    public static class OnActionClicked implements OnActionClickedListener {
        private Listener mListener;

        public OnActionClicked(Listener listener) {
            mListener = listener;
        }

        @Override
        public void onActionClicked(Action action) {
            Option option = Option.fromId(action.getId());
            if(option == Option.UNKNOWN){
                mListener.onGroupActionClicked(action.getId() - 100);
            } else {
                VideoAction.handleActionClick(Option.fromId(action.getId()), mListener);
            }
        }
    }

    public static void handleActionClick(Option option, Listener listener){
        if(listener != null){
            switch (option){
                case WATCH:
                    listener.playVideo();
                    break;
                case RESUME:
                    listener.resumeVideo();
                    break;
                case TRAILER:
                    listener.playTrailer();
                    break;
                case REFRESH_DATA:
                    listener.refreshData();
                    break;
//                case CONVERT:
//                    listener.onConvert();
//                    break;
            }
        }
    }

    public static class GetGroups extends AsyncTask<Void, Void, List<Group>> {
        public interface Listener{
            void update(Group group, String verb, String title);
        }
        private Context mContext;
        private Video mVideo;
        private Listener mListener;

        public GetGroups(Context context, Video video, Listener listener) {
            mContext = context;
            mVideo = video;
            mListener = listener;
        }

        @Override
        protected List<Group> doInBackground(Void... voids) {
            return AppDatabase.getInstance(mContext).groupDao().getByType(GroupType.VIDEO.getId());
        }

        @Override
        protected void onPostExecute(List<Group> groups) {
            super.onPostExecute(groups);

            if(groups != null && !groups.isEmpty()){
                for(Group group:groups){
                    @StringRes int subTextResId;

                    if(group.getPutIds().contains(mVideo.getPutId())){
                        subTextResId = R.string.text_remove_from;
                    } else {
                        subTextResId = R.string.text_add_to;
                    }

                    if(mListener != null){
                        mListener.update(group, mContext.getString(subTextResId), group.getTitle());
                    }
                }
            }
        }
    }

    public static class OnGroupClicked extends AsyncTask<Void, Void, Integer>{
        private long mGroupId;
        private Context mContext;
        private Video mVideo;
        private Listener mListener;

        public OnGroupClicked(long groupId, Context context, Video video, Listener listener) {
            mGroupId = groupId;
            mContext = context;
            mVideo = video;
            mListener = listener;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            @StringRes int verb = -1;

            if(mGroupId == Group.DEFAULT_ID_WATCH_LATER
                    || mGroupId == Group.DEFAULT_ID_FAVOURITE) {
                long putId = mVideo.getPutId();

                GroupDao groupDao = AppDatabase.getInstance(mContext).groupDao();
                Group group = groupDao.get(mGroupId);

                if(group.getPutIds().contains(putId)){
                    group.removePutId(putId);
                    verb = R.string.text_add_to;
                } else {
                    group.addPutId(putId);
                    verb = R.string.text_remove_from;
                }

                groupDao.insert(group);
            }

            return verb;
        }

        @Override
        protected void onPostExecute(Integer verb) {
            super.onPostExecute(verb);

            if(verb > 0 && mListener != null) {
                mListener.updateActionGroup(mGroupId, verb);
            }
        }
    }

    public static class OnResumeResponse extends Response {
        private Video mVideo;
        private Listener mListener;

        public OnResumeResponse(Video video, Listener listener) {
            mVideo = video;
            mListener = listener;
        }

        @Override
        public void onSuccess(JsonObject result) {
            try {
                long resumeTime = result.get("start_from").getAsLong();
                mVideo.setResumeTime(resumeTime);
            } catch (UnsupportedOperationException | NullPointerException e) {
                mVideo.setResumeTime(0);
            }

            if (mVideo.getResumeTime() > 0) {
                if(mListener != null) {
                    mListener.updateActionResume();
                }
            }
        }
    }

//    private static class OnConvertResponse extends Response {
//        @Override
//        public void onSuccess(JsonObject result) {
//            // todo: this isn't working correctly //
//            VideoDetailsFragment detailsFragment = (VideoDetailsFragment) getFragmentManager().findFragmentById(R.id.details_fragment);
//            detailsFragment.conversionStarted();
//        }
//    }

//    private static class OnConvertResponse extends Response {
//        @Override
//        public void onSuccess(JsonObject result) {
//            int percentDone = -1;
//
//            try {
//                percentDone = result.get("mp4").getAsJsonObject().get("percent_done").getAsInt();
//            } catch (UnsupportedOperationException | NullPointerException e) {
//
//            }
//
//            if (percentDone >= 0) {
//                Action action = getAction(DetailsAction.CONVERT.getId());
//
//                if (action != null) {
//                    if (percentDone < 100) {
//                        action.setLabel2(percentDone + "%");
//                        updateActions(action);
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                getConversionStatus();
//                            }
//                        }, 1000);
//                    } else {
//                        mActionAdapter.remove(action);
//                        updateActions(action);
//                        mVideo.setConverted(true);
//                    }
//                }
//            }
//        }
//    }
}
