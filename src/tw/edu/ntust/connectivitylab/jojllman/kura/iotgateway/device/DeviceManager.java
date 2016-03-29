package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
	
	private List<IDeviceProfile> m_deviceProfiles;
	
	public DeviceManager(int port) {
		m_deviceProfiles = new ArrayList<>(); 
	}
	
	public List<IDeviceProfile> getDeviceProfiles() {
		synchronized (m_deviceProfiles) {
			return new ArrayList<IDeviceProfile>(m_deviceProfiles);	
		}
	}
	
	private void parseDeviceAdvertisementMessage(byte[] data) {
		String parseData = new String(data);
		
		JSONObject jsonObject = new JSONObject(parseData);
		
		String name = jsonObject.getString("Name");
		String description = jsonObject.getString("Description");
		JSONObject deviceMain = jsonObject.getJSONObject("Detail");
		String type = deviceMain.getString("Type");
		String uuid = deviceMain.getString("UUID");
		
		
	}
	
	private void insertDeviceProfile(IDeviceProfile profile) {
		m_deviceProfiles.add(profile);
	}
	
	public void onDeviceConnectRequest() {
		
	};
	
	public void onDeviceConnected() {
		
	};
	
	public void onDeviceDisconnected() {
		
	};
	
}
