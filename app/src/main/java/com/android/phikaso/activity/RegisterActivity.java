package com.android.phikaso.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.android.phikaso.R;
import com.android.phikaso.model.RegisterModel;
import com.android.phikaso.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    public static final int PICK_FROM_ALBUM = 1;
    private Uri imageUri;
    private String pathUri;
    private File tempFile;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextContent;
    private ImageView imageViewFile;

    private DatabaseReference mDBReference;
    private HashMap<String, Object> childUpdates;
    private Map<String, Object> countValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //초기화
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        //아이디 설정
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextContent = findViewById(R.id.editTextContent);
        imageViewFile = findViewById(R.id.imageViewFile);

        //전체 피해 사례 초기화
        count();

        imageViewFile.setOnClickListener(onClickListener);
        findViewById(R.id.buttonRegister).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageViewFile:
                    Intent intent_album = new Intent(Intent.ACTION_PICK);
                    intent_album.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent_album, PICK_FROM_ALBUM);
                    break;
                case R.id.buttonRegister:
                    register();
                    updateCount();//전체 피해 사례 증가
                    Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent_main);
                    break;
            }
        }
    };

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

    private void register() {
        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        String content = editTextContent.getText().toString();

        final String uid = mAuth.getCurrentUser().getUid();
        final Uri file = Uri.fromFile(new File(pathUri));

        StorageReference storageReference = mStorage.getReference().child("imageFile").child("uid/" + file.getLastPathSegment());
        storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                final Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                while (!imageUrl.isComplete()) ;

                mDatabase.getReference().child("users").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserModel userModel = snapshot.getValue(UserModel.class);
                                RegisterModel registerModel = new RegisterModel();

                                registerModel.name = name;
                                registerModel.phone = phone;
                                registerModel.email = userModel.email;
                                registerModel.content = content;
                                registerModel.file = imageUrl.getResult().toString();

                                mDatabase.getReference().child("phishingCases").child("content").push().setValue(registerModel);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
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
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateCount() {
        Map<String, Object> childUpdates1 = new HashMap<>();
        childUpdates1.put("count/", ServerValue.increment(1));
        mDBReference.updateChildren(childUpdates1);
    }
}
