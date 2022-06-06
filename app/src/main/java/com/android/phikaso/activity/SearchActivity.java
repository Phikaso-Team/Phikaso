package com.android.phikaso.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.phikaso.R;
import com.android.phikaso.util.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SearchActivity";

    private EditText editPhoneNumber;
    private TextView textSearchPolice;
    private TextView textSearchMoya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_search);
        setContentView(R.layout.activity_search2);

        findViewById(R.id.search_btn_query).setOnClickListener(this);

        editPhoneNumber  = (EditText) findViewById(R.id.search_edit_phone);
        textSearchPolice = (TextView) findViewById(R.id.search_text_result_police);
        textSearchMoya   = (TextView) findViewById(R.id.search_text_result_moya);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.search_btn_query) {
            String num = editPhoneNumber.getText().toString();
            searchNumberPolice(num);
            searchNumberMoya(num);
        }
    }

    // 경찰청 피싱 번호 검색
    private void searchNumberPolice(final String query) {
        final String ts  = Long.toString(System.currentTimeMillis());
        final String cb  = "jQuery" + (ts+ts).substring(0, 21) + "_" + ts;
        final String url = "https://net-durumi.cyber.go.kr/countFraud.do?callback=" + cb + "&fieldType=H&keyword=" + query + "&accessType=3&_=" + ts;

        final Request.Builder builder = new Request.Builder().url(url).get();
        builder.addHeader("User-Agent", getString(R.string.user_agent));
        builder.addHeader("Referer", "https://cyberbureau.police.go.kr/");
        final Request request = builder.build();

        new Thread(() -> { // 인터넷 요청은 새 스레드에서 수행해야 함
            final OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    searchResultPolice(null);
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                    if (!response.isSuccessful() || response.body() == null) {
                        searchResultPolice(null);
                        return;
                    }
                    String data = response.body().string();
                    if (!data.contains("\"success\":true")) {
                        searchResultPolice(null);
                        return;
                    }
                    data = data.substring(cb.length() + "({\"message\":\"".length());
                    data = data.substring(0, data.length() - "\",\"success\":true})".length());
                    final String result = StringUtil.removeHtmlTags(StringUtil.unescapeUnicode(data));
                    runOnUiThread(() -> {
                        searchResultPolice(result);
                    });
                }
            });
        }).start();
    }

    // 경찰청 피싱 번호 검색 결과
    private void searchResultPolice(final String result) {
        if (result == null) {
            textSearchPolice.setText("조회에 실패하였습니다.");
        } else {
            if (result.contains("접수된 민원이 없습니다")) {
                textSearchPolice.setTextColor(Color.BLACK);
            } else {
                textSearchPolice.setTextColor(Color.RED);
            }
            textSearchPolice.setText(result);
        }
    }

    // 뭐야이번호 피싱 번호 검색
    private void searchNumberMoya(final String query) {
        final String      url  = "http://www.moyaweb.com/search_result.do";
        final RequestBody body = new FormBody.Builder().add("SCH_TEL_NO", query).build();

        final Request.Builder builder = new Request.Builder().url(url).post(body);
        builder.addHeader("User-Agent", getString(R.string.user_agent));
        builder.addHeader("Referer", "http://www.moyaweb.com/main.do");
        final Request request = builder.build();

        new Thread(() -> {
            final OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    searchResultMoya(null);
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                    if (!response.isSuccessful() || response.body() == null) {
                        searchResultMoya(null);
                        return;
                    }
                    String data = response.body().string();
                    Document doc = Jsoup.parse(data);
                    Elements content_td = doc.getElementsByClass("content_td");
                    String result = content_td.text();
                    runOnUiThread(() -> {
                        searchResultMoya(result);
                    });
                }
            });
        }).start();
    }

    private void searchResultMoya(final String result) {
        if (result == null) {
            textSearchMoya.setText("");
        } else {
            textSearchMoya.setText(result);
        }
    }
}
