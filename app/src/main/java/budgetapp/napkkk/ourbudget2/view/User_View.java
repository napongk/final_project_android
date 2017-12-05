package budgetapp.napkkk.ourbudget2.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import budgetapp.napkkk.ourbudget2.R;

/**
 * Created by napkkk on 26/11/2560.
 */

public class User_View extends FrameLayout {
    private TextView tvProfilename;
    private ProfilePictureView profilePic;



    public User_View(@NonNull Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public User_View(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
    }

    public User_View(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
    }

    public void initInflate(){
        inflate(getContext(),R.layout.viewmember_item,this);
    }

    private void initInstances() {
        tvProfilename = findViewById(R.id.profileName);
        profilePic = findViewById(R.id.profilePic);
        profilePic.setPresetSize(ProfilePictureView.NORMAL);

    }

    public void setTvProfilename(String text) {
        tvProfilename.setText(text);
    }

    public void setProfilePic(String id) {
        profilePic.setProfileId(id);
    }
}
