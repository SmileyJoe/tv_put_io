package io.smileyjoe.putio.tv.util;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonUtil {

//    public interface SetListener<T>{
//        void set(T object);
//    }

    private JsonObject mJsonObject;

    public JsonUtil(JsonObject jsonObject) {
        mJsonObject = jsonObject;
    }

//    public boolean setIfValid(SetListener<String> setListener, String ...names){
//        for(String name : names) {
//            if (isValid(name)) {
//                setListener.set(getString(name));
//                return true;
//            }
//        }
//
//        return false;
//    }

//    public boolean setIfNotEmpty(SetListener<String> setListener, String ...names){
//        for(String name : names) {
//            String value = getString(name);
//            if (!TextUtils.isEmpty(value)) {
//                setListener.set(value);
//                return true;
//            }
//        }
//
//        return false;
//    }

    public String getStringNotEmpty(String... names) {
        for (String name : names) {
            String string = getString(name);
            if (!TextUtils.isEmpty(string)) {
                return string;
            }
        }

        return null;
    }

    public String getString(String name) {
        if (isValid(name)) {
            try {
                return mJsonObject.get(name).getAsString();
            } catch (UnsupportedOperationException e) {

            }
        }

        return null;
    }

    public long getLong(String name) {
        if (isValid(name)) {
            try {
                return mJsonObject.get(name).getAsLong();
            } catch (UnsupportedOperationException e) {

            }
        }

        return -1;
    }

    public int getInt(String name) {
        if (isValid(name)) {
            try {
                return mJsonObject.get(name).getAsInt();
            } catch (UnsupportedOperationException e) {

            }
        }

        return -1;
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        if (isValid(name)) {
            try {
                return mJsonObject.get(name).getAsBoolean();
            } catch (UnsupportedOperationException e) {

            }
        }

        return defaultValue;
    }

    public JsonArray getJsonArray(String name) {
        if (isValid(name)) {
            return mJsonObject.get(name).getAsJsonArray();
        }

        return null;
    }

//    public Integer[] getIntArray(String name){
//        if(isValid(name)){
//            try{
//                return mJsonObject.get(name).getAs
//            } catch (UnsupportedOperationException e){
//
//            }
//        }
//    }

    public boolean isValid(String name) {
        return mJsonObject != null && mJsonObject.has(name);
    }
}
