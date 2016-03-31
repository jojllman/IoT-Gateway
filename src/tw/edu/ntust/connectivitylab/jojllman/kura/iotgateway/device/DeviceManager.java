package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public DeviceManager(int port) {
		m_deviceProfiles = new ArrayList<>(); 
		instance = this;
	}
	
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
	
	public void onDeviceDisconnected() {
		//TODO: onDeviceDisconnected
	};
	
	public void onAcceptRequest(boolean accept, int requestID) {
		IDeviceProfile profile = m_pendingProfiles.get(Integer.valueOf(requestID));
		
		if(accept)
			insertDeviceProfile(profile);
		
		m_pendingProfiles.remove(requestID);
	}
	
}
