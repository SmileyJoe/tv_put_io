package io.smileyjoe.putio.tv.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.interfaces.FilterItem;

@Entity(tableName = "group")
public class Group implements FilterItem, Parcelable {

    public static int DEFAULT_ID_MOVIES = 1;
    public static int DEFAULT_ID_SERIES = 2;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;
    @ColumnInfo(name = "title")
    private String mTitle;
    @ColumnInfo(name = "put_ids_json")
    private String mPutIdsJson;
    @Ignore
    private ArrayList<Long> mPutIds;

    public Group() {
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public ArrayList<Long> getPutIds() {
        return mPutIds;
    }

    public String getPutIdsJson() {
        return mPutIdsJson;
    }

    @Override
    public int getIconResId() {
        @DrawableRes int iconResId;

        if(getId() == Group.DEFAULT_ID_MOVIES){
            iconResId = R.drawable.ic_movie_24;
        } else if (getId() == Group.DEFAULT_ID_SERIES){
            iconResId = R.drawable.ic_series_24;
        } else {
            iconResId = R.drawable.ic_folder_24;
        }

        return iconResId;
    }

    @Override
    public boolean isDefaultSelected() {
        return false;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setPutIds(ArrayList<Long> putIds) {
        mPutIds = putIds;

        if(TextUtils.isEmpty(mPutIdsJson)) {
            setPutIdsJson();
        }
    }

    public void addPutId(Long putId){
        if(mPutIds == null){
            mPutIds = new ArrayList<>();
        }

        mPutIds.add(putId);

        setPutIdsJson();
    }

    private void setPutIdsJson(){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        setPutIdsJson(gson.toJson(mPutIds, type));
    }

    public void setPutIdsJson(String putIdsJson) {
        mPutIdsJson = putIdsJson;

        if(mPutIds == null || mPutIds.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Long>>() {
            }.getType();
            setPutIds(gson.fromJson(putIdsJson, type));
        }
    }

    public Video toVideo(){
        Video video = new Video();
        video.setFileType(FileType.GROUP);
        video.setPutId(getId());
        video.setTitle(getTitle());
        video.setGenreIdsJson(getPutIdsJson());
        return video;
    }

    @Override
    public String toString() {
        return "Group{" +
                "mId=" + mId +
                ", mTitle='" + mTitle + '\'' +
                ", mPutIdsJson='" + mPutIdsJson + '\'' +
                ", mPutIds=" + mPutIds +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mPutIdsJson);
        dest.writeList(this.mPutIds);
    }

    protected Group(Parcel in) {
        this.mId = in.readInt();
        this.mTitle = in.readString();
        this.mPutIdsJson = in.readString();
        this.mPutIds = new ArrayList<Long>();
        in.readList(this.mPutIds, Long.class.getClassLoader());
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
