package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.Permission;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile.DataExchangeProtocol;

public class TopicChannel<T> {
	static public enum ChannelDataType {
		Boolean, Integer, Short, String
	}
	static public enum ChannelMode {
		r,w,o,rw,ow
	}
	
	private IDeviceProfile device;
	
	private T obj;
	private T min;
	private T max;
	private String topic;
	private String altTopic;
	private String id;
	private ChannelDataType type;
	private Permission perm;
	private ChannelMode mode;
	private String description;
	private String url;
	
	public TopicChannel(ChannelDataType type, String topic, String perm) {
		this.type = type;
		this.topic = topic;
		this.perm = new Permission(perm);
	}

	public TopicChannel(ChannelDataType type, String topic, String perm, T data) {
		this.type = type;
		this.topic = topic;
		this.perm = new Permission(perm);
	}
	
	public TopicChannel(ChannelDataType type, String topic, String perm, T data, T min, T max) {
		this.type = type;
		this.topic = topic;
		this.perm = new Permission(perm);
	}
	
	public TopicChannel(ChannelDataType type, String topic, String perm, T min, T max) {
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
	public String getAlternativeTopic() { return altTopic; }
	public String setAlternativeTopic(String topic) { altTopic = topic; return altTopic; }
	public String getId() { return id;}
	public String setId(String id) { this.id = id; return this.id; } 
	public ChannelMode getMode() { return mode; }
	public ChannelMode setMode(ChannelMode mode) { this.mode = mode; return this.mode; }
	public String setDescription(String desc) { this.description = desc; return this.description; }
	public String getDescription() { return this.description; }
	public String setURL(String coapURL) { this.url = coapURL; return this.url; }
	public String getURL() { return this.url; }
	public IDeviceProfile setDevice(IDeviceProfile device) { this.device = device; return this.device; }
	public IDeviceProfile getDevice() { return this.device; }
	
	public boolean initialize() {
		if(id == null || topic == null)
			return false;
		
		DataExchangeProtocol protocol = device.getDataExchangeProtocol();
		if(protocol == DataExchangeProtocol.MQTT) {
			
		}
		
		return true;
	}
}
