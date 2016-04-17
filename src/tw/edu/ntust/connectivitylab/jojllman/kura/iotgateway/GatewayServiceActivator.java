package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.AccessControlManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.DeviceManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.discovery.DeviceDiscovery;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.event.EventManager;

import java.util.Dictionary;
import java.util.Hashtable;

public class GatewayServiceActivator {
	private static final Logger s_logger = LoggerFactory.getLogger(GatewayServiceActivator.class);
    private static final String APP_ID = "tw.edu.ntust.connectivitylab.jojllman.kura.GatewayServiceActivator";
    
    private DeviceDiscovery m_deviceDiscovery;
	private DeviceManager m_deviceManager;
	private AccessControlManager m_accessManager;
	private EventManager m_eventManager;
	private BundleContext _context;
	private ServiceTracker _tracker;
	private final String _path = "/test";

    protected void activate(ComponentContext componentContext) {
        s_logger.info("Bundle " + APP_ID + " has started!");
        s_logger.debug(APP_ID + ": This is a debug message.");

		_context = componentContext.getBundleContext();
		_tracker = new ServiceTracker(
				_context,
				HttpService.class.getName(),
				new ServiceTrackerCustomizer() {
					public Object addingService(ServiceReference serviceReference) {
						try {
							s_logger.debug(serviceReference.getBundle().getSymbolicName());
							HttpService service = (HttpService)_context.getService(serviceReference);
							Dictionary<String, String> initParams = new Hashtable<String, String>();
							initParams.put("javax.ws.rs.Application", SampleApplication.class.getName());
							service.registerServlet(_path, new SampleServlet(), initParams, null);
							return service;
						} catch (Exception ex) {
							ex.printStackTrace();
							throw new RuntimeException(ex);
						}
					}

					public void modifiedService(ServiceReference serviceReference, Object o) {

					}

					public void removedService(ServiceReference serviceReference, Object o) {
						HttpService service = (HttpService)_context.getService(serviceReference);
						if (service != null) {
							service.unregister(_path);
						}
					}
				}
		);
		_tracker.open();

        final String topic        = "MQTT Examples";
        final String content      = "Message from MqttPublishSample";
        final int qos             = 2;
        String broker       = "tcp://iot.eclipse.org:1883";
        String clientId     = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
//            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
//            connOpts.setCleanSession(true);
//            System.out.println("Connecting to broker: "+broker);
//            sampleClient.connect(connOpts);
//            System.out.println("Connected");
//            System.out.println("Publishing message: "+content);
//            MqttMessage message = new MqttMessage(content.getBytes());
//            message.setQos(qos);
//            sampleClient.publish(topic, message);
//            System.out.println("Message published");
//            sampleClient.disconnect();
//            System.out.println("Disconnected");
            
            final MqttAsyncClient asyncClient = new MqttAsyncClient(broker, clientId, persistence);
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            asyncClient.connect(connOpts, null, new IMqttActionListener() {
				
				@Override
				public void onSuccess(IMqttToken arg0) {
					System.out.println("Publish completed: " + arg0);
					try {
						System.out.println("Connected");
			            System.out.println("Publishing message: "+content);
			            MqttMessage message = new MqttMessage(content.getBytes());
			            message.setQos(qos);
			            asyncClient.publish(topic, message);
			            System.out.println("Message published");
			            
			            new Thread(new Runnable() {
							
							@Override
							public void run() {
								try {
									Thread.sleep(2000);
									asyncClient.disconnect(null, new IMqttActionListener() {
										
										@Override
										public void onSuccess(IMqttToken arg0) {
											System.out.println("Disconnected");
										}
										
										@Override
										public void onFailure(IMqttToken arg0, Throwable arg1) {
											System.out.println("Disconnect failed");
										}
									});
								} catch (MqttException | InterruptedException e) {
									e.printStackTrace();
								}
							}
						}).start();;
					} catch(MqttException me) {
			            System.out.println("reason "+me.getReasonCode());
			            System.out.println("msg "+me.getMessage());
			            System.out.println("loc "+me.getLocalizedMessage());
			            System.out.println("cause "+me.getCause());
			            System.out.println("excep "+me);
			            me.printStackTrace();
			        }
				}
				
				@Override
				public void onFailure(IMqttToken arg0, Throwable arg1) {
					System.out.println("Publish failed: " + arg0);
				}
			});
            
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
        
        m_deviceDiscovery = new DeviceDiscovery();
		m_deviceManager = new DeviceManager();
		m_accessManager = new AccessControlManager();
		m_eventManager = new EventManager();

		m_deviceManager.setAccessControlManager(m_accessManager);
		m_eventManager.setDeviceManager(m_deviceManager);

        try {
        	m_deviceDiscovery.startDiscovery();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    protected void deactivate(ComponentContext componentContext) {
		_tracker.remove(_tracker.getServiceReference());
    	m_deviceDiscovery.stopDiscovery();
		m_deviceDiscovery = null;
		m_deviceManager = null;
		m_accessManager = null;
		m_eventManager = null;

        s_logger.info("Bundle " + APP_ID + " has stopped!");
    }
    
    protected void update(ComponentContext componentContext) {
		m_deviceDiscovery.stopDiscovery();
		m_deviceDiscovery = null;
		m_deviceManager = null;
		m_accessManager = null;
		m_eventManager = null;

        s_logger.info("Bundle " + APP_ID + " has updated!");
    }
}
