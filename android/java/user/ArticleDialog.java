package com.name.social_helper_r_p.user;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArticleDialog extends DialogFragment {
    TextView title;
    TextView article;

    Bundle bundle;

    URLS urls = new URLS();
    Fetch fetch = new Fetch();

    SharedPreferences preferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.article, container, false);
        title = v.findViewById(R.id.title);
        article = v.findViewById(R.id.article);

        bundle = getArguments();


        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        Thread getArticle = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request(urls.URL(), urls.getArticle(), "POST", "POST");
                RequestData data = new RequestData();
                data.setData(new Data("token", preferences.getString("TOKEN", "")));
                data.setData(new Data("id", bundle.getString("id")));
                data.setData(new Data("token_ID", preferences.getString("TOKEN_ID", "")));

                Return response = fetch.fetch(request, data);
                if(getContext()!=null){
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (response.getType()){
                                case 0:

                                    JSONObject data = (JSONObject) response.getObject();
                                    System.out.println(data);
                                    try {
                                        JSONObject obj = data.getJSONObject("articles");
                                        article.setText(Html.fromHtml(obj.getString("description")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        }
                    });
                }

            }
        });

        getArticle.start();


        title.setText(bundle.getString("title"));



        return v;
    }
}
