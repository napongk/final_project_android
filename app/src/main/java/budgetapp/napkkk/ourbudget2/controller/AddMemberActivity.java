package budgetapp.napkkk.ourbudget2.controller;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import budgetapp.napkkk.ourbudget2.R;
import budgetapp.napkkk.ourbudget2.controller.adapter.UserAdapter;
import budgetapp.napkkk.ourbudget2.model.UserDao;

public class AddMemberActivity extends AppCompatActivity {

    CheckBox addCheckBox;
    Button addtoggleBtn, addapplyBtn, cancelBtn;
    DatabaseReference databaseReference;
    SharedPreferences sp;
    List<UserDao> user;
    ListView listView;
    UserAdapter adapter;
    SparseBooleanArray sparseBooleanArray ;
    ImageView checked;

//    AdapterView<UserAdapter> adapterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        getSupportActionBar().setTitle("เพิ่มสมาชิก");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initFirebase();
        initInstance();
        getQuery();


        listView.setTag(addCheckBox);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                addCheckBox = adapterView.findViewById(R.id.addCheckBox);
                sparseBooleanArray = listView.getCheckedItemPositions();
                addCheckBox = adapterView.findViewById(R.id.addCheckBox);

                addCheckBox.performClick();

                String ValueHolder = "" ;

                int j = 0 ;

                while (j < sparseBooleanArray.size()) {

                    if (sparseBooleanArray.valueAt(j)) {

                        ValueHolder += user.get(sparseBooleanArray.keyAt(j)).getUserName() + ",";
                    }

                    j++ ;
                }

                ValueHolder = ValueHolder.replaceAll("(,)*$", "");

                Toast.makeText(AddMemberActivity.this, "ListView Selected Values = " + ValueHolder, Toast.LENGTH_LONG).show();
//                if(addCheckBox.isChecked()){
//                    addCheckBox.setChecked(false);
//                    addCheckBox.setVisibility(View.GONE);
//                }
//                else {
//                    addCheckBox.setVisibility(View.VISIBLE);
//                    addCheckBox.setChecked(true);
//                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }

    private void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private String catchID() {
        Bundle bundle = getIntent().getExtras();
        return bundle.getString("groupID");
    }

    private void getQuery() {
        Query query = databaseReference.child("User");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Toast.makeText(ViewMemberActivity.this, postSnapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                    UserDao userDao = postSnapshot.getValue(UserDao.class);
                    user.add(userDao);
                }
                adapter = new UserAdapter(user);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initInstance(){

        addtoggleBtn = findViewById(R.id.addmember_button);
        addapplyBtn = findViewById(R.id.applymember_button);
        cancelBtn = findViewById(R.id.cancel_button);

        user = new ArrayList<>();
        listView = findViewById(R.id.addmember_listview);
    }

    public void addapply(View view) {
        addtoggleBtn.setVisibility(View.VISIBLE);
        addapplyBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
//        addCheckBox.setVisibility(View.VISIBLE);
    }

    public void addtoggle(View view) {
        addtoggleBtn.setVisibility(View.GONE);
        addapplyBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
//        addCheckBox.setVisibility(View.GONE);
    }

    public void cancel(View view) {
        addtoggleBtn.setVisibility(View.VISIBLE);
        addapplyBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
//        addCheckBox.setVisibility(View.GONE);
    }
}
