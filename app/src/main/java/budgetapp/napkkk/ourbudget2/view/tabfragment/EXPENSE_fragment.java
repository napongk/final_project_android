package budgetapp.napkkk.ourbudget2.view.tabfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import budgetapp.napkkk.ourbudget2.R;
import budgetapp.napkkk.ourbudget2.controller.InGroupActivity;
import budgetapp.napkkk.ourbudget2.controller.adapter.InGroupAdapter;
import budgetapp.napkkk.ourbudget2.model.TransactionDao;
import budgetapp.napkkk.ourbudget2.view.SwipeToDeleteListViewListener;


public class EXPENSE_fragment extends android.support.v4.app.Fragment {

    private static final String TAG = "EXPENSE_FRAGMENT";
    DatabaseReference databaseReference;
    List<TransactionDao> transaction;
    InGroupAdapter adapter;
    ListView listView;
    String ingroupid;
    InGroupActivity activity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment, container, false);

        Bundle bundle = getArguments();
        ingroupid = bundle.getString("ingroupid");

        transaction = new ArrayList<>();
        listView = view.findViewById(R.id.ingroup_listview);
        initFirebase();
        showData();

        activity = (InGroupActivity) getActivity();

        adapter = new InGroupAdapter(transaction);

        SwipeToDeleteListViewListener swipeListener = new SwipeToDeleteListViewListener(listView, new SwipeToDeleteListViewListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    TransactionDao dao = transaction.get(position);
                    activity.moneyChange(dao.getIngroupid(), dao.getType(), Integer.parseInt(dao.getMoney()));
                    databaseReference.child("Transaction").child(dao.getId()).child("type").setValue("history");
                    adapter.notifyDataSetChanged();
                }
            }
        });

        listView.setOnTouchListener(swipeListener);

        return view;
    }

    private void showData() {
        Query query = databaseReference.child("Transaction").orderByChild("type").equalTo("expense");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transaction.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getValue(TransactionDao.class).getIngroupid().equals(ingroupid)) {
                        TransactionDao dao = postSnapshot.getValue(TransactionDao.class);
                        transaction.add(dao);
                    }
                }
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
