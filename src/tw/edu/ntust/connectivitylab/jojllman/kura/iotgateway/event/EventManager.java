package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.AccessControlManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.DeviceManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel;

public class EventManager {
	private static final Logger s_logger = LoggerFactory.getLogger(EventManager.class);
	private static EventManager instance;
	public static EventManager getInstance() { return instance; }
	
	private DeviceManager deviceManager;
	private Map<String, List<Event>> userEvents;
	
	public EventManager() {
		userEvents = new HashMap<>();

		instance = this;
		s_logger.debug("Event manager started.");
	}
	
	public void setDeviceManager(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
	public Map<String, TopicChannel<?>> getTopicChannels(List<String> channelStrings) {
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

	public String addEvent(String userid, Event event) {
		List<Event> events = userEvents.get(userid);
		if(events == null)
			events = new ArrayList<>();

		event.setEventId(AccessControlManager.GetRandomEventId());
		events.add(event);
		return event.getEventId();
	}

	public boolean removeEvent(String userid, Event event) {
		List<Event> events = userEvents.get(userid);
		if(events == null)
			events = new ArrayList<>();

		events.add(event);
		return true;
	}

	public List<Event> getUserEvents(String userid) {
		return new ArrayList<>(userEvents.get(userid));
	}
	public Event getUserEvent(String userid, String eventId) {
		List<Event> events = userEvents.get(userid);
		for(Event event : events) {
			if(event.getEventId().compareTo(eventId) == 0)
				return event;
		}
		return null;
	}
	public boolean doesUserHasEvent(String userId, String eventId) {
		List<Event> events = userEvents.get(userId);
		if(events == null)
			return false;

		for(Event event : events) {
			if(event.getEventId().compareTo(eventId) == 0)
				return true;
		}

		return false;
	}

	public Map<String, List<Event>> getAllEvents() {
		return new HashMap<>(userEvents);
	}
}
