package com.rathoreapps.team.syllabusuploder1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference myRef;
    private FirebaseDatabase database;
    AutoCompleteTextView branchNameET, subjectET;
    EditText chapterNameET, contentET, unitNoET;
    Button saveBtn;
    String branchName, subject, chapterName,content,unitNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setSaveBtn();
    }

    private void initViews(){
        branchNameET = findViewById(R.id.branchNameID);
        subjectET = findViewById(R.id.subjectID);
        chapterNameET = findViewById(R.id.chapterID);
        contentET = findViewById(R.id.contentID);
        unitNoET = findViewById(R.id.unitNo);
        saveBtn = findViewById(R.id.saveBtnID);
    }

    private void setSaveBtn(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                branchName = branchNameET.getText().toString();
                subject = subjectET.getText().toString();
                chapterName = chapterNameET.getText().toString();
                content = contentET.getText().toString();
                unitNo = unitNoET.getText().toString();
                writeToDatabase();
                readFromDatabase();
            }
        });
    }
    private void writeToDatabase() {
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = (DatabaseReference) database.getReference().child("Branches").child(branchName).child("Semester 1").child(subject).child("Unit: "+unitNo+" "+chapterName);
        myRef.setValue(content);
    }

    private void readFromDatabase(){
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("read Value", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Read Cancelled", "Failed to read value.", error.toException());
            }
        });
    }
}
