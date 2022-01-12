package io.smileyjoe.putio.tv.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.telecom.Call;
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
import io.smileyjoe.putio.tv.object.DetailsAction;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.GroupType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.object.VideoType;
import io.smileyjoe.putio.tv.ui.activity.PlaybackActivity;
import io.smileyjoe.putio.tv.ui.activity.VideoDetailsActivity;
import io.smileyjoe.putio.tv.ui.fragment.VideoDetailsFragment;

public class VideoDetailsHelper {

    public interface ActionListener{

        Activity getActivity();
        Video getVideo();
        void update(Video video);
        void updateGroupAction(long groupId, @StringRes int verb);
        void updateResumeAction();

        default Context getBaseContext(){
            return getActivity().getBaseContext();
        }

        default void onResumeVideo(){
            play(getActivity(), getVideo(), true);
        }

        default void onWatchVideo(){
            play(getActivity(), getVideo(), false);
        }

        default void onTrailer(){
            getActivity().startActivity(PlaybackActivity.getIntent(getBaseContext(), getVideo().getYoutubeTrailerUrl()));
        }

        default void onRefreshData(){
            Tmdb.update(getBaseContext(), getVideo(), updatedVideo -> update(updatedVideo));
        }

//        default void onConvert(){
//            Putio.convertFile(getBaseContext(), getVideo().getPutId(), result -> Putio.getConversionStatus(getBaseContext(), getVideo().getPutId(), new VideoDetailsFragment.OnConvertResponse()););
//        }

        default void addGroupActions(GetGroups.Listener listener){
            VideoDetailsHelper.GetGroups groupsTask = new VideoDetailsHelper.GetGroups(getBaseContext(), getVideo(), listener);
            groupsTask.execute();
        }

        default void onGroupClicked(long groupId){
            OnGroupClicked task = new OnGroupClicked(groupId, getBaseContext(), getVideo(), this);
            task.execute();
        }

        default void getResumeTime() {
            Putio.getResumeTime(getBaseContext(), getVideo().getPutId(), new OnResumeResponse(getVideo(), this));
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
        private ActionListener mListener;
        private DetailsAction mAction;

        public OnActionButtonClicked(DetailsAction action, ActionListener listener) {
            mAction = action;
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            VideoDetailsHelper.handleActionClick(mAction, mListener);
        }
    }

    public static class OnActionClicked implements OnActionClickedListener {
        private ActionListener mListener;

        public OnActionClicked(ActionListener listener) {
            mListener = listener;
        }

        @Override
        public void onActionClicked(Action action) {
            DetailsAction option = DetailsAction.fromId(action.getId());
            if(option == DetailsAction.UNKNOWN){
                mListener.onGroupClicked(action.getId() - 100);
            } else {
                VideoDetailsHelper.handleActionClick(DetailsAction.fromId(action.getId()), mListener);
            }
        }
    }

    public static void handleActionClick(DetailsAction action, ActionListener listener){
        if(listener != null){
            switch (action){
                case WATCH:
                    listener.onWatchVideo();
                    break;
                case RESUME:
                    listener.onResumeVideo();
                    break;
                case TRAILER:
                    listener.onTrailer();
                    break;
                case REFRESH_DATA:
                    listener.onRefreshData();
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
        private ActionListener mListener;

        public OnGroupClicked(long groupId, Context context, Video video, ActionListener listener) {
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

            if(verb > 0) {
                mListener.updateGroupAction(mGroupId, verb);
            }
        }
    }

    public static class OnResumeResponse extends Response {
        private Video mVideo;
        private ActionListener mListener;

        public OnResumeResponse(Video video, ActionListener listener) {
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
                    mListener.updateResumeAction();
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
