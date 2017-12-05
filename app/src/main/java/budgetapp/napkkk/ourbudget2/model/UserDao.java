package budgetapp.napkkk.ourbudget2.model;

import java.util.HashMap;

/**
 * Created by napkkk on 2/12/2560.
 */

public class UserDao {
    private String userName, userPic;
    private HashMap<String, String> inmember, own;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public HashMap<String, String> getInmember() {
        return inmember;
    }

    public void setInmember(HashMap<String, String> inmember) {
        this.inmember = inmember;

    }

    public HashMap<String, String> getOwn() {
        return own;
    }

    public void setOwn(HashMap<String, String> own) {
        this.own = own;
    }


}
