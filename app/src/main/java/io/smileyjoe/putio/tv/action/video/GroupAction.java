package io.smileyjoe.putio.tv.action.video;

import android.content.Context;

import androidx.annotation.StringRes;

import java.util.List;
import java.util.Optional;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.db.AppDatabase;
import io.smileyjoe.putio.tv.db.GroupDao;
import io.smileyjoe.putio.tv.object.Group;
import io.smileyjoe.putio.tv.object.GroupType;
import io.smileyjoe.putio.tv.object.Video;
import io.smileyjoe.putio.tv.util.Async;
import io.smileyjoe.putio.tv.util.Settings;

public interface GroupAction extends Action {

    void updateActionGroup(long groupId, @StringRes int verb);
    void addActionGroup(Group group, String verb, String title);

    default void addGroupActions(Get.Listener listener) {
        new Get(getContext(), getVideo(), listener).run();
    }

    @Override
    default void setupActions() {
        addGroupActions((group, verb, title) -> addActionGroup(group, verb, title));
    }

    @Override
    default void handleClick(ActionOption option) {
        // do nothing, this is handled in onGroupActionClicked //
    }

    default void onGroupActionClicked(long groupId) {
        new OnClicked(groupId, getContext(), getVideo(), this).run();
    }

    default long getGroupActionId(long groupId) {
        return groupId + 100;
    }

    default long getGroupId(long actionId) {
        return actionId - 100;
    }

    class Get extends Async.Runner<List<io.smileyjoe.putio.tv.object.Group>> {
        public interface Listener {
            void update(Group group, String verb, String title);
        }

        private Context mContext;
        private Video mVideo;
        private Optional<Listener> mListener;

        public Get(Context context, Video video, Get.Listener listener) {
            mContext = context;
            mVideo = video;
            mListener = Optional.ofNullable(listener);
        }

        @Override
        protected List<io.smileyjoe.putio.tv.object.Group> onBackground() {
            return AppDatabase.getInstance(mContext).groupDao().getByType(GroupType.VIDEO.getId());
        }

        @Override
        protected void onMain(List<io.smileyjoe.putio.tv.object.Group> groups) {
            if (groups != null && !groups.isEmpty()) {
                groups.stream()
                        .filter(Group::isEnabled)
                        .forEach(group -> {
                            @StringRes int subTextResId;

                            if (group.getPutIds().contains(mVideo.getPutId())) {
                                subTextResId = R.string.text_remove_from;
                            } else {
                                subTextResId = R.string.text_add_to;
                            }

                            mListener.ifPresent(listener -> listener.update(group, mContext.getString(subTextResId), group.getTitle()));
                        });
            }
        }
    }

    class OnClicked extends Async.Runner<Integer> {
        private long mGroupId;
        private Context mContext;
        private Video mVideo;
        private Optional<GroupAction> mListener;

        public OnClicked(long groupId, Context context, Video video, GroupAction listener) {
            mGroupId = groupId;
            mContext = context;
            mVideo = video;
            mListener = Optional.ofNullable(listener);
        }

        @Override
        protected Integer onBackground() {
            @StringRes int verb = -1;

            if (mGroupId == io.smileyjoe.putio.tv.object.Group.DEFAULT_ID_WATCH_LATER
                    || mGroupId == io.smileyjoe.putio.tv.object.Group.DEFAULT_ID_FAVOURITE) {
                long putId = mVideo.getPutId();

                GroupDao groupDao = AppDatabase.getInstance(mContext).groupDao();
                io.smileyjoe.putio.tv.object.Group group = groupDao.get(mGroupId);

                if (group.getPutIds().contains(putId)) {
                    group.removePutId(putId);
                    verb = R.string.text_add_to;
                } else {
                    group.addPutId(putId);
                    verb = R.string.text_remove_from;
                }

                Settings.getInstance(mContext).saveGroupPutIds(mContext, group);
                groupDao.updatePutIds(group.getId(), group.getPutIdsJson());
            }

            return verb;
        }

        @Override
        protected void onMain(Integer verb) {
            if (verb > 0 && mListener.isPresent()) {
                mListener.get().updateActionGroup(mGroupId, verb);
            }
        }
    }
}
