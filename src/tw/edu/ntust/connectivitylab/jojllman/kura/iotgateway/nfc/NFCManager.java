package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.nfc;

import com.pi4j.io.serial.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 * Created by jojllman on 2016/6/1.
 */


public class NFCManager {
    private static final Logger s_logger = LoggerFactory.getLogger(NFCManager.class);
    private static NFCManager instance = null;
    public static NFCManager getInstance() {
        return instance;
    }

    private Serial serial;
    private boolean started = false;

    public NFCManager(String port, int baudrate) {
        serial = SerialFactory.createInstance();

        serial.addListener(new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                String reuslt = event.getData();
                if(reuslt.contains("Success")) {
                    started = false;
                    s_logger.debug("NFC success!");
                }
                else if(reuslt.contains("NFC failed!")) {
                    started = false;
                    s_logger.debug("NFC failed!");
                }

                s_logger.debug(event.getData());
            }
        });

        try {
            serial.open(port, baudrate);
        }
        catch (SerialPortException ex) {
            ex.printStackTrace();
        }

        instance = this;
    }

    public void startNFC(String name, String ssid, String pass) {
        if(!started) {
            String result = String.format("%s|%s|%s|%s", name, ssid, pass, genKey());
            serial.write("SD\r\n");
            serial.write(result);
            serial.write("\r\n");
            serial.write("SNFC");
            started = true;
            s_logger.debug("Started NFC for 10 seconds");
        }
        else {
            s_logger.debug("Already started NFC, try again later.");
        }
    }

    private String genKey() {
        String text = "";
        String possible = "ABCDEF0123456789";

        for( int i=0; i < 32; i++ )
            text += possible.charAt((int) Math.round(Math.random() * possible.length()));

        return text;
    }
}
