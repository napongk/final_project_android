package budgetapp.napkkk.ourbudget2;

import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Calendar now;
    TextView datetext;
    Button datepick;
    EditText groupName, startMoney, targetMoney;
    CheckBox income,expense,target,time;
    String typeChosen,targetStat,timeStat,textDate;
    List group;
    DatabaseReference databaseReference;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
//        getActionBar().setTitle("Add a group as Owner");
        initInstances();
        initFirebase();


        datepick.setOnClickListener(new View.OnClickListener() {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        now.set(year,monthOfYear,dayOfMonth);
        Date date = now.getTime();
        textDate = dateFormat.format(date);

        datetext.setText(textDate);
    }

    private void initInstances() {
        datetext = findViewById(R.id.textDate);

        groupName = findViewById(R.id.groupName);
        startMoney = findViewById(R.id.startmoney_amount);
        targetMoney = findViewById(R.id.targetmoney_amount);

        datepick = findViewById(R.id.pickdate);

        income = findViewById(R.id.incomeType);
        expense = findViewById(R.id.expenseType);
        target = findViewById(R.id.targetamount);
        time = findViewById(R.id.timetarget);


        group = new ArrayList<>();

    }

    public void pushdatabase(View view) {
        addGroup();
    }

    public void checkCheckBox() {
        if(income.isChecked() && expense.isChecked()){
            typeChosen = "both";
        }
        else if(expense.isChecked()){
            typeChosen = "expense";
        }
        else if(income.isChecked()){
            typeChosen = "income";
        }
        if(target.isChecked()){
            targetStat = targetMoney.getText().toString();
        }
        else{
            targetStat = "OFF";
        }
        if(time.isChecked()){
            timeStat = textDate;
        }
        else{
            timeStat = "OFF";
        }

    }

    private void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


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
            groupDB.setOwner("TEST");
            groupDB.setType(typeChosen);


            databaseReference.child("Group_List").child(id).setValue(groupDB);

            Toast.makeText(this, "Group Added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }
}
