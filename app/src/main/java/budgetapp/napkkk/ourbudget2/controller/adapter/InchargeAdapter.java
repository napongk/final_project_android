package budgetapp.napkkk.ourbudget2.controller.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import budgetapp.napkkk.ourbudget2.model.UserDao;
import budgetapp.napkkk.ourbudget2.view.Incharge_View;

public class InchargeAdapter extends BaseAdapter {

    List<UserDao> user;

    public InchargeAdapter(List<UserDao> user) {
        this.user = user;
    }

    @Override
    public int getCount() {
        if (user == null) {
            return 0;
        }
        return user.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Incharge_View item;
        if (view != null) {
            item = (Incharge_View) view;

        } else {
            item = new Incharge_View(viewGroup.getContext());
        }

        UserDao dao = user.get(i);

        item.setTvProfilename(dao.getUserName());
        item.setProfilePic(dao.getUserPic());

        return item;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
