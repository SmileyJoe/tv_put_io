package io.smileyjoe.putio.tv.object;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.smileyjoe.putio.tv.util.JsonUtil;

public class Conversion {

    public enum Status {
        IN_QUEUE("IN_QUEUE"),
        EXTRACTING("EXTRACTING"),
        EXTRACTED("EXTRACTED"),
        PASSWORD("PASSWORD"),
        ERROR("ERROR"),
        NOT_AVAILABLE("NOT_AVAILABLE"),
        CONVERTING("CONVERTING"),
        COMPLETED("COMPLETED"),
        UNKNOWN("");

        private String mPutKey;

        Status(String putKey) {
            mPutKey = putKey;
        }

        public static Status fromPut(String key) {
            return Arrays.stream(values())
                    .filter(type -> type.mPutKey.equals(key))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }

    private long mPutId;
    private int mPercent;
    private Status mStatus;

    public void setPutId(long putId) {
        mPutId = putId;
    }

    public void setPercent(int percent) {
        mPercent = percent;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public long getPutId() {
        return mPutId;
    }

    public int getPercent() {
        return mPercent;
    }

    public String getPercentFormatted() {
        return mPercent + "%";
    }

    public Status getStatus() {
        return mStatus;
    }

    public static Conversion fromApi(JsonObject jsonObject) {
        Conversion conversion = new Conversion();
        JsonUtil json = new JsonUtil(jsonObject);

        conversion.setPercent(json.getInt("percent_done"));
        conversion.setStatus(Status.fromPut(json.getString("status")));
        conversion.setPutId(json.getLong("id"));

        return conversion;
    }

    public static ArrayList<Conversion> fromApi(JsonArray jsonArray) {
        return StreamSupport.stream(jsonArray.spliterator(), false)
                .map(jsonElement -> fromApi(jsonElement.getAsJsonObject()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        return "Conversion{" +
                "mPutId=" + mPutId +
                ", mPercent=" + mPercent +
                ", mStatus=" + mStatus +
                '}';
    }
}
