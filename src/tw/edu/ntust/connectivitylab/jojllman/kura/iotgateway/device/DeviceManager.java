package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile.DeviceType;

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
		DeviceProfile profile = new DeviceProfile();
		
		String name = jsonObject.getString("Name");
		String description = jsonObject.getString("Description");
		JSONObject deviceMain = jsonObject.getJSONObject("Detail");
		String type = deviceMain.getString("Type");
		String uuid = deviceMain.getString("UUID");
		
		profile.setName(name);
		profile.setType(DeviceType.valueOf(type));
		profile.setUUID(UUID.fromString(uuid));
		
		JSONArray channels = deviceMain.getJSONArray("Channels");
		
		for(int i=0; i<channels.length(); i++) {
			JSONObject obj = channels.getJSONObject(i);
			String topic = obj.getString("Topic");
			String valueType = obj.getString("Type");
			
			if(valueType.compareToIgnoreCase("Boolean") == 0) {
				Boolean valueBoolDefault = obj.getBoolean("Default");
			}
			else if(valueType.compareToIgnoreCase("Integer") == 0) {
				Integer valueIntegerDefault = obj.getInt("Default");
				Integer valueIntegerMin = obj.getInt("Min");
				Integer valueIntegerMax = obj.getInt("Max");
			}
			else if(valueType.compareToIgnoreCase("Short") == 0) {
				Short valueShortDefault = Short.parseShort(obj.getString("Default"));
				Short valueShortMin = Short.parseShort(obj.getString("Min"));
				Short valueShortMax = Short.parseShort(obj.getString("Max"));
			}
			else if(valueType.compareToIgnoreCase("String") == 0) {
				String valueString = obj.getString("Default");
			}
		}
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
