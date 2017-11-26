package budgetapp.napkkk.ourbudget2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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

public class OnGroupActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    List<GroupDao> group;
    GroupAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ongroup_activity);

        listView = findViewById(R.id.groupListView);
        group = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnGroupActivity.this, AddGroupActivity.class);
                startActivity(intent);
            }
        });

        initFirebase();
        showData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GroupDao dao = group.get(i);
//                Toast.makeText(OnGroupActivity.this, dao.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), InGroupActivity.class);
                intent.putExtra("groupID", dao.getGroupid());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                GroupDao dao = group.get(i);
                databaseReference.child("Group_List").child(dao.getGroupid()).removeValue();
                return false;
            }
        });
    }

    private void showData() {
        Query query = databaseReference.child("Group_List");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                group.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    GroupDao dao = postSnapshot.getValue(GroupDao.class);
                    group.add(dao);
                }
                adapter = new GroupAdapter(group);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }





}
