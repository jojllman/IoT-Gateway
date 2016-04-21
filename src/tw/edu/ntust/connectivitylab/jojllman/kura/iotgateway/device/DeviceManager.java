package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.AccessControlManager;

/**
 * 
 * @author jojllman
 * This is a class for managing device and maintain device profile. Also give device list 
 * to users.
 *
 */

public class DeviceManager {
	private static final Logger s_logger = LoggerFactory.getLogger(DeviceManager.class);
	private static DeviceManager instance;
	public static DeviceManager getInstance() { return instance; }
	
	private List<IDeviceProfile> m_deviceProfiles;
	private Map<Integer, IDeviceProfile> m_pendingProfiles;
	private AccessControlManager m_access;
	
	public DeviceManager() {
		m_deviceProfiles = new ArrayList<>(); 
		instance = this;

		s_logger.debug("Device manager started.");
	}
	
	public void setAccessControlManager(AccessControlManager acc) {
		m_access = acc;
	}

	public boolean isDeviceExist(IDeviceProfile profile) { return m_deviceProfiles.contains(profile); }
	public List<IDeviceProfile> getDeviceProfiles() {
		synchronized (m_deviceProfiles) {
			return new ArrayList<IDeviceProfile>(m_deviceProfiles);
		}
	}
	private void insertDeviceProfile(IDeviceProfile profile) {
		m_deviceProfiles.add(profile);
		s_logger.debug("Insert device profile " + profile);
	}
	
	public void onDeviceJoinRequest(DeviceProfile profile) {
		//TODO: onDeviceJoin
	};
	
	public void onDeviceConnected(JSONObject json) {
		DeviceProfile profile = DeviceProfile.parseDeviceAdvertisementMessage(json);
		onDeviceJoinRequest(profile);
	};
	
	public void onDeviceDisconnected(IDeviceProfile device) {
		String id = device.getId();
		
		if(m_deviceProfiles.contains(device)) {
			m_deviceProfiles.remove(device);
		}
		
		Iterator<Entry<Integer, IDeviceProfile>> it = m_pendingProfiles.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Integer, IDeviceProfile> entry = it.next();
			if(entry.getValue().getId().compareTo(id) == 0) {
				m_pendingProfiles.remove(entry.getKey());
				break;
			}
		}
	};
	
	public void onResponseRequest(boolean accept, int requestID) {
		IDeviceProfile profile = m_pendingProfiles.get(Integer.valueOf(requestID));
		
		if(accept)
			insertDeviceProfile(profile);
		
		m_pendingProfiles.remove(requestID);
	}

	public TopicChannel findChannelById(String channelId) {
		for(IDeviceProfile device : m_deviceProfiles) {
			List<TopicChannel<?>> channels = device.getChannels();
			for(TopicChannel channel : channels) {
				if (channel.getId().compareTo(channelId) == 0) {
					return channel;
				}
			}
		}
		return null;
	}

	public IDeviceProfile findDeviceById(String deviceId) {
		for(IDeviceProfile device : m_deviceProfiles) {
			if(device.getId().compareTo(deviceId) == 0) {
				return device;
			}
		}
		return null;
	}
}
