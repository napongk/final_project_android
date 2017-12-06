package budgetapp.napkkk.ourbudget2.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import budgetapp.napkkk.ourbudget2.controller.adapter.GroupAdapter;
import budgetapp.napkkk.ourbudget2.model.GroupDao;
import budgetapp.napkkk.ourbudget2.R;
import budgetapp.napkkk.ourbudget2.model.TransactionDao;

public class OnGroupActivity extends AppCompatActivity {
    TextView edit_timeFrom, edit_timeTo;
    EditText edit_groupname, edit_description, edit_money;
    LinearLayout edit_form, profilelayout;
    DatabaseReference databaseReference;
    List<GroupDao> group = new ArrayList<>();
    List<TransactionDao> transaction = new ArrayList<>();
    GroupAdapter adapter;
    ListView listView;
    ProfilePictureView profilePictureView;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ongroup_activity);

        listView = findViewById(R.id.groupListView);

        Toast.makeText(OnGroupActivity.this, "หากชื่อผู้ใช้งานและรูปไม่ขึ้น กรุณาล็อคอินใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();


//        getSupportActionBar().setTitle("กลุ่มของคุณ");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnGroupActivity.this, AddGroupActivity.class);
                startActivity(intent);
            }
        });

        initFirebase();
        getFBProfile();

        Toast.makeText(OnGroupActivity.this, sp.getString("name","null"), Toast.LENGTH_SHORT).show();

        showData();
        getTransaction();


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
                showDialog(view, dao);
                return true;
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
                    if(postSnapshot.getValue(GroupDao.class).getInmember() != null) {
                        boolean name = postSnapshot.getValue(GroupDao.class).getInmember().containsKey(sp.getString("name", "null"));
                        if (name) {
                            group.add(dao);
                        }
                    }
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

    private void showDialog(final View view, final GroupDao dao) {
        final CharSequence choice[] = new CharSequence[] {"แก้ไข", "ลบ"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(dao.getName());
        builder.setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(choice[which].equals("ลบ")){
                    databaseReference.child("Group_List").child(dao.getGroupid()).removeValue();
                    databaseReference.child("User").child(sp.getString("name","null"))
                            .child("own").child(dao.getGroupid()).removeValue();

                }
                else{
                    editDialog(view, dao);

                }
            }
        });
        builder.show();
    }

    private void editDialog(View view,final GroupDao dao) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        view = getLayoutInflater().inflate(R.layout.dialog_editgroup, null);

        edit_groupname = view.findViewById(R.id.editgroupname);
        edit_description = view.findViewById(R.id.editdescription);
        edit_money = view.findViewById(R.id.editmoney);
        edit_form = view.findViewById(R.id.timeedit_form);
        edit_timeFrom = view.findViewById(R.id.editFrom);
        edit_timeTo = view.findViewById(R.id.editTo);

        if(dao.getTarget().equals("OFF")){
            edit_money.setVisibility(View.GONE);
        }
        if(dao.getTime().equals("OFF")){
            edit_form.setVisibility(View.GONE);
        }


        edit_groupname.setText(dao.getName());
        edit_description.setText(dao.getDescription());
        edit_money.setText(dao.getTarget());
        edit_timeFrom.setText(dao.getTime());

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("แก้ไข", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        editSet(dao,
                                edit_description.getText().toString(),
                                edit_groupname.getText().toString(),
                                edit_money.getText().toString(),
                                edit_timeFrom.getText().toString());
                        Toast.makeText(OnGroupActivity.this, "Edited", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.create();
        builder.show();
    }

    private void editSet(GroupDao dao, String description, String group_name, String money, String timeform) {
        databaseReference.child("Group_List").child(dao.getGroupid()).child("description").setValue(description);
        if(!dao.getTarget().isEmpty()){
            databaseReference.child("Group_List").child(dao.getGroupid()).child("target").setValue(money);
        }
        databaseReference.child("Group_List").child(dao.getGroupid()).child("name").setValue(group_name);
        databaseReference.child("Group_List").child(dao.getGroupid()).child("time").setValue(timeform);
    }

    private void getFBProfile(){
        profilePictureView = findViewById(R.id.imageLogin2);
        profilelayout = findViewById(R.id.profilelayout);

        sp = getSharedPreferences("FB_PROFILE", Context.MODE_PRIVATE);
        TextView testjson = findViewById(R.id.testjson);
        testjson.setText(sp.getString("name", "null"));
        profilePictureView.setPresetSize(ProfilePictureView.NORMAL);
        profilePictureView.setProfileId(sp.getString("imageid", "null"));

    }

    public void logout(View view) {
        logoutDialog();
    }

    private void logoutDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout ?");
        builder.setPositiveButton("ออกจากระบบ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LoginManager.getInstance().logOut();
                finish();
            }
        });
        builder.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void getTransaction(){
        Query query = databaseReference.child("Transaction");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transaction.clear();
                TransactionDao dao = dataSnapshot.getValue(TransactionDao.class);
                transaction.add(dao);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("คุณต้องการจะออกจาก ourBudget หรือไม่ ?");

        builder.setPositiveButton("ออก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
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
}
