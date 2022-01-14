package io.smileyjoe.putio.tv.action.video;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.StringRes;

import java.util.List;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.db.GroupDao;
import io.smileyjoe.putio.tv.object.GroupType;
import io.smileyjoe.putio.tv.object.Video;

public interface GroupAction extends Action{

    void updateActionGroup(long groupId, @StringRes int verb);
    void addActionGroup(io.smileyjoe.putio.tv.object.Group group, String verb, String title);

    default void addGroupActions(Get.Listener listener){
        Get groupsTask = new Get(getBaseContext(), getVideo(), listener);
        groupsTask.execute();
    }

    @Override
    default void setupActions(){
        addGroupActions((group, verb, title) -> {
            addActionGroup(group, verb, title);
        });
    }

    @Override
    default void handleClick(ActionOption option){
        // do nothing, this is handled in onGroupActionClicked //
    }

    default void onGroupActionClicked(long groupId){
        OnClicked task = new OnClicked(groupId, getBaseContext(), getVideo(), this);
        task.execute();
    }

    default long getGroupActionId(long groupId){
        return groupId + 100;
    }

    default long getGroupId(long actionId){
        return actionId - 100;
    }

    class Get extends AsyncTask<Void, Void, List<io.smileyjoe.putio.tv.object.Group>> {
        public interface Listener{
            void update(io.smileyjoe.putio.tv.object.Group group, String verb, String title);
        }
        private Context mContext;
        private Video mVideo;
        private Get.Listener mListener;

        public Get(Context context, Video video, Get.Listener listener) {
            mContext = context;
            mVideo = video;
            mListener = listener;
        }

        @Override
        protected List<io.smileyjoe.putio.tv.object.Group> doInBackground(Void... voids) {
            return AppDatabase.getInstance(mContext).groupDao().getByType(GroupType.VIDEO.getId());
        }

        @Override
        protected void onPostExecute(List<io.smileyjoe.putio.tv.object.Group> groups) {
            super.onPostExecute(groups);

            if(groups != null && !groups.isEmpty()){
                for(io.smileyjoe.putio.tv.object.Group group:groups){
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

    class OnClicked extends AsyncTask<Void, Void, Integer>{
        private long mGroupId;
        private Context mContext;
        private Video mVideo;
        private GroupAction mListener;

        public OnClicked(long groupId, Context context, Video video, GroupAction listener) {
            mGroupId = groupId;
            mContext = context;
            mVideo = video;
            mListener = listener;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            @StringRes int verb = -1;

            if(mGroupId == io.smileyjoe.putio.tv.object.Group.DEFAULT_ID_WATCH_LATER
                    || mGroupId == io.smileyjoe.putio.tv.object.Group.DEFAULT_ID_FAVOURITE) {
                long putId = mVideo.getPutId();

                GroupDao groupDao = AppDatabase.getInstance(mContext).groupDao();
                io.smileyjoe.putio.tv.object.Group group = groupDao.get(mGroupId);

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
}
