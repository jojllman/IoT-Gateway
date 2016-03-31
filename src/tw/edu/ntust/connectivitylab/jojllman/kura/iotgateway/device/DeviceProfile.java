package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceProfile implements IDeviceProfile{
	private static final Logger s_logger = LoggerFactory.getLogger(DeviceProfile.class);
	
	static public enum ChannelDataType {
		Boolean, Integer, Short, String
	}
	
	static public enum PermissionType {
		All, Own, Group
	}
	
	static public class Permission {
		private short perm;
		private byte all;
		private byte own;
		private byte group;
		
		public Permission(short perm) {
			setPermission(perm);
		}
		
		public Permission(String perm) {
			setPermission(perm);
		}
		
		public short setPermission(short perm) {
			this.perm = perm;
			all = (byte) ((perm & 0x1C) >> 6);
			own = (byte) ((perm & 0x38) >> 6);
			group = (byte) ((perm & 0x7) >> 6);
			return this.perm;
		}
		
		public String setPermission(String perm) {
			if(perm.length() != 3) {
				s_logger.warn("Permission is not right: " + perm + ", fallback to 777");
				perm = "777";
			}
			
			all = (byte) (perm.charAt(0) - '0');
			own = (byte) (perm.charAt(1) - '0');
			group = (byte) (perm.charAt(2) - '0');
			
			this.perm = (short) ((all << 6) & (own << 3) & (group));
			return perm;
		}
		
		public short getPermission() { return perm; }
		public String getPermissionString() { return all + "" + own + "" + group; }
		public boolean getReadPermission(PermissionType type) {
			switch (type) {
			case All:
				return (all & 0x4) != 0;
			case Own:
				return (own & 0x4) != 0;
			case Group:
				return (group & 0x4) != 0;
			default:
				return false;
			}
		}
		public boolean getWritePermission(PermissionType type) {
			switch (type) {
			case All:
				return (all & 0x2) != 0;
			case Own:
				return (own & 0x2) != 0;
			case Group:
				return (group & 0x2) != 0;
			default:
				return false;
			}
		}
		public boolean getModifyPermission(PermissionType type) {
			switch (type) {
			case All:
				return (all & 0x1) != 0;
			case Own:
				return (own & 0x1) != 0;
			case Group:
				return (group & 0x1) != 0;
			default:
				return false;
			}
		}
	}
	
	static public class Channel<T> {
		
		private T obj;
		private T min;
		private T max;
		private String topic;
		private ChannelDataType type;
		private Permission perm;
		
		public Channel(ChannelDataType type, String topic, String perm) {
			this.type = type;
			this.topic = topic;
			this.perm = new Permission(perm);
		}

		public Channel(ChannelDataType type, String topic, String perm, T data) {
			this.type = type;
			this.topic = topic;
			this.perm = new Permission(perm);
		}
		
		public Channel(ChannelDataType type, String topic, String perm, T data, T min, T max) {
			this.type = type;
			this.topic = topic;
			this.perm = new Permission(perm);
		}
		
		public Channel(ChannelDataType type, String topic, String perm, T min, T max) {
			this.type = type;
			this.topic = topic;
			this.perm = new Permission(perm);
		}
		
		public T getValue() { return obj; }
		public T setValue(T value) { obj = value; return obj; }
		public ChannelDataType getType() { return type; }
		public ChannelDataType setType(ChannelDataType type) { this.type = type; return type; }
		public T getMin() { return min; }
		public T setMin(T value) { min = value; return min; }
		public T getMax() { return max; }
		public T setMax(T value) { max = value; return max; }
		public String getTopic() { return topic; }
		public String setTopic(String topic) { this.topic = topic; return this.topic; }
		public String getPermission() { return this.perm.getPermissionString(); }
		public String setPermission(String perm) {
			this.perm.setPermission(perm);
			return this.perm.getPermissionString();
		}
		
	}

	static public DeviceProfile parseDeviceAdvertisementMessage(byte[] data) {
		String parseData = new String(data);
		JSONObject jsonObject = new JSONObject(parseData);
		DeviceProfile profile = new DeviceProfile();
		
		String name = jsonObject.getString("Name");
		String description = jsonObject.getString("Description");
		JSONObject deviceMain = jsonObject.getJSONObject("Detail");
		String type = deviceMain.getString("Type");
		String uuid = deviceMain.getString("UUID");
		String perm = deviceMain.getString("Permission");
		
		profile.m_jsonRoot = jsonObject;
		profile.setName(name);
		profile.setDescription(description);
		profile.setType(DeviceType.valueOf(type));
		profile.setUUID(UUID.fromString(uuid));
		profile.setPermission(perm);
		
		JSONArray channels = deviceMain.getJSONArray("Channels");
		
		for(int i=0; i<channels.length(); i++) {
			JSONObject obj = channels.getJSONObject(i);
			String topic = obj.getString("Topic");
			String valueType = obj.getString("Type");
			String valuePermission = obj.getString("Permission");
			
			if(valueType.compareToIgnoreCase("Boolean") == 0) {
				Boolean valueBoolDefault = obj.getBoolean("Default");
				Channel<Boolean> ch = new Channel<Boolean>(ChannelDataType.Boolean, 
						topic, 
						valuePermission, 
						valueBoolDefault);
				profile.addChannel(ch);
			}
			else if(valueType.compareToIgnoreCase("Integer") == 0) {
				Integer valueIntegerDefault = obj.getInt("Default");
				Integer valueIntegerMin = obj.getInt("Min");
				Integer valueIntegerMax = obj.getInt("Max");
				Channel<Integer> ch = new Channel<Integer>(ChannelDataType.Integer, 
						topic, 
						valuePermission, 
						valueIntegerDefault, valueIntegerMin, valueIntegerMax);
				profile.addChannel(ch);
			}
			else if(valueType.compareToIgnoreCase("Short") == 0) {
				Short valueShortDefault = Short.parseShort(obj.getString("Default"));
				Short valueShortMin = Short.parseShort(obj.getString("Min"));
				Short valueShortMax = Short.parseShort(obj.getString("Max"));
				Channel<Short> ch = new Channel<Short>(ChannelDataType.Short, 
						topic, 
						valuePermission, 
						valueShortDefault, valueShortMin, valueShortMax);
				profile.addChannel(ch);
			}
			else if(valueType.compareToIgnoreCase("String") == 0) {
				String valueString = obj.getString("Default");
				Channel<String> ch = new Channel<String>(ChannelDataType.String, 
						topic, 
						valuePermission, 
						valueString);
				profile.addChannel(ch);
			}
			
		}
		
		return profile;
	}
	static public DeviceProfile parseDeviceAdvertisementMessage(JSONObject jsonObject) {
		DeviceProfile profile = new DeviceProfile();
		
		String name = jsonObject.getString("Name");
		String description = jsonObject.getString("Description");
		JSONObject deviceMain = jsonObject.getJSONObject("Detail");
		String type = deviceMain.getString("Type");
		String uuid = deviceMain.getString("UUID");
		String perm = deviceMain.getString("Permission");
		
		profile.m_jsonRoot = jsonObject;
		profile.setName(name);
		profile.setDescription(description);
		profile.setType(DeviceType.valueOf(type));
		profile.setUUID(UUID.fromString(uuid));
		profile.setPermission(perm);
		
		JSONArray channels = deviceMain.getJSONArray("Channels");
		
		for(int i=0; i<channels.length(); i++) {
			JSONObject obj = channels.getJSONObject(i);
			String topic = obj.getString("Topic");
			String valueType = obj.getString("Type");
			String valuePermission = obj.getString("Permission");
			
			if(valueType.compareToIgnoreCase("Boolean") == 0) {
				Boolean valueBoolDefault = obj.getBoolean("Default");
				Channel<Boolean> ch = new Channel<Boolean>(ChannelDataType.Boolean, 
						topic, 
						valuePermission, 
						valueBoolDefault);
				profile.addChannel(ch);
			}
			else if(valueType.compareToIgnoreCase("Integer") == 0) {
				Integer valueIntegerDefault = obj.getInt("Default");
				Integer valueIntegerMin = obj.getInt("Min");
				Integer valueIntegerMax = obj.getInt("Max");
				Channel<Integer> ch = new Channel<Integer>(ChannelDataType.Integer, 
						topic, 
						valuePermission, 
						valueIntegerDefault, valueIntegerMin, valueIntegerMax);
				profile.addChannel(ch);
			}
			else if(valueType.compareToIgnoreCase("Short") == 0) {
				Short valueShortDefault = Short.parseShort(obj.getString("Default"));
				Short valueShortMin = Short.parseShort(obj.getString("Min"));
				Short valueShortMax = Short.parseShort(obj.getString("Max"));
				Channel<Short> ch = new Channel<Short>(ChannelDataType.Short, 
						topic, 
						valuePermission, 
						valueShortDefault, valueShortMin, valueShortMax);
				profile.addChannel(ch);
			}
			else if(valueType.compareToIgnoreCase("String") == 0) {
				String valueString = obj.getString("Default");
				Channel<String> ch = new Channel<String>(ChannelDataType.String, 
						topic, 
						valuePermission, 
						valueString);
				profile.addChannel(ch);
			}
			
		}
		
		return profile;
	}
	
	private String m_name;
	private String m_description;
	private DeviceType m_type;
	private UUID m_uuid;
	private Map<String, Channel<?>> m_channleList;
	private Permission m_permission;
	private JSONObject m_jsonRoot;
	
	private void addChannel (Channel<?> channel) {
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
	public boolean setPermission(String perm) {
		if(perm == null)
			return false;
		
		if(m_permission.setPermission(perm).compareTo(perm) != 0)
			return false;
		
		return true;
	}

	@Override
	public String getPermission() {
		return m_permission.getPermissionString();
	}

	@Override
	public JSONObject getJSONRoot() {
		return m_jsonRoot;
	}

}
