/**
 * 
 */
package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.List;
import java.util.Map;

/**
 * @author jojllman
 * This is a device profile interface class. It's is used to store device's profile
 *
 */
public interface IDeviceProfile {
	public enum DataExchangeProtocol {
		MQTT,CoAP
	}
	public enum CommunicationTechnology {
		BLE,ZigBee,WiFi,Ethernet
	}
	
	public enum QoSLevel {
		None,AtleastOnce,ExactlyOnce
	}
	
	public enum SecurityLevel {
		None,TLS,SSL,DTLS
	}
	
	public boolean setName();
	public boolean setDataExchangeProtocol();
	public boolean setDataExchangeProtocolVersion();
	public boolean setCommunicationTechnology();
	public String getName();
	public String getDataExchangeProtocol();
	public String getDataExchangeProtocolVersion();
	public String getCommunicationTechnology();
	
	public List<String> getDataTopicNames();
	public Map<String, String> getAlternativeDataTopicNames();
	public boolean setAlternativeDataTopicName(String oriTopic, String newTopic);
	public Map<String, QoSLevel> getDataTopicQoSs();
	public boolean setDataTopicQoS(String topic, QoSLevel qos);
	public Map<String, SecurityLevel> getDataTopicSecurities();
	public boolean setDataTopicSecurity(String topic, SecurityLevel security);
	
	public List<String> getControlTopicNames();
	public Map<String, String> getAlternativeControlTopicNames();
	public boolean setAlternativeControlTopicName(String oriTopic, String newTopic);
	public Map<String, QoSLevel> getControlTopicQoSs();
	public boolean setControlTopicQoS(String topic, QoSLevel qos);
	public Map<String, SecurityLevel> getControlTopicSecurities();
	public boolean setControlTopicSecurity(String topic, SecurityLevel security);
}
