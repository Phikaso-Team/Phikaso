package com.phikaso.app.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.phikaso.app.R;
import com.phikaso.app.model.RegisterModel;
import com.phikaso.app.model.UserModel;
import com.phikaso.app.service.ReportService;
import com.phikaso.app.util.PreferenceManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    public static final int PICK_FROM_ALBUM = 1;
    private Uri imageUri;
    private String pathUri;
    private File tempFile;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage  mStorage;

    private EditText editTextTitle;
    private EditText editTextPhone;
    private EditText editTextContent;
    private ImageView imageViewFile;
    private TextView imageViewText;

    private DatabaseReference mDBReference;
    private HashMap<String, Object> childUpdates;
    private Map<String, Object> countValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mDatabase = FirebaseDatabase.getInstance();
        mStorage  = FirebaseStorage.getInstance();

        editTextTitle = findViewById(R.id.report_edit_title);
        editTextPhone = findViewById(R.id.report_edit_phone);
        editTextContent = findViewById(R.id.report_edit_content);
        imageViewFile = findViewById(R.id.report_img_file);
        imageViewText = findViewById(R.id.report_text_file);

        count(); // 전체 피해 사례 초기화

        imageViewFile.setOnClickListener(this);
        findViewById(R.id.report_btn_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_img_file:
                Intent intent_album = new Intent(Intent.ACTION_PICK);
                intent_album.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent_album, PICK_FROM_ALBUM);
                break;
            case R.id.report_btn_submit:
                postPhishingCaseToDB();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {
            imageUri = data.getData();
            pathUri = getPath(data.getData());
            Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + imageUri);
            imageViewFile.setImageURI(imageUri);
            imageViewText.setVisibility(View.INVISIBLE);
        }
    }

    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        return cursor.getString(index);
    }

    // 피싱 신고 등록을 할 때 사용자 정보가 필요한가?
    // 일단은 피싱 내용(Text)만 저장하도록 할게요  (postPhishingCaseToDB)
    private void register() {
        String title = editTextTitle.getText().toString();
        String phone = editTextPhone.getText().toString();
        String content = editTextContent.getText().toString();

        final String uid = PreferenceManager.getString(this, "personal-id");
        final Uri file = Uri.fromFile(new File(pathUri));

        StorageReference storageReference = mStorage.getReference().child("imageFile").child("uid/" + file.getLastPathSegment());
        storageReference.putFile(imageUri).addOnCompleteListener(task -> {
            final Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
            while (!imageUrl.isComplete());

            mDatabase.getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        RegisterModel registerModel = new RegisterModel();

                        registerModel.title = title;
                        registerModel.phone = phone;
                        registerModel.email = userModel.email;
                        registerModel.content = content;
                        registerModel.file = imageUrl.getResult().toString();

                        mDatabase.getReference().child("phishingCases").child("content").push().setValue(registerModel);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
        });
    }

    // 피싱 신고 등록 -> DB에 저장 (피싱 내용 텍스트만 저장)
    private void postPhishingCaseToDB() {
        String phishingText = editTextContent.getText().toString();
        ReportService reportService = new ReportService();
        reportService.reportPhishing(phishingText);
        Toast.makeText(ReportActivity.this, "피싱 메시지를 신고해주셔서 감사합니다!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void count() {
        mDBReference = mDatabase.getReference().child("phishingCases");
        mDBReference.child("count")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {

                    } catch (Exception e) {
                        RegisterModel registerModel = new RegisterModel();
                        childUpdates = new HashMap<>();
                        countValue = registerModel.toMap();
                        childUpdates.put("count", countValue);
                        mDBReference.updateChildren(childUpdates);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
    }

    private void updateCount() {
        Map<String, Object> childUpdates1 = new HashMap<>();
        childUpdates1.put("count/", ServerValue.increment(1));
        mDBReference.updateChildren(childUpdates1);
    }
}
