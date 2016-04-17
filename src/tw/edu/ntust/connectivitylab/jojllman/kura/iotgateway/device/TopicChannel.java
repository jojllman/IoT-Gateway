package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import org.eclipse.paho.client.mqttv3.*;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.Permission;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile.DataExchangeProtocol;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class TopicChannel<T> implements Runnable, MqttCallback {
	private static final Logger s_logger = LoggerFactory.getLogger(TopicChannel.class);
	private static final String s_serverURL = "tcp://iot.eclipse.org:1883";

	static public enum ChannelDataType {
		Boolean, Integer, Short, String
	}
	static public enum ChannelMode {
		r,w,o,rw,ow
	}
	static public enum ChannelQoS {
		FireAndForget(0), DeliverAtleastOnce(1),DeliverExactlyOnce(2);
		private final int mask;

	    ChannelQoS(int mask)
	    {
	        this.mask = mask;
	    }

	    public int getMask()
	    {
	        return mask;
	    }
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
	private ChannelQoS qos;
	private DataExchangeProtocol protocol;
	
	private MqttAsyncClient mqttClient;
	private HashMap<IMqttDeliveryToken, T> mqttDeliverQueue;
	
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
	public T setValue(T value) {
		if(protocol == DataExchangeProtocol.MQTT) {
			try {
				IMqttDeliveryToken token = mqttClient.publish(topic,
                        obj.toString().getBytes(StandardCharsets.UTF_8),
                        qos.getMask(),
                        true);
				mqttDeliverQueue.put(token, value);
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}
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
	public ChannelQoS setQoS(ChannelQoS qos) { this.qos = qos; return this.qos; }
	public ChannelQoS getQoS() { return this.qos; }
	public IDeviceProfile setDevice(IDeviceProfile device) {
		this.device = device;
		protocol = device.getDataExchangeProtocol();
		if(protocol == DataExchangeProtocol.MQTT) {
			mqttDeliverQueue = new HashMap<>();
		}
		return this.device;
	}
	public IDeviceProfile getDevice() { return this.device; }
	
	public boolean connect() {
		if(id == null || topic == null || device == null)
			return false;

		if(protocol == DataExchangeProtocol.MQTT) {
			try {
				DataExchangeProtocol protocol = device.getDataExchangeProtocol();
				if (protocol == DataExchangeProtocol.MQTT) {
					mqttClient = new MqttAsyncClient(s_serverURL, this.id, new MemoryPersistence());
					MqttConnectOptions connOpts = new MqttConnectOptions();
					connOpts.setCleanSession(true);
					IMqttToken token = mqttClient.connect(connOpts);
					token.waitForCompletion(10000);
					mqttClient.setCallback(this);
				}
			} catch (MqttException e) {
				if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_TIMEOUT) {
					s_logger.info("Mqtt timeout, Topic:" + topic);
				} else {
					e.printStackTrace();
				}
				return false;
			}
		}
		else if(protocol == DataExchangeProtocol.COAP) {

		}

		return true;
	}

	public boolean disconnect() {
		if(protocol == DataExchangeProtocol.MQTT) {
			IMqttToken token;
			try {
				token = mqttClient.disconnect(10000);
				token.waitForCompletion(10000);
				mqttClient = null;
			} catch (MqttException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	@Override
	public void run() {
		
	}

	@Override
	public void connectionLost(Throwable throwable) {
		mqttClient = null;
		s_logger.info("Mqtt connection lost, Topic:" + topic);
	}

	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		if(topic.compareToIgnoreCase(altTopic) != 0)
			return;

		T value;
		switch(type) {
			case Integer: {
				value = (T) Integer.valueOf(mqttMessage.getPayload().toString());
				break;
			}
			case Boolean: {
				value = (T) Boolean.valueOf(mqttMessage.getPayload().toString());
				break;
			}
			case Short: {
				value = (T) Short.valueOf(mqttMessage.getPayload().toString());
				break;
			}
			case String: {
				value = (T) mqttMessage.getPayload().toString();
				break;
			}
			default:
				s_logger.error("ChannelDataType error!");
				return;
		}

		obj = value;
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		obj = mqttDeliverQueue.get(iMqttDeliveryToken);
		mqttDeliverQueue.remove(iMqttDeliveryToken);
	}

}
