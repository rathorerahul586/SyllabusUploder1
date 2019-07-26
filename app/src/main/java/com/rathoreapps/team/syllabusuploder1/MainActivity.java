package com.rathoreapps.team.syllabusuploder1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private AutoCompleteTextView subjectET;
    private EditText branchNameET, noOfSemesterET, chapterNameET, contentET, unitNoET;
    private Button saveBtn, addBranch;
    private String branchName, noOfSemester, subjectName, chapterName,content,unitNo;
    private Spinner branchListSpinner, semesterListSpinner;
    private String[] branchListForSpinner, semesterListForSpinner;
    private ArrayAdapter<String> branchSpinnerAdapter, semesterListAdepter;
    private ProgressBar branchSpinnerProgressBar, semesterListSpinnerProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setSaveBtn();
        setAddBranch();
        readBranchList();
    }

    private void initViews(){
        branchNameET = findViewById(R.id.branchNameID);
        subjectET = findViewById(R.id.subjectID);
        chapterNameET = findViewById(R.id.chapterID);
        contentET = findViewById(R.id.contentID);
        unitNoET = findViewById(R.id.unitNo);
        saveBtn = findViewById(R.id.saveBtnID);
        addBranch = findViewById(R.id.add_branch);
        branchListSpinner = findViewById(R.id.branch_spinner);
        semesterListSpinner = findViewById(R.id.semester_list_spinner);
        branchSpinnerProgressBar = findViewById(R.id.branch_spinner_progressBar);
        semesterListSpinnerProgressBar = findViewById(R.id.semester_list_spinner_progressBar);
        noOfSemesterET = findViewById(R.id.no_of_semester);
    }

    private void setSaveBtn(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                branchName = branchListSpinner.getSelectedItem().toString();
                subjectName = subjectET.getText().toString().trim();
                chapterName = chapterNameET.getText().toString().trim();
                content = contentET.getText().toString().trim();
                unitNo = unitNoET.getText().toString().trim();
                writeToDatabase(branchName,"Semester 1", subjectName,unitNo,chapterName);
                branchNameET.getText().clear();
                subjectET.getText().clear();
                chapterNameET.getText().clear();
                contentET.getText().clear();
                unitNoET.getText().clear();
            }
        });
    }

    private void setAddBranch (){
        addBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (branchNameET.getVisibility()==View.GONE) {
                    branchNameET.setVisibility(View.VISIBLE);
                    noOfSemesterET.setVisibility(View.VISIBLE);
                }else{
                    branchName = branchNameET.getText().toString().trim();
                    noOfSemester = noOfSemesterET.getText().toString().trim();
                    if (branchName.isEmpty() || noOfSemester.isEmpty()){
                        Toast.makeText(MainActivity.this, "Branch Name or Number of Semester to likh le", Toast.LENGTH_SHORT).show();
                    }else if (branchName.contains(".")||
                            branchName.contains("$")||
                            branchName.contains("[")||
                            branchName.contains("]")||
                            branchName.contains("#")||
                            branchName.contains("/")){
                        Toast.makeText(MainActivity.this, "Branch Name Must Not Contain any of \n . , # , [ , ] , # , / ", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        int getNoOfSemester = Integer.valueOf(noOfSemester);
                        addNewBranchToDatabase(branchName, getNoOfSemester);
                        Toast.makeText(MainActivity.this, branchName + " added to Database", Toast.LENGTH_SHORT).show();
                        branchNameET.getText().clear();
                        branchNameET.setVisibility(View.GONE);
                        noOfSemesterET.getText().clear();
                        noOfSemesterET.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
    private void writeToDatabase(String branch, String semester, String subject, String unit, String chapterName) {
        database = FirebaseDatabase.getInstance();
        myRef = (DatabaseReference) database.getReference().child("Branches").child(branch).child(semester).child(subject).child("Unit: "+unit+" "+chapterName);
        myRef.setValue(content);
    }

    private void addNewBranchToDatabase(String branch, int noOfSemester){
        database = FirebaseDatabase.getInstance();
        myRef = (DatabaseReference)database.getReference().child("Branches").child(branch);
        for (int i = 1; i<= noOfSemester; i++){
            myRef.child("Semester "+i).setValue("Semester "+i);
        }
    }

    private void readBranchList(){
        branchSpinnerProgressBar.setVisibility(View.VISIBLE);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Branches");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                int childCount = (int) dataSnapshot.getChildrenCount();
                Log.d("", "onDataChange: "+ childCount);
                branchListForSpinner = new String[childCount];
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String name = ds.getKey();
                    branchListForSpinner[i] = name;
                    i++;
                }

                branchSpinnerAdapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item, branchListForSpinner);
                branchSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                branchListSpinner.setAdapter(branchSpinnerAdapter);

                branchListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedBranch = adapterView.getItemAtPosition(i).toString();
                        Log.d("", "onItemSelected: "+selectedBranch);
                        readSemesterList(selectedBranch);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                branchListSpinner.setVisibility(View.VISIBLE);
                branchSpinnerProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Read Cancelled", "Failed to read value.", error.toException());
            }
        });
    }

    private void readSemesterList(String branchName){
        semesterListSpinnerProgressBar.setVisibility(View.VISIBLE);
        database = database.getInstance();
        myRef = database.getReference().child("Branches").child(branchName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                int semCount = (int) dataSnapshot.getChildrenCount();
                semesterListForSpinner = new String[semCount];
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String name = ds.getKey();
                    semesterListForSpinner[i] = name;
                    i++;
                }
                semesterListAdepter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_dropdown_item, semesterListForSpinner);
                semesterListAdepter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                semesterListSpinner.setAdapter(semesterListAdepter);
                semesterListSpinner.setVisibility(View.VISIBLE);
                semesterListSpinnerProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
