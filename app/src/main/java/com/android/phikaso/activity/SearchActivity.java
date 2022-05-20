package com.android.phikaso.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.phikaso.R;
import com.android.phikaso.util.StringUtil;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SearchActivity";

    private EditText editPhoneNumber;
    private TextView textSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findViewById(R.id.search_btn_query).setOnClickListener(this);

        editPhoneNumber  = (EditText) findViewById(R.id.search_edit_phone);
        textSearchResult = (TextView) findViewById(R.id.search_text_result);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.search_btn_query) {
            String num = editPhoneNumber.getText().toString();
            searchInThePolice(num);
        }
    }

    // 경찰청 피싱 번호 검색 결과
    private void searchResultCallback(final String result) {
        if (result == null) {
            textSearchResult.setText("조회에 실패하였습니다.");
        } else {
            if (result.contains("접수된 민원이 없습니다")) {
                textSearchResult.setTextColor(Color.BLACK);
            } else {
                textSearchResult.setTextColor(Color.RED);
            }
            textSearchResult.setText(result);
        }
    }

    // 경찰청 피싱 번호 검색
    private void searchInThePolice(final String query) {
        final String ts  = Long.toString(System.currentTimeMillis());
        final String cb  = "jQuery" + (ts+ts).substring(0, 21) + "_" + ts;
        final String url = "https://net-durumi.cyber.go.kr/countFraud.do?callback=" + cb + "&fieldType=H&keyword=" + query + "&accessType=3&_=" + ts;

        final Request.Builder builder = new Request.Builder().url(url).get();
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36");
        builder.addHeader("Referer", "https://cyberbureau.police.go.kr/");
        builder.addHeader("Accept", "*/*");
        final Request request = builder.build();

        new Thread(() -> { // 인터넷 요청은 새 스레드에서 수행해야 함
            final OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    searchResultCallback(null);
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        searchResultCallback(null);
                        return;
                    }
                    String data = response.body().string();
                    if (!data.contains("\"success\":true")) {
                        searchResultCallback(null);
                        return;
                    }
                    data = data.substring(cb.length() + "({\"message\":\"".length());
                    data = data.substring(0, data.length() - "\",\"success\":true})".length());
                    final String result = StringUtil.removeHtmlTags(StringUtil.unescapeUnicode(data));
                    runOnUiThread(() -> {
                        searchResultCallback(result);
                    });
                }
            });
        }).start();
    }
}
