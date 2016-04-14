package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.DeviceManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel;

public class EventManager {
	private static final Logger s_logger = LoggerFactory.getLogger(EventManager.class);
	private static EventManager instance;
	public static EventManager getInstance() { return instance; }
	
	private DeviceManager deviceManager;
	
	public EventManager() {
		s_logger.debug("Event manager started.");
	}
	
	public void setDeviceManager(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
	
	public Map<String, TopicChannel<?>> geTopicChannels(List<String> channelStrings) {
		Map<String, TopicChannel<?>> map = new HashMap<>();
		List<IDeviceProfile> profiles = deviceManager.getDeviceProfiles();
		boolean escape;
		for(String ch : channelStrings) {
			escape = false;
			for(IDeviceProfile profile : profiles) {
				List<TopicChannel<?>> channels = profile.getChannels();
				for(TopicChannel<?> channel : channels) {
					if(ch.compareTo(channel.getId()) == 0) {
						map.put(ch, channel);
						escape = true;
						break;
					}
				}
				if(escape)
					break;
			}
		}
		
		if(map.size() == 0)
			return null;
		
		return map;
	}
}
