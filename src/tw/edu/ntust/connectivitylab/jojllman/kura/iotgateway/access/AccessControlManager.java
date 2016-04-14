package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel;

public class AccessControlManager {
	private static final Logger s_logger = LoggerFactory.getLogger(AccessControlManager.class);
	private static AccessControlManager instance;
	public static AccessControlManager getInstance() { return instance; }
	private static final SessionIdentifierGenerator s_deviceIdGen = new SessionIdentifierGenerator();
	private static final SessionIdentifierGenerator s_channelIdGen = new SessionIdentifierGenerator();
	public static String GetRandomDeviceId() {
		return s_deviceIdGen.nextSessionId();
	}
	
	public static String GetRandomChannelId() {
		return s_channelIdGen.nextSessionId();
	}
	
	private Map<String, Permission> m_devicePermissions;
	private Map<String, Permission> m_channelPermissions;
	
	public AccessControlManager() {
		m_devicePermissions = new HashMap<>();
		m_channelPermissions = new HashMap<>();
		
		instance = this;

		s_logger.debug("Access control manager started.");
	}
	
	public boolean registerDevicePermission(IDeviceProfile device, String perm) {
		String id = device.getId();
		if(m_devicePermissions.get(id) != null) {
			s_logger.debug("Device registered already");
			return false;
		}
		
		Permission permission = new Permission(perm);
		m_devicePermissions.put(id, permission);
		return true;
	}
	
	public boolean registerChanncelPermission(TopicChannel<?> channel, String perm) {
		String id = channel.getId();
		if(m_channelPermissions.get(id) != null) {
			s_logger.debug("Channel registered already");
			return false;
		}
		
		Permission permission = new Permission(perm);
		m_channelPermissions.put(id, permission);
		return true;
	}
	
	public boolean unregisterDevicePermission(IDeviceProfile device) {
		String id = device.getId();
		Permission perm = m_devicePermissions.get(id);
		if(perm == null) {
			s_logger.debug("Device didn't exist");
			return false;
		}
		m_devicePermissions.remove(id);
		return true;
	}
	
	public boolean unregisterChanncelPermission(TopicChannel<?> channel) {
		String id = channel.getId();
		Permission perm = m_channelPermissions.get(id);
		if(perm == null) {
			s_logger.debug("Channel didn't exist");
			return false;
		}
		m_channelPermissions.remove(id);
		return true;
	}
	
	public boolean getDeviceReadPermission(IDeviceProfile device, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(device.getId());
		if(permission == null)
			return false;
		
		return permission.getReadPermission(type);
	}
	
	public boolean getDeviceWritePermission(IDeviceProfile device, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(device.getId());
		if(permission == null)
			return false;
		
		return permission.getWritePermission(type);
	}
	
	public boolean getDeviceModifyPermission(IDeviceProfile device, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(device.getId());
		if(permission == null)
			return false;
		
		return permission.getModifyPermission(type);
	}
	
	public boolean getChannelReadPermission(TopicChannel<?> channel, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(channel.getId());
		if(permission == null)
			return false;
		
		return permission.getReadPermission(type);
	}
	
	public boolean getChannelWritePermission(TopicChannel<?> channel, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(channel.getId());
		if(permission == null)
			return false;
		
		return permission.getWritePermission(type);
	}
	
	public boolean getChannelModifyPermission(TopicChannel<?> channel, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(channel.getId());
		if(permission == null)
			return false;
		
		return permission.getModifyPermission(type);
	}
}
