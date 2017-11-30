package budgetapp.napkkk.ourbudget2.controller;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import budgetapp.napkkk.ourbudget2.model.GroupDao;
import budgetapp.napkkk.ourbudget2.R;
import budgetapp.napkkk.ourbudget2.model.TransactionDao;
import budgetapp.napkkk.ourbudget2.controller.adapter.SectionPageAdapter;
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
    TextView currentmoney, goalmoney, moneybound, spaceshow, goalshow, description, timeshow, dialog_banner;
    EditText add_descript, add_money;
    LinearLayout timesection;
    Toolbar toolbar;
    GroupDao dao;
    SectionPageAdapter adapter;
    AlertDialog.Builder alert;
    TabLayout tabLayout;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingroup_acitivity);

        initInstance();
        initFirebase();
        getQuery();

    }

    ///////////// ------------ Menu -------------------- /////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_in_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

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

        group = new ArrayList<>();
        bundle = new Bundle();

        SectionPageAdapter sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

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

    private void setupFragment() {
        bundle.putString("ingroupid", dao.getGroupid());
        income_fragment = new INCOME_fragment();
        income_fragment.setArguments(bundle);
        expense_fragment = new EXPENSE_fragment();
        expense_fragment.setArguments(bundle);
        history_fragment = new HISTORY_fragment();
        history_fragment.setArguments(bundle);
    }


    private void setupViewPager() {
        final SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        tabLayout = findViewById(R.id.tabs);
        setupFragment();


        switch (dao.getType()) {
            case "income":
                adapter.addFragment(income_fragment, "income");
                adapter.addFragment(history_fragment, "history");
                break;
            case "expense":
                adapter.addFragment(expense_fragment, "expense");
                adapter.addFragment(history_fragment, "history");
                break;
            case "both":
                adapter.addFragment(income_fragment, "income");
                adapter.addFragment(expense_fragment, "expense");
                adapter.addFragment(history_fragment, "history");
                break;
        }

        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);
        selected = String.valueOf(adapter.getPageTitle(0));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selected = String.valueOf(adapter.getPageTitle(tab.getPosition()));
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

    /////////////// ---------------------------------- ///////////////////////////

    private String catchID() {
        Bundle bundle = getIntent().getExtras();
        return bundle.getString("groupID");
    }

    private void getQuery() {
        Query query = databaseReference.child("Group_List").child(catchID());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dao = dataSnapshot.getValue(GroupDao.class);
                Toast.makeText(InGroupActivity.this, dao.getGroupid(), Toast.LENGTH_SHORT).show();
                setData();
                setupViewPager();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setData() {
        if ("OFF".equals(dao.getTarget())) {
            goalmoney.setVisibility(View.GONE);
            moneybound.setVisibility(View.GONE);
            spaceshow.setVisibility(View.GONE);
            goalshow.setVisibility(View.GONE);
        }
        if ("OFF".equals(dao.getTime())) {
            timesection.setVisibility(View.GONE);
        }
        toolbar.setTitle(dao.getName());
        currentmoney.setText(dao.getMoney());
        goalmoney.setText(dao.getTarget());
        timeshow.setText(dao.getTime());

        if(dao.getDescription().isEmpty()){
            description.setVisibility(View.GONE);
        }
        else {
            description.setText(dao.getDescription());
        }

    }

    private void showAddDialog(View mView){
        alert = new AlertDialog.Builder(InGroupActivity.this);
        mView = getLayoutInflater().inflate(R.layout.dialog_addtrans, null);
        dialog_banner = mView.findViewById(R.id.dialog_banner);
        add_descript = mView.findViewById(R.id.adddescript);
        add_money = mView.findViewById(R.id.addmoney);

        if(selected.equals("income")){
            dialog_banner.setText("Income Transaction");
            dialog_banner.setBackgroundColor(Color.parseColor("#FF46A746"));
        }
        else{
            dialog_banner.setText("Expense Transaction");
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
                        dialog.dismiss();
                    }
                });
        alert.create();
        alert.show();

    }

    private void addTransaction(String descript, String money) {
        //checking if the value is provided
        if (!TextUtils.isEmpty(descript)) {
            String id = databaseReference.child("Transaction").push().getKey();

            TransactionDao transactionDao = new TransactionDao();
            transactionDao.setId(id);
            transactionDao.setDescription(descript);
            transactionDao.setMoney(money);
            transactionDao.setType(selected);
            transactionDao.setIngroupid(dao.getGroupid());

            databaseReference.child("Transaction").child(id).setValue(transactionDao);

            Toast.makeText(this, "Transaction added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }

    public void testQuery2(String grpid, String type, Integer money) {
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

    private void startCountAnimation(Integer start, Integer stop) {
        ValueAnimator animator = ValueAnimator.ofInt(start, stop);
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                currentmoney.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }


}
