package budgetapp.napkkk.ourbudget2;

/**
 * Created by napkkk on 21/11/2560.
 */

public class GroupDao {
    private String groupid, type, name, time, owner, money, target;

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String id) {
        this.groupid = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
