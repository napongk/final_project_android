package budgetapp.napkkk.ourbudget2.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import budgetapp.napkkk.ourbudget2.R;
import budgetapp.napkkk.ourbudget2.model.GroupDao;
import budgetapp.napkkk.ourbudget2.model.UserDao;

public class AddGroupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Calendar now;
    TextView datetext;
    Button datepickfrom;
    EditText groupName, startMoney, targetMoney, descritpion_text;
    CheckBox target, time;
    String typeChosen, targetStat, timeStat, textDate;
    List group;
    DatabaseReference databaseReference;
    SharedPreferences sp;
    Spinner trans_spinner;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        initInstances();
        initFirebase();

        getSupportActionBar().setTitle("เพิ่มกลุ่ม");
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        datepickfrom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddGroupActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    //////////// initailize ////////////////////

    private void initInstances() {
        sp = getSharedPreferences("FB_PROFILE", Context.MODE_PRIVATE);

        trans_spinner = findViewById(R.id.trans_spinner);

        ArrayList<String> spinneritem = new ArrayList<>();
        spinneritem.add("รายรับ");
        spinneritem.add("รายจ่าย");
        spinneritem.add("รายรับ & รายจ่าย");

        ArrayAdapter<String> adapter_trans = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, spinneritem);
        trans_spinner.setAdapter(adapter_trans);

        datetext = findViewById(R.id.textDate);

        groupName = findViewById(R.id.groupName);
        startMoney = findViewById(R.id.startmoney_amount);
        targetMoney = findViewById(R.id.targetmoney_amount);
        targetMoney.setEnabled(false);
        descritpion_text = findViewById(R.id.description_text);

        datepickfrom = findViewById(R.id.pickdate);
        datepickfrom.setEnabled(false);

        target = findViewById(R.id.targetamount);

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!target.isChecked()) {
                    targetMoney.setEnabled(false);
                } else {
                    targetMoney.setEnabled(true);
                }
            }
        });

        time = findViewById(R.id.timetarget);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (time.isChecked()) {
                    datepickfrom.setEnabled(true);
                } else {
                    datepickfrom.setEnabled(false);
                }
            }
        });


        group = new ArrayList<>();

    }


    private void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    ///////////////////////////////////////////////////////////////////////


    /////////////////////// Firebase query /ADD //////////////////////////
    private void addGroup() {
        String name = groupName.getText().toString();
        checkCheckBox();

        //checking if the value is provided
        if (!TextUtils.isEmpty(name)) {
            String id = databaseReference.child("Group_List").push().getKey();

            GroupDao groupDB = new GroupDao();
            groupDB.setGroupid(id);
            groupDB.setName(name);
            groupDB.setMoney(startMoney.getText().toString());
            groupDB.setTarget(targetStat);
            groupDB.setTime(timeStat);
            groupDB.setOwner(sp.getString("name", "null"));
            groupDB.setType(typeChosen);
            groupDB.setDescription(descritpion_text.getText().toString());

            UserDao userDao = new UserDao();
            userDao.setUserName(sp.getString("name", "null"));
            userDao.setUserPic(sp.getString("imageid", "null"));


            databaseReference.child("Group_List").child(id).setValue(groupDB);
            databaseReference.child("Group_List").child(id).child("inmember").child(sp.getString("name", "null")).setValue(userDao);
            databaseReference.child("User").child(sp.getString("name", "null")).child("own").child(id).setValue(name);
            databaseReference.child("User").child(sp.getString("name", "null")).child("inmember").child(id).setValue(name);

            Toast.makeText(this, "Group Added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        now.set(year, monthOfYear, dayOfMonth);
        Date date = now.getTime();
        textDate = dateFormat.format(date);

        datetext.setText(textDate);
    }


    public void pushdatabase(View view) {
        addGroup();
        finish();
    }

    ///////////////////////////////////////////////////////////////////


    public void checkCheckBox() {
        if (trans_spinner.getSelectedItem().toString().equals("รายรับ & รายจ่าย")) {
            typeChosen = "both";
        } else if (trans_spinner.getSelectedItem().toString().equals("รายจ่าย")) {
            typeChosen = "expense";
        } else if (trans_spinner.getSelectedItem().toString().equals("รายรับ")) {
            typeChosen = "income";
        }
        if (target.isChecked()) {
            targetStat = targetMoney.getText().toString();
        } else {
            targetStat = "OFF";
        }
        if (time.isChecked()) {
            timeStat = textDate;
        } else {
            timeStat = "OFF";
        }

    }
}
