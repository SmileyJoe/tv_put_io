package io.smileyjoe.putio.tv.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Entity(tableName = "group")
public class Group implements Parcelable {

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

    public void setId(int id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setPutIds(ArrayList<Long> putIds) {
        mPutIds = putIds;

        if(TextUtils.isEmpty(mPutIdsJson)){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            setPutIdsJson(gson.toJson(putIds, type));
        }
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
