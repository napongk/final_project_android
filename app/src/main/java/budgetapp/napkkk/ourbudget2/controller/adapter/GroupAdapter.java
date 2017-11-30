package budgetapp.napkkk.ourbudget2.controller.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import budgetapp.napkkk.ourbudget2.model.GroupDao;
import budgetapp.napkkk.ourbudget2.view.OnGroup_View;

/**
 * Created by napkkk on 22/11/2560.
 */

public class GroupAdapter extends BaseAdapter {

    List<GroupDao>  group;

    public GroupAdapter(List<GroupDao> group){
        this.group = group;
    }

    @Override
    public int getCount() {
        if(group == null){
            return 0;
        }
        return group.size();
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
        OnGroup_View item;
        if (view != null)
        item = (OnGroup_View) view;
        else
        item = new OnGroup_View(viewGroup.getContext());

        GroupDao dao = group.get(i);

        item.setTvName(dao.getName());
        item.setTvGroup(dao.getType());

        return item;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
