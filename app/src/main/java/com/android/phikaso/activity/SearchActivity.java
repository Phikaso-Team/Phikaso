package com.android.phikaso.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.phikaso.R;
import com.android.phikaso.RequestHttpConnection;
import com.android.phikaso.util.StringUtil;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SearchActivity";

    private EditText editPhoneNumber;
    private TextView textSearchResultPolice;
    private TextView textSearchResultMoya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findViewById(R.id.search_btn_query).setOnClickListener(this);

        editPhoneNumber  = (EditText) findViewById(R.id.search_edit_phone);
        textSearchResultPolice = (TextView) findViewById(R.id.search_text_result_police);
        textSearchResultMoya = (TextView) findViewById(R.id.search_text_result_moya);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.search_btn_query) {
            String num = editPhoneNumber.getText().toString();
            searchInThePolice(num);
            searchInTheMoya(num);
        }
    }

    // 경찰청 피싱 번호 검색 결과
    private void searchResultCallback(final String result) {
        if (result == null) {
            textSearchResultPolice.setText("조회에 실패하였습니다.");
        } else {
            if (result.contains("접수된 민원이 없습니다")) {
                textSearchResultPolice.setTextColor(Color.BLACK);
            } else {
                textSearchResultPolice.setTextColor(Color.RED);
            }
            textSearchResultPolice.setText(result);
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

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            textSearchResultMoya.setText(s);
        }
    }

    // 뭐야이번호 피싱 번호 검색
    private void searchInTheMoya(final String query) {
        // URL 설정.
        String url = "http://www.moyaweb.com/search_result.do";
        ContentValues values = new ContentValues();
        values.put("SCH_TEL_NO", query);
        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, values);
        networkTask.execute();
    }
}
