package budgetapp.napkkk.ourbudget2.controller;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import budgetapp.napkkk.ourbudget2.controller.adapter.InchargeAdapter;
import budgetapp.napkkk.ourbudget2.model.GroupDao;
import budgetapp.napkkk.ourbudget2.R;
import budgetapp.napkkk.ourbudget2.model.TransactionDao;
import budgetapp.napkkk.ourbudget2.controller.adapter.SectionPageAdapter;
import budgetapp.napkkk.ourbudget2.model.UserDao;
import budgetapp.napkkk.ourbudget2.view.tabfragment.EXPENSE_fragment;
import budgetapp.napkkk.ourbudget2.view.tabfragment.HISTORY_fragment;
import budgetapp.napkkk.ourbudget2.view.tabfragment.INCOME_fragment;

public class InGroupActivity extends AppCompatActivity {

    private String selected;

    INCOME_fragment income_fragment;
    EXPENSE_fragment expense_fragment;
    HISTORY_fragment history_fragment;
    Bundle bundle;
    DatabaseReference databaseReference;
    List<GroupDao> group;
    TextView currentmoney, goalmoney, moneybound, spaceshow, goalshow, description, timeshow, dialog_banner, membernumber;
    EditText add_descript, add_money;
    LinearLayout timesection;
    Toolbar toolbar;
    GroupDao dao;
    AlertDialog.Builder alert;
    String transactionId = "dummy";
    TabLayout tabLayout;
    ViewPager mViewPager;
    DecimalFormat formatter;
    List<UserDao> userNormal, userInCharge;
    ListView showIncharge_listview, showAddInchagre_listview;
    HashMap<String, HashMap<String, String>> incharge;
    InchargeAdapter inchargeAdapter;
    Button addinchargeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingroup_acitivity);


        initFirebase();
        initInstance();
        getQuery();

    }

    ///////////// ------------ Menu -------------------- /////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_in_group, menu);
        displayMemberAmount();
        menu.add(Menu.NONE, Menu.NONE, 1, "eiei").setActionView(membernumber).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_member) {
            Intent intent = new Intent(InGroupActivity.this, ViewMemberActivity.class);
            intent.putExtra("groupID", catchID());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ///////////// ----------------------------------- /////

    //////////// ----- init & setup ------------------ /////

    private void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void initInstance() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userNormal = new ArrayList<>();
        userInCharge = new ArrayList<>();
        group = new ArrayList<>();
        bundle = new Bundle();

        formatter = new DecimalFormat("#,###,###");
        incharge = new HashMap<>();

        membernumber = new TextView(InGroupActivity.this);

        currentmoney = findViewById(R.id.currentmoney);
        goalmoney = findViewById(R.id.goalmoney);
        moneybound = findViewById(R.id.moneyBound);
        spaceshow = findViewById(R.id.spaceshow);
        goalshow = findViewById(R.id.goalshow);
        timeshow = findViewById(R.id.timeshow);
        description = findViewById(R.id.description);
        timesection = findViewById(R.id.timesection);

        FloatingActionButton fab = (findViewById(R.id.fab));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog(view);
            }
        });
    }

    private void setupFragment(GroupDao dao) {
            bundle.putString("ingroupid", dao.getGroupid());
            income_fragment = new INCOME_fragment();
            income_fragment.setArguments(bundle);
            expense_fragment = new EXPENSE_fragment();
            expense_fragment.setArguments(bundle);
            history_fragment = new HISTORY_fragment();
            history_fragment.setArguments(bundle);
    }


    private void setupViewPager(GroupDao dao) {
            final SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
            mViewPager = findViewById(R.id.container);
            tabLayout = findViewById(R.id.tabs);
            setupFragment(dao);


            switch (dao.getType()) {
                case "income":
                    adapter.addFragment(income_fragment, "รายรับ");
                    adapter.addFragment(history_fragment, "ประวัติ");
                    break;
                case "expense":
                    adapter.addFragment(expense_fragment, "รายจ่าย");
                    adapter.addFragment(history_fragment, "ประวัติ");
                    break;
                case "both":
                    adapter.addFragment(income_fragment, "รายรับ");
                    adapter.addFragment(expense_fragment, "รายจ่าย");
                    adapter.addFragment(history_fragment, "ประวัติ");
                    break;
            }

            mViewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(mViewPager);

            selected = String.valueOf(adapter.getPageTitle(0));
            switch (selected){
                case "รายรับ" : selected = "income";
                    break;
                case "รายจ่าย" : selected = "expense";
                    break;
                case "ประวัติ" : selected = "history";
                    break;
            }

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (String.valueOf(adapter.getPageTitle(tab.getPosition()))){
                        case "รายรับ" : selected = "income";
                                     break;
                        case "รายจ่าย" : selected = "expense";
                                     break;
                        case "ประวัติ" : selected = "history";
                                     break;
                    }
                    Toast.makeText(InGroupActivity.this, selected, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

    }

    private String catchID() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            return bundle.getString("groupID");
        }

        return null;
    }

    private void setData(GroupDao dao) {
        if ("OFF".equals(dao.getTarget())) {
            goalmoney.setVisibility(View.GONE);
            moneybound.setVisibility(View.GONE);
            spaceshow.setVisibility(View.GONE);
            goalshow.setVisibility(View.GONE);
        }
        else{
            goalmoney.setText(formatter.format(Integer.parseInt(dao.getTarget())));
        }

        if ("OFF".equals(dao.getTime())) {
            timesection.setVisibility(View.GONE);
        }
        toolbar.setTitle(dao.getName());

        currentmoney.setText(formatter.format(Integer.parseInt(dao.getMoney())));

        timeshow.setText(dao.getTime());

        if (dao.getDescription().isEmpty()) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(dao.getDescription());
        }

    }

    /////////////// ---------------------------------- ///////////////////////////

    /////////////// ------------Query----------------- ///////////////////////////
    private void getQuery() {
        Query query = databaseReference.child("Group_List").child(catchID());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dao = dataSnapshot.getValue(GroupDao.class);
                setData(dao);
                setupViewPager(dao);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addTransaction(String descript, String money) {

        //checking if the value is provided
        if (!TextUtils.isEmpty(descript)) {

            TransactionDao transactionDao = new TransactionDao();
            transactionDao.setId(transactionId);
            transactionDao.setDescription(descript);
            transactionDao.setMoney(money);
            transactionDao.setType(selected);
//            transactionDao.setIncharge(incharge);
            transactionDao.setIngroupid(dao.getGroupid());

            databaseReference.child("Transaction").child(transactionId).setValue(transactionDao);
            Toast.makeText(InGroupActivity.this, "สามารถปัดเลื่อนขวาที่รายการเพื่ออัพเดตข้อมูลได้", Toast.LENGTH_SHORT).show();

            Toast.makeText(this, "Transaction added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }

    private void inchargeQuery() {
        Query query = databaseReference.child("Transaction").child(transactionId).child("incharge");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userNormal.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserDao userDao = postSnapshot.getValue(UserDao.class);
                    userNormal.add(userDao);
                }
                inchargeAdapter = new InchargeAdapter(userNormal);
                showIncharge_listview.setAdapter(inchargeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void moneyChange(String grpid, String type, Integer money) {
        Integer moneyNow,
                getMoney = Integer.parseInt(dao.getMoney());

        if(type.equals("income")) {
            moneyNow = getMoney + money;
        }
        else{
            moneyNow = getMoney - money;
        }

        startCountAnimation(getMoney, moneyNow);
        databaseReference.child("Group_List").child(grpid).child("money").setValue(moneyNow.toString());
    }

    private void addinchargeQuery() {
        Query query = databaseReference.child("Group_List").child(catchID()).child("inmember");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInCharge.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserDao userDao = postSnapshot.getValue(UserDao.class);
                    userInCharge.add(userDao);
                }
                inchargeAdapter = new InchargeAdapter(userInCharge);
                showAddInchagre_listview.setAdapter(inchargeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                finish();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////// Dialog ////////////////////////////////////////

    @SuppressLint("InflateParams")
    private void showAddDialog(View mView){
        transactionId = databaseReference.child("TransactionId").push().getKey();
        databaseReference.child("Transaction").child(transactionId).child("ingroupid").setValue(dao.getGroupid());
        databaseReference.child("Transaction").child(transactionId).child("type").setValue("history");

        alert = new AlertDialog.Builder(InGroupActivity.this);
        mView = getLayoutInflater().inflate(R.layout.dialog_addtrans, null);
        dialog_banner = mView.findViewById(R.id.dialog_banner);
        add_descript = mView.findViewById(R.id.adddescript);
        add_money = mView.findViewById(R.id.addmoney);
        showIncharge_listview = mView.findViewById(R.id.incharge_person);
        addinchargeBtn = mView.findViewById(R.id.addincharge_button);


        inchargeQuery();

        if(selected.equals("income")){
            dialog_banner.setText("เพิ่มรายการรายรับ");
            dialog_banner.setBackgroundColor(Color.parseColor("#FF46A746"));
        }
        else{
            dialog_banner.setText("เพิ่มรายการรายจ่าย");
            dialog_banner.setBackgroundColor(Color.parseColor("#ff6961"));
        }

        alert.setView(mView)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addTransaction(add_descript.getText().toString(), add_money.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseReference.child("Transaction").child(transactionId).removeValue();
                        dialog.dismiss();
                    }
                });

        addinchargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddInchargeDialog();
            }
        });

        alert.create();
        alert.show();

    }


    private void showAddInchargeDialog(){
        alert = new AlertDialog.Builder(InGroupActivity.this);
        @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.dialog_addincharge, null);
        showAddInchagre_listview = mView.findViewById(R.id.addincharge_person);
        alert.setView(mView);

        alert.setNegativeButton("กลับ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        addinchargeQuery();

        showAddInchagre_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                databaseReference.child("Transaction").child(transactionId).child("incharge").child(userInCharge.get(i).getUserName()).child("userName").setValue(userInCharge.get(i).getUserName());
                databaseReference.child("Transaction").child(transactionId).child("incharge").child(userInCharge.get(i).getUserName()).child("userPic").setValue(userInCharge.get(i).getUserPic());
            }
        });

        alert.show();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////// Display ////////////////////////////////////////////////////

    private void startCountAnimation(Integer start, Integer stop) {
        ValueAnimator animator = ValueAnimator.ofInt(start, stop);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                currentmoney.setText(formatter.format(animation.getAnimatedValue()));
            }
        });
        animator.start();
    }

    private void displayMemberAmount(){
        Query query = databaseReference.child("Group_List").child(dao.getGroupid()).child("inmember");
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                membernumber.setText(dataSnapshot.getChildrenCount()+"");
                membernumber.setTextColor(Color.WHITE);
                membernumber.setTypeface(null, Typeface.BOLD);
                membernumber.setTextSize(14);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////


}
