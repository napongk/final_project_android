package budgetapp.napkkk.ourbudget2.view;

import budgetapp.napkkk.ourbudget2.R;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by napkkk on 26/11/2560.
 */

public class InGroup_View extends FrameLayout {
    private TextView tvDescription, tvCost, tvPerson;
    private ImageView incomeicon, expenseicon, historyicon;



    public InGroup_View(@NonNull Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public InGroup_View(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
    }

    public InGroup_View(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
    }

    public void initInflate(){
        inflate(getContext(),R.layout.ingroup_item,this);
    }

    private void initInstances() {
        tvDescription = findViewById(R.id.item_descript);
        tvCost = findViewById(R.id.cost);
        tvPerson = findViewById(R.id.agent);
        incomeicon = findViewById(R.id.incomeicon);
        expenseicon = findViewById(R.id.expenseicon);
        historyicon = findViewById(R.id.historyicon);
    }

    public void setTvDescription(String text) {
        tvDescription.setText(text);
    }
    public void setTvCost(String text) {
        tvCost.setText(text);
    }
    public void setTvPerson(String text) {
        tvPerson.setText(text);
    }

    public void setVisibilty(String text){
        switch(text){
            case "income" : incomeicon.setVisibility(VISIBLE);
                            tvCost.setBackgroundResource(R.drawable.rounded_income);
                            break;
            case "expense" : expenseicon.setVisibility(VISIBLE);
                            tvCost.setBackgroundResource(R.drawable.rounded_expense);
                            break;
            case "history" : historyicon.setVisibility(VISIBLE);
                            tvCost.setBackgroundResource(R.drawable.rounded_history);
                            break;
        }
    }
}
