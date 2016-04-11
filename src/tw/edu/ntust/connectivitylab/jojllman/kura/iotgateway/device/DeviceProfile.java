package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.AccessControlManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel.ChannelDataType;

public class DeviceProfile implements IDeviceProfile{
	private static final Logger s_logger = LoggerFactory.getLogger(DeviceProfile.class);

	static public DeviceProfile parseDeviceAdvertisementMessage(byte[] data) {
		return parseDeviceAdvertisementMessage(new JSONObject(new String(data)));
	}
	static public DeviceProfile parseDeviceAdvertisementMessage(JSONObject jsonObject) {
		AccessControlManager accessControlManager = AccessControlManager.getInstance();
		DeviceProfile profile = new DeviceProfile();
		
		String name = jsonObject.getString("Name");
		String description = jsonObject.getString("Description");
		JSONObject deviceMain = jsonObject.getJSONObject("Detail");
		String type = deviceMain.getString("Type");
		String uuid = deviceMain.getString("UUID");
		String perm = deviceMain.getString("Permission");
		
		profile.m_jsonRoot = jsonObject;
		profile.setId(AccessControlManager.GetRandomDeviceId());
		profile.setName(name);
		profile.setDescription(description);
		profile.setType(DeviceType.valueOf(type));
		profile.setUUID(UUID.fromString(uuid));
		accessControlManager.registerDevicePermission(profile, perm);
		
		JSONArray channels = deviceMain.getJSONArray("Channels");
		
		for(int i=0; i<channels.length(); i++) {
			JSONObject obj = channels.getJSONObject(i);
			String topic = obj.getString("Topic");
			String valueType = obj.getString("Type");
			String valuePermission = obj.getString("Permission");
			String id = AccessControlManager.GetRandomChannelId();
			TopicChannel<?> ch = null;
			
			if(valueType.compareToIgnoreCase("Boolean") == 0) {
				Boolean valueBoolDefault = obj.getBoolean("Default");
				ch = new TopicChannel<Boolean>(ChannelDataType.Boolean, 
						topic, 
						valuePermission, 
						valueBoolDefault);
			}
			else if(valueType.compareToIgnoreCase("Integer") == 0) {
				Integer valueIntegerDefault = obj.getInt("Default");
				Integer valueIntegerMin = obj.getInt("Min");
				Integer valueIntegerMax = obj.getInt("Max");
				ch = new TopicChannel<Integer>(ChannelDataType.Integer, 
						topic, 
						valuePermission, 
						valueIntegerDefault, valueIntegerMin, valueIntegerMax);
			}
			else if(valueType.compareToIgnoreCase("Short") == 0) {
				Short valueShortDefault = Short.parseShort(obj.getString("Default"));
				Short valueShortMin = Short.parseShort(obj.getString("Min"));
				Short valueShortMax = Short.parseShort(obj.getString("Max"));
				ch = new TopicChannel<Short>(ChannelDataType.Short, 
						topic, 
						valuePermission, 
						valueShortDefault, valueShortMin, valueShortMax);
				
			}
			else if(valueType.compareToIgnoreCase("String") == 0) {
				String valueString = obj.getString("Default");
				ch = new TopicChannel<String>(ChannelDataType.String, 
						topic, 
						valuePermission, 
						valueString);
			}
			
			if(ch == null) {
				s_logger.error("Channel read error!");
			}
			else {
				ch.setId(id);
				profile.addChannel(ch);
				accessControlManager.registerChanncelPermission(ch, valuePermission);
			}
			
		}
		
		return profile;
	}
	
	private String m_id;
	private String m_name;
	private String m_description;
	private DeviceType m_type;
	private UUID m_uuid;
	private Map<String, TopicChannel<?>> m_channleList;
	private JSONObject m_jsonRoot;
	
	public DeviceProfile() {
		m_channleList = new HashMap<>();
	}
	
	private void addChannel (TopicChannel<?> channel) {
		m_channleList.put(channel.getTopic(), channel);
	}
	
	@Override
	public boolean setName(String name) {
		if(name == null)
			return false;
		
		this.m_name = name;
		return true;
	}

	@Override
	public boolean setDataExchangeProtocol(DataExchangeProtocol protocol) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setDataExchangeProtocolVersion(String version) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setCommunicationTechnology(CommunicationTechnology tech) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setDescription(String description) {
		if(description == null)
			return false;
		
		this.m_description = description;
		return true;
	}

	@Override
	public boolean setType(DeviceType type) {
		this.m_type = type;
		return true;
	}

	@Override
	public boolean setUUID(UUID uuid) {
		if(uuid != null)
			return false;
		
		this.m_uuid = uuid;
		return true;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public DataExchangeProtocol getDataExchangeProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataExchangeProtocolVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommunicationTechnology getCommunicationTechnology() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		return m_description;
	}

	@Override
	public DeviceType getType() {
		return m_type;
	}

	@Override
	public UUID getUUID() {
		return m_uuid;
	}

	@Override
	public List<String> getDataTopicNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getAlternativeDataTopicNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setAlternativeDataTopicName(String oriTopic, String newTopic) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, QoSLevel> getDataTopicQoSs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setDataTopicQoS(String topic, QoSLevel qos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, SecurityLevel> getDataTopicSecurities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setDataTopicSecurity(String topic, SecurityLevel security) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getControlTopicNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getAlternativeControlTopicNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setAlternativeControlTopicName(String oriTopic, String newTopic) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, QoSLevel> getControlTopicQoSs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setControlTopicQoS(String topic, QoSLevel qos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, SecurityLevel> getControlTopicSecurities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setControlTopicSecurity(String topic, SecurityLevel security) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JSONObject getJSONRoot() {
		return m_jsonRoot;
	}
	@Override
	public boolean setId(String id) {
		if(id == null) {
			return false;
		}
		
		this.m_id = id;
		return true;
	}
	@Override
	public String getId() {
		return m_id;
	}
	@Override
	public List<TopicChannel<?>> getChannels() {
		return new ArrayList<>(m_channleList.values());
	}

}
