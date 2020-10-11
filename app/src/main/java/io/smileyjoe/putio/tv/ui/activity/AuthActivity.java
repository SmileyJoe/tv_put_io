package io.smileyjoe.putio.tv.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;

import io.smileyjoe.putio.tv.Application;
import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.util.JsonUtil;
import io.smileyjoe.putio.tv.util.SharedPrefs;

public class AuthActivity extends Activity {

    private TextView mTextCode;
    private ProgressBar mProgressCode;
    private SharedPrefs mPrefs;
    private String mCode;
    private boolean mIsPaused = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!TextUtils.isEmpty(Application.getPutToken())){
            authComplete();
            return;
        }

        setContentView(R.layout.activity_auth);
        mPrefs = SharedPrefs.getInstance(getBaseContext());

        mTextCode = findViewById(R.id.text_code);
        mProgressCode = findViewById(R.id.progress_code);
        populateInstructions();

        Putio.getAuthCode(getBaseContext(), new OnCodeResponse());
    }

    private void authComplete(){
        startActivity(MainActivity.getIntent(getBaseContext()));
        finish();
    }

    private void populateInstructions(){
        String instructions = getString(R.string.text_auth_instructions);
        String url = getString(R.string.text_auth_link_url);

        int urlStart = instructions.indexOf("{url}");
        int urlLength = url.length();

        Spannable spanInstructions = new SpannableString(instructions.replace("{url}", url));
        spanInstructions.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getBaseContext(), R.color.color_primary)), urlStart, urlStart + urlLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) findViewById(R.id.text_instructions)).setText(spanInstructions);
    }

    private void populateCode(){
        mProgressCode.setVisibility(View.GONE);
        mTextCode.setText(mCode);
        mTextCode.setVisibility(View.VISIBLE);
    }

    private void getToken(){
        if(!mIsPaused) {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Putio.getAuthToken(getBaseContext(), mCode, new OnTokenResponse());
                }
            }, 5000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsPaused = false;

        if(!TextUtils.isEmpty(mCode)){
            getToken();
        }
    }

    private class OnTokenResponse extends Response{
        @Override
        public void onSuccess(JsonObject result) {
            JsonUtil json = new JsonUtil(result);
            String token = json.getString("oauth_token");

            if(TextUtils.isEmpty(token)){
                getToken();
            } else {
                mPrefs.savePutToken(token);
                ((Application) getApplication()).setPutToken(token);
                authComplete();
            }
        }
    }

    private class OnCodeResponse extends Response{
        @Override
        public void onSuccess(JsonObject result) {
            JsonUtil json = new JsonUtil(result);
            mCode = json.getString("code");
            populateCode();
            getToken();
        }
    }
}