package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jojllman on 2016/4/19.
 */

public class GroupManager {
    private static final Logger s_logger = LoggerFactory.getLogger(GroupManager.class);
    private static GroupManager instance;
    public static GroupManager getInstance() { return instance; }

    private List<Group> groupList;

    public GroupManager() {
        groupList = new ArrayList<>();
        instance = this;
    }

    public boolean isGroupExist(Group group) { return groupList.contains(group); }
    public boolean isGroupExist(String groupName) { return findGroup(groupName) != null; }
    public Group findGroup(String groupName) {
        for(Group group : groupList) {
            if(group.getGroupName().compareToIgnoreCase(groupName) == 0)
                return group;
        }
        return null;
    }
    public boolean addGroup(String groupName) {
        if(findGroup(groupName) != null)
            return false;

        Group group = new Group();
        group.setGroupName(groupName);
        group.setGroupId(AccessControlManager.GetRandomGroupId());
        groupList.add(group);
        return true;
    }
    public boolean removeGroup(Group group) {
        if(!groupList.contains(group))
            return false;

        groupList.remove(group);
        return true;
    }

    public List<Group> getAllGroup() {
        return groupList;
    }
}
