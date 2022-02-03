package io.smileyjoe.putio.tv.object;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "character")
public class Character implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;
    @ColumnInfo(name = "video_tmdb_id")
    private long mVideoTmdbId;
    @ColumnInfo(name = "cast_member_tmdb_id")
    private long mCastMemberTmdbId;
    @ColumnInfo(name = "name")
    private String mName;
    @ColumnInfo(name = "profile_image")
    private String mProfileImage;
    @ColumnInfo(name = "cast_member_name")
    private String mCastMemberName;
    @ColumnInfo(name = "order")
    private int mOrder;

    public void setId(int id) {
        mId = id;
    }

    public void setVideoTmdbId(long videoTmdbId) {
        mVideoTmdbId = videoTmdbId;
    }

    public void setCastMemberTmdbId(long castMemberTmdbId) {
        mCastMemberTmdbId = castMemberTmdbId;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setProfileImage(String profileImage) {
        mProfileImage = profileImage;
    }

    public void setCastMemberName(String castMemberName) {
        mCastMemberName = castMemberName;
    }

    public void setOrder(int order) {
        mOrder = order;
    }

    public int getId() {
        return mId;
    }

    public long getVideoTmdbId() {
        return mVideoTmdbId;
    }

    public long getCastMemberTmdbId() {
        return mCastMemberTmdbId;
    }

    public String getName() {
        return mName;
    }

    public String getProfileImage() {
        return mProfileImage;
    }

    public Uri getProfileImageAsUri() {
        if (!TextUtils.isEmpty(mProfileImage)) {
            return Uri.parse(mProfileImage);
        } else {
            return null;
        }
    }

    public String getCastMemberName() {
        return mCastMemberName;
    }

    public int getOrder() {
        return mOrder;
    }

    @Override
    public String toString() {
        return "Character{" +
                "mId=" + mId +
                ", mVideoTmdbId=" + mVideoTmdbId +
                ", mCastMemberTmdbId=" + mCastMemberTmdbId +
                ", mName='" + mName + '\'' +
                ", mProfileImage='" + mProfileImage + '\'' +
                ", mCastMemberName='" + mCastMemberName + '\'' +
                ", mOrder=" + mOrder +
                '}';
    }

    public Character() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeLong(this.mVideoTmdbId);
        dest.writeLong(this.mCastMemberTmdbId);
        dest.writeString(this.mName);
        dest.writeString(this.mProfileImage);
        dest.writeString(this.mCastMemberName);
        dest.writeInt(this.mOrder);
    }

    protected Character(Parcel in) {
        this.mId = in.readInt();
        this.mVideoTmdbId = in.readLong();
        this.mCastMemberTmdbId = in.readLong();
        this.mName = in.readString();
        this.mProfileImage = in.readString();
        this.mCastMemberName = in.readString();
        this.mOrder = in.readInt();
    }

    public static final Creator<Character> CREATOR = new Creator<Character>() {
        @Override
        public Character createFromParcel(Parcel source) {
            return new Character(source);
        }

        @Override
        public Character[] newArray(int size) {
            return new Character[size];
        }
    };
}
