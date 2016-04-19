package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jojllman on 2016/4/19.
 */

public class UserManager {
    private static final Logger s_logger = LoggerFactory.getLogger(UserManager.class);
    private static UserManager instance;
    public static UserManager getInstance() { return instance; }

    private List<User> userList;

    public UserManager() {
        userList = new ArrayList<>();
        instance = this;
    }

    public boolean isUserExist(User user) { return userList.contains(user); }
    public boolean isUserExist(String userName) { return findUser(userName) != null; }
    public User findUser(String username) {
        for(User user : userList) {
            if(user.getUsername().compareToIgnoreCase(username) == 0)
                return user;
        }

        return null;
    }

    public boolean addUser(User user) {
        if (isUserExist(user.getUsername()))
            return false;

        userList.add(user);
        return true;
    }
    public boolean removeUser(User user) {
        User user2 = findUser(user.getUsername());
        if (user2 == null)
            return false;

        userList.remove(user2);
        return true;
    }
}
