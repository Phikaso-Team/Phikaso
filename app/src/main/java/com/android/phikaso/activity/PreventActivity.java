package com.android.phikaso.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.phikaso.R;
import com.android.phikaso.util.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PreventActivity extends AppCompatActivity {
    private TextView personalName;
    private TextView personalCount;
    private TextView preventCountAll;
    private DatabaseReference mDBReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prevent);

        personalName = findViewById(R.id.personalName);
        personalCount = findViewById(R.id.personalCount);
        preventCountAll = findViewById(R.id.preventCountAll);

        personalName.setText(PreferenceManager.getString(this, "personal-name"));
        personalCount();
        preventCountAll();
    }

    private void personalCount(){
        String uid = PreferenceManager.getString(this, "personal-id");
        mDBReference = FirebaseDatabase.getInstance().getReference().child("users");
        mDBReference.child(uid)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer count = snapshot.child("count").getValue(Integer.class);
                    if(count != null){
                        personalCount.setText(String.valueOf(count));
                    }else{
                        personalCount.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
    }

    private void preventCountAll(){
        mDBReference = FirebaseDatabase.getInstance().getReference();
        mDBReference.child("total")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer count = snapshot.child("count").getValue(Integer.class);
                    if(count != null){
                        preventCountAll.setText(String.valueOf(count));
                    }else{
                        preventCountAll.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
    }
}
