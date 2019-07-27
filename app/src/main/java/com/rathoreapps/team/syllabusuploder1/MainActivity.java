package com.rathoreapps.team.syllabusuploder1;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private AutoCompleteTextView subjectET;
    private EditText branchNameET, noOfSemesterET, chapterNameET, contentET, unitNoET;
    private Button addBranchBtn, addSubjectBtn, addSyllabusBtn, hideBranch, hideSubject, hideSyllabus;
    private String branchName, noOfSemester, subjectName, chapterName, content, unitNo;
    private Spinner branchListSpinner, semesterListSpinner, subjectListSpinner;
    private String[] branchListForSpinner, semesterListForSpinner, subjectListForSpinner;
    private ArrayAdapter<String> branchSpinnerAdapter, semesterListAdepter, subjectListAdapter;
    private ProgressBar branchSpinnerProgressBar, semesterListSpinnerProgressBar, subjectListSpinnerProgressBar;
    private RelativeLayout branchSpinnerLayout, semesterListSpinnerLayout, subjectListSpinnerLayout;
    private LinearLayout syllabusLayout;
    private String selectedBranch, selectedSemester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setAddBranch();
        setAddSubjectBtn();
        setAddSyllabus();
    }

    private void initViews() {
        context = getBaseContext();
        branchSpinnerLayout = findViewById(R.id.branch_spinner_layout);
        semesterListSpinnerLayout = findViewById(R.id.semester_list_spinner_layout);
        subjectListSpinnerLayout = findViewById(R.id.subject_spinner_layout);
        syllabusLayout = findViewById(R.id.syllabusLayout);
        branchNameET = findViewById(R.id.branchNameID);
        subjectET = findViewById(R.id.subjectID);
        chapterNameET = findViewById(R.id.chapterID);
        contentET = findViewById(R.id.contentID);
        unitNoET = findViewById(R.id.unitNo);
        addBranchBtn = findViewById(R.id.add_branch);
        addSubjectBtn = findViewById(R.id.add_subject);
        addSyllabusBtn = findViewById(R.id.add_syllabus);
        hideBranch = findViewById(R.id.hideBranch);
        hideSubject = findViewById(R.id.hideSubject);
        hideSyllabus = findViewById(R.id.hideSyllabus);
        branchListSpinner = findViewById(R.id.branch_spinner);
        branchListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedBranch = adapterView.getSelectedItem().toString();
                readSemesterList(selectedBranch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(context, "Please Select Any Branch Name form Dropdown Menu", Toast.LENGTH_SHORT).show();
            }
        });
        semesterListSpinner = findViewById(R.id.semester_list_spinner);
        semesterListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSemester = adapterView.getSelectedItem().toString();
                readSubjectList(selectedBranch, selectedSemester);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        subjectListSpinner = findViewById(R.id.subject_spinner);
        branchSpinnerProgressBar = findViewById(R.id.branch_spinner_progressBar);
        semesterListSpinnerProgressBar = findViewById(R.id.semester_list_spinner_progressBar);
        subjectListSpinnerProgressBar = findViewById(R.id.subject_spinner_progressBar);
        noOfSemesterET = findViewById(R.id.no_of_semester);
    }

    private void setAddBranch() {
        addBranchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (branchNameET.getVisibility() == View.GONE) {
                    branchNameET.setVisibility(View.VISIBLE);
                    noOfSemesterET.setVisibility(View.VISIBLE);
                    hideBranch.setVisibility(View.VISIBLE);
                } else {
                    branchName = branchNameET.getText().toString().trim();
                    noOfSemester = noOfSemesterET.getText().toString().trim();
                    if (branchName.isEmpty() || noOfSemester.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Branch Name and No. of semesters", Toast.LENGTH_SHORT).show();
                    } else if (branchName.contains(".") ||
                            branchName.contains("$") ||
                            branchName.contains("[") ||
                            branchName.contains("]") ||
                            branchName.contains("#") ||
                            branchName.contains("/")) {
                        Toast.makeText(MainActivity.this, "Branch Name Must Not Contain any of \n . , # , [ , ] , # , / ", Toast.LENGTH_SHORT).show();
                    } else {
                        int getNoOfSemester = Integer.valueOf(noOfSemester);
                        addNewBranchToDatabase(branchName, getNoOfSemester);
                        Toast.makeText(MainActivity.this, branchName + " added to Database", Toast.LENGTH_SHORT).show();
                        branchNameET.getText().clear();
                        branchNameET.setVisibility(View.GONE);
                        noOfSemesterET.getText().clear();
                        noOfSemesterET.setVisibility(View.GONE);
                        hideBranch.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void setAddSubjectBtn() {
        addSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (branchSpinnerLayout.getVisibility() == View.GONE) {
                    branchSpinnerLayout.setVisibility(View.VISIBLE);
                    semesterListSpinnerLayout.setVisibility(View.VISIBLE);
                    subjectET.setVisibility(View.VISIBLE);
                    hideSubject.setVisibility(View.VISIBLE);
                    readBranchList();
                } else {
                    subjectName = subjectET.getText().toString().trim();
                    if (subjectName.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Subject Name", Toast.LENGTH_SHORT).show();
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setTitle("Check Carefully")
                                .setMessage("Branch Name: " + selectedBranch + "\nSemester: " + selectedSemester + "\nSubject Name: " + subjectName);
                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addNewSubjectToDatabase(selectedBranch, selectedSemester, subjectName);
                                dialogInterface.dismiss();
                                subjectET.getText().clear();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
        });
    }

    private void setAddSyllabus() {
        addSyllabusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (syllabusLayout.getVisibility() == View.GONE) {
                    addSubjectBtn.setVisibility(View.GONE);
                    hideSyllabus.setVisibility(View.VISIBLE);
                    branchSpinnerLayout.setVisibility(View.VISIBLE);
                    semesterListSpinnerLayout.setVisibility(View.VISIBLE);
                    syllabusLayout.setVisibility(View.VISIBLE);
                    readBranchList();
                } else {
                    branchName = branchListSpinner.getSelectedItem().toString();
                    if (subjectListAdapter.getCount() == 0) {
                        Toast.makeText(context, "Their is no subject in Selected branch and semester. Please add Subject first", Toast.LENGTH_SHORT).show();
                    } else {
                        subjectName = subjectListSpinner.getSelectedItem().toString();
                        chapterName = chapterNameET.getText().toString().trim();
                        content = contentET.getText().toString().trim();
                        unitNo = unitNoET.getText().toString().trim();
                        if (subjectName.isEmpty())
                            Toast.makeText(context, "Please Select any Subject", Toast.LENGTH_SHORT).show();
                        else if (unitNo.isEmpty())
                            Toast.makeText(context, "Enter Unit No", Toast.LENGTH_SHORT).show();
                        else if (chapterName.isEmpty())
                            Toast.makeText(context, "Enter Chapter Name", Toast.LENGTH_SHORT).show();
                        else if (content.isEmpty())
                            Toast.makeText(context, "Enter Content", Toast.LENGTH_SHORT).show();
                        else if (chapterName.contains(".") ||
                                chapterName.contains("$") ||
                                chapterName.contains("[") ||
                                chapterName.contains("]") ||
                                chapterName.contains("#") ||
                                chapterName.contains("/")) {
                            Toast.makeText(MainActivity.this, "Chapter Name Must Not Contain any of \n . , # , [ , ] , # , / ", Toast.LENGTH_SHORT).show();
                        } else {
                            addNewSyllabusToDatabase(branchName, selectedSemester, subjectName, unitNo, chapterName);
                            branchNameET.getText().clear();
                            subjectET.getText().clear();
                            chapterNameET.getText().clear();
                            contentET.getText().clear();
                            unitNoET.getText().clear();
                        }
                    }

                }
            }
        });
    }

    private void addNewBranchToDatabase(String branch, int noOfSemester) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Branches").child(branch);
        for (int i = 1; i <= noOfSemester; i++) {
            myRef.child("Semester " + i).setValue("Semester " + i);
        }
    }

    private void addNewSubjectToDatabase(String branch, String semester, String subject) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Branches").child(branch).child(semester).child(subject);
        myRef.setValue(subject);
    }

    private void addNewSyllabusToDatabase(String branch, String semester, String subject, String unit, String chapterName) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Branches").child(branch).child(semester).child(subject).child("Unit: " + unit + " " + chapterName);
        myRef.setValue(content);


    }

    private void readBranchList() {
        branchSpinnerProgressBar.setVisibility(View.VISIBLE);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Branches");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                int childCount = (int) dataSnapshot.getChildrenCount();
                branchListForSpinner = new String[childCount];
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getKey();
                    branchListForSpinner[i] = name;
                    i++;
                }

                branchSpinnerAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, branchListForSpinner);
                branchSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                branchListSpinner.setAdapter(branchSpinnerAdapter);
                branchListSpinner.setVisibility(View.VISIBLE);
                branchSpinnerProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Read Cancelled", "Failed to read value.", error.toException());
            }
        });
    }

    private void readSemesterList(String branchName) {
        semesterListSpinnerProgressBar.setVisibility(View.VISIBLE);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Branches").child(branchName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                int semCount = (int) dataSnapshot.getChildrenCount();
                semesterListForSpinner = new String[semCount];
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getKey();
                    semesterListForSpinner[i] = name;
                    i++;
                }
                semesterListAdepter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, semesterListForSpinner);
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

    private void readSubjectList(String branch, String semester) {
        subjectListSpinnerLayout.setVisibility(View.VISIBLE);
        subjectListSpinnerProgressBar.setVisibility(View.VISIBLE);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("Branches").child(branch).child(semester);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                int subjectCount = (int) dataSnapshot.getChildrenCount();
                subjectListForSpinner = new String[subjectCount];
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getKey();
                    subjectListForSpinner[i] = name;
                    i++;
                }
                subjectListAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, subjectListForSpinner);
                subjectListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectListSpinner.setVisibility(View.VISIBLE);
                subjectListSpinner.setAdapter(subjectListAdapter);
                subjectListSpinnerProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setHideBranch(View view) {
        branchSpinnerLayout.setVisibility(View.GONE);
        branchNameET.getText().clear();
        branchNameET.setVisibility(View.GONE);
        noOfSemesterET.getText().clear();
        noOfSemesterET.setVisibility(View.GONE);
        hideBranch.setVisibility(View.GONE);
    }

    public void setHideSubject(View view) {
        branchSpinnerLayout.setVisibility(View.GONE);
        semesterListSpinnerLayout.setVisibility(View.GONE);
        subjectET.getText().clear();
        subjectET.setVisibility(View.GONE);
        hideSubject.setVisibility(View.GONE);
    }

    public void setHideSyllabus(View view) {
        branchSpinnerLayout.setVisibility(View.GONE);
        semesterListSpinnerLayout.setVisibility(View.GONE);
        hideSyllabus.setVisibility(View.GONE);
        syllabusLayout.setVisibility(View.GONE);
        addSubjectBtn.setVisibility(View.VISIBLE);
    }
}
