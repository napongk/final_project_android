package budgetapp.napkkk.ourbudget2.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

    Button addtoggleBtn, addapplyBtn, cancelBtn;
    DatabaseReference databaseReference;
    SharedPreferences sp;
    List<UserDao> user;
    ListView listView;
    UserAdapter adapter;
    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        getSupportActionBar().setTitle("เพิ่มสมาชิก");
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initFirebase();
        initInstance();
        personQuery();
        getGroupName();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = databaseReference.child("Group_List").push().getKey();
                UserDao dao = user.get(i);
                addMemberDialog(dao, id);
                Toast.makeText(AddMemberActivity.this, user.get(i).getUserName(), Toast.LENGTH_SHORT).show();
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

        return (super.onOptionsItemSelected(item));
    }

    private void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    private void personQuery() {
        Query query = databaseReference.child("User");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserDao userDao = postSnapshot.getValue(UserDao.class);
                    if (!userDao.getInmember().containsKey(catchID())) {
                        user.add(userDao);
                    }
                }
                adapter = new UserAdapter(user);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String catchID() {
        Bundle bundle = getIntent().getExtras();
        return bundle.getString("groupID");
    }

    private void initInstance() {

        addtoggleBtn = findViewById(R.id.addmember_button);
        sp = getSharedPreferences("FB_PROFILE", Context.MODE_PRIVATE);

        user = new ArrayList<>();
        listView = findViewById(R.id.addmember_listview);
    }

    private void addMemberDialog(final UserDao dao, final String id) {
        final String groupid = catchID();
        final String groupname = groupName;

        Toast.makeText(AddMemberActivity.this, "groupid " + groupid, Toast.LENGTH_SHORT).show();
        Toast.makeText(AddMemberActivity.this, "groupname " + groupname, Toast.LENGTH_SHORT).show();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("เพิ่มสมาชิก");
        builder.setMessage(Html.fromHtml("ต้องการจะเพิ่ม " + "<b>" + dao.getUserName() + "</b>" + " เข้ากลุ่มมั้ย ?"));

        builder.setPositiveButton("เพิ่ม", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("Group_List").child(groupid).child("inmember").child(dao.getUserName()).child("userName").setValue(dao.getUserName());
                databaseReference.child("Group_List").child(groupid).child("inmember").child(dao.getUserName()).child("userPic").setValue(dao.getUserPic());
                databaseReference.child("User").child(dao.getUserName()).child("inmember").child(groupid).setValue(groupname);
                Toast.makeText(AddMemberActivity.this, "เพิ่ม " + dao.getUserName() + " เข้ากลุ่มแล้ว", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create();
        builder.show();
    }

    private void getGroupName() {
        Query query = databaseReference.child("Group_List").child(catchID()).child("name");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupName = String.valueOf(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
