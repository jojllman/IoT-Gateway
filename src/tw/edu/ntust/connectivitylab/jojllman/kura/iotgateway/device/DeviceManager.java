package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceManager {
	private static final Logger s_logger = LoggerFactory.getLogger(DeviceManager.class);
	
	private List<IDeviceProfile> m_deviceProfiles;
	
	public DeviceManager(int port) {
		m_deviceProfiles = new ArrayList<>(); 
	}
	
	public List<IDeviceProfile> getDeviceProfiles() {
		synchronized (m_deviceProfiles) {
			return new ArrayList<IDeviceProfile>(m_deviceProfiles);	
		}
	}
	
	public void insertDeviceProfile(IDeviceProfile profile) {
		m_deviceProfiles.add(profile);
	}
	
	public void onDeviceConnectRequest() {
		
	};
	
	public void onDeviceConnected() {
		
	};
	
	public void onDeviceDisconnected() {
		
	};
	
}
