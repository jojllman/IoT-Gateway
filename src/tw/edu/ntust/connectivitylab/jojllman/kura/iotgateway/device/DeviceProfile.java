package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeviceProfile implements IDeviceProfile{

	@Override
	public boolean setName(String name) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setType(DeviceType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setUUID(UUID uuid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
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
	public boolean getDescription(String description) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DeviceType getType(DeviceType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UUID getUUID(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
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

}
