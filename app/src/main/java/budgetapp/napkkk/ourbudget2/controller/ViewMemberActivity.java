package budgetapp.napkkk.ourbudget2.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ViewMemberActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    SharedPreferences sp;
    List<UserDao> user;
    ListView listView;
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewmember_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("สมาชิก");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initFirebase();
        initInstance();
        getQuery();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_member, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_member:
                goAddmember();
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
        Toast.makeText(ViewMemberActivity.this, catchID(), Toast.LENGTH_SHORT).show();
        Query query = databaseReference.child("Group_List").child(catchID()).child("inmember");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    user.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserDao userDao = postSnapshot.getValue(UserDao.class);
                        user.add(userDao);
                    }
                    adapter = new UserAdapter(user);
                    listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                finish();
            }
        });
    }


    private void initInstance(){
        user = new ArrayList<>();
        View content = findViewById(R.id.member_content);

        listView = content.findViewById(R.id.member_listview);
    }

    private void goAddmember(){
        Intent intent = new Intent(ViewMemberActivity.this, AddMemberActivity.class);
        intent.putExtra("groupID", catchID());
        startActivity(intent);
    }




}
