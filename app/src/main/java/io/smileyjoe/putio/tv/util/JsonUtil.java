package io.smileyjoe.putio.tv.util;

import com.google.gson.JsonObject;

public class JsonUtil {

    private JsonObject mJsonObject;

    public JsonUtil(JsonObject jsonObject) {
        mJsonObject = jsonObject;
    }

    public String getString(String name){
        if(isValid(name)){
            return mJsonObject.get(name).getAsString();
        }

        return null;
    }

    public long getLong(String name){
        if(isValid(name)){
            return mJsonObject.get(name).getAsLong();
        }

        return -1;
    }

    public int getInt(String name){
        if(isValid(name)){
            return mJsonObject.get(name).getAsInt();
        }

        return -1;
    }

    public boolean getBoolean(String name, boolean defaultValue){
        if(isValid(name)){
            return mJsonObject.get(name).getAsBoolean();
        }

        return defaultValue;
    }

    private boolean isValid(String name){
        return mJsonObject != null && mJsonObject.has(name);
    }
}
