package budgetapp.napkkk.ourbudget2.model;

import java.util.List;

/**
 * Created by napkkk on 25/11/2560.
 */

public class TransactionDao {
    private String id , ingroupid, description, money, type;
    private List<String> incharge;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getIncharge() {
        return incharge;
    }

    public void setIncharge(List<String> incharge) {
        this.incharge = incharge;
    }

    public String getIngroupid() {
        return ingroupid;
    }

    public void setIngroupid(String ingroupid) {
        this.ingroupid = ingroupid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
