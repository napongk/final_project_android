package budgetapp.napkkk.ourbudget2;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by napkkk on 25/11/2560.
 */

public class InGroupAdapter extends BaseAdapter {

    List<TransactionDao> ingroup;

    public InGroupAdapter(List<TransactionDao> ingroup) {
        this.ingroup = ingroup;
    }

    @Override
    public int getCount() {
        if(ingroup == null){
            return 0;
        }
        return ingroup.size();
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
        InGroup_View item;
        if (view != null)
            item = (InGroup_View) view;
        else
            item = new InGroup_View(viewGroup.getContext());

        TransactionDao dao = ingroup.get(i);

        item.setTvDescription(dao.getDescription());
        item.setTvCost(dao.getMoney());
        item.setVisibilty(dao.getType());

        return item;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
