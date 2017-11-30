package budgetapp.napkkk.ourbudget2.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import budgetapp.napkkk.ourbudget2.R;

/**
 * Created by napkkk on 22/11/2560.
 */

public class OnGroup_View extends FrameLayout{
    private TextView tvName;
    private TextView tvGroup;


    public OnGroup_View(@NonNull Context context) {
        super(context);
        initInflate();
        initInstances();
    }



    public OnGroup_View(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
    }

    public OnGroup_View(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
    }

    public void initInflate(){
        inflate(getContext(), R.layout.ongroup_item,this);
    }

    private void initInstances() {
        tvName = findViewById(R.id.groupname_view);
        tvGroup = findViewById(R.id.type_view);
    }

    public void setTvName(String text) {
        tvName.setText(text);
    }
    public void setTvGroup(String text) {
        tvGroup.setText(text);
    }

//    @Override
//    protected Parcelable onSaveInstanceState() {
//        Parcelable superState = super.onSaveInstanceState();
//
//        BundleSavedState savedState = new BundleSavedState(superState);
//        // Save Instance State(s) here to the 'savedState.getBundle()'
//        // for example,
//        // savedState.getBundle().putString("key", value);
//
//        return savedState;
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        BundleSavedState ss = (BundleSavedState) state;
//        super.onRestoreInstanceState(ss.getSuperState());
//
//        Bundle bundle = ss.getBundle();
//        // Restore State from bundle here
//    }

}
