package io.smileyjoe.putio.tv.ui.fragment.settings;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import com.prof.rssparser.Article;
import com.prof.rssparser.Channel;
import com.prof.rssparser.OnTaskCompleted;
import com.prof.rssparser.Parser;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.putio.tv.BuildConfig;
import io.smileyjoe.putio.tv.R;

public class SettingsStatusFragment extends SettingsBaseFragment{

    private static final String RSS_FEED = "https://hf2g47jkpb2s.statuspage.io/history.rss";

    public static GuidedAction getAction(Context context, int id) {
        return new GuidedAction.Builder(context)
                .id(id)
                .title(R.string.settings_title_status)
                .description(R.string.settings_description_status)
                .build();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parser parser = new Parser.Builder()
                .charset(Charset.forName("ISO-8859-7"))
                .build();
        parser.onFinish(new OnTaskCompleted() {
            //what to do when the parsing is done
            @Override
            public void onTaskCompleted(Channel channel) {
                List<GuidedAction> actions = new ArrayList<>();
                channel.getArticles().forEach(article -> {
                    Spanned description = Html.fromHtml("<b>" + article.getTitle() + "</b>" +
                            "<br/>" +
                            article.getDescription(), FROM_HTML_MODE_LEGACY);

                    GuidedAction.Builder builder = new GuidedAction.Builder(getContext())
                            .title(article.getPubDate())
                            .description(description)
                            .focusable(true)
                            .editable(false)
                            .infoOnly(true)
                            .multilineDescription(true);
                    actions.add(builder.build());
                });
                setActions(actions);
            }

            //what to do in case of error
            @Override
            public void onError(Exception e) {
                // Handle the exception
            }
        });
        parser.execute(RSS_FEED);
    }

    @Override
    @NonNull
    public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
        return getGuidance(R.string.settings_title_status, R.string.settings_description_status);
    }

}
