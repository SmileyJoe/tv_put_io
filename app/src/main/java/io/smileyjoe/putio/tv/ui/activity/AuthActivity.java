package io.smileyjoe.putio.tv.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;

import io.smileyjoe.putio.tv.R;
import io.smileyjoe.putio.tv.network.Putio;
import io.smileyjoe.putio.tv.network.Response;
import io.smileyjoe.putio.tv.util.JsonUtil;

public class AuthActivity extends Activity {

    private TextView mTextCode;
    private ProgressBar mProgressCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mTextCode = findViewById(R.id.text_code);
        mProgressCode = findViewById(R.id.progress_code);
        populateInstructions();

        Putio.getAuthCode(getBaseContext(), new OnCodeResponse());
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

    private void populateCode(String code){
        mProgressCode.setVisibility(View.GONE);
        mTextCode.setText(code);
        mTextCode.setVisibility(View.VISIBLE);
    }

    private class OnCodeResponse extends Response{
        @Override
        public void onSuccess(JsonObject result) {
            JsonUtil json = new JsonUtil(result);
            String code = json.getString("code");
            populateCode(code);
        }
    }
}
