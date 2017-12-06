package budgetapp.napkkk.ourbudget2.view.tabfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import budgetapp.napkkk.ourbudget2.controller.adapter.InGroupAdapter;
import budgetapp.napkkk.ourbudget2.R;
import budgetapp.napkkk.ourbudget2.model.TransactionDao;

/**
 * Created by napkkk on 24/11/2560.
 */

public class HISTORY_fragment extends android.support.v4.app.Fragment {

    private static final String TAG = "HISTORY_FRAGMENT";
    DatabaseReference databaseReference;
    List<TransactionDao> transaction;
    InGroupAdapter adapter;
    ListView listView;
    String ingroupid;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment,container,false);

        Bundle bundle = getArguments();
        ingroupid = bundle.getString("ingroupid");

        transaction = new ArrayList<>();
        listView = view.findViewById(R.id.ingroup_listview);
        initFirebase();
        showData();

        return view;
    }

    private void showData() {
        Query query = databaseReference.child("Transaction").orderByChild("type").equalTo("history");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transaction.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if(postSnapshot.getValue(TransactionDao.class).getIngroupid().equals(ingroupid)){
                        TransactionDao dao = postSnapshot.getValue(TransactionDao.class);
                        transaction.add(dao);
                    }
                }
                adapter = new InGroupAdapter(transaction);
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
