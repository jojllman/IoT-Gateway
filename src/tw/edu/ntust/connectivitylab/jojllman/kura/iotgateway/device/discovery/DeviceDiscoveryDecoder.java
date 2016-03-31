package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.discovery;

import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class DeviceDiscoveryDecoder extends ByteToMessageDecoder {
	private static final Logger s_logger = LoggerFactory.getLogger(DeviceDiscoveryDecoder.class);
	static enum DecodeState {
			none, decoding
	}
	static final int BYTES_LENGTH = Integer.SIZE / 8;
	
	private DecodeState currentState = DecodeState.none;
	private int bodyLength = 0;
	
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
	    	if(currentState == DecodeState.none) {
		        if (in.readableBytes() < (BYTES_LENGTH)) {
		            return;
		        }
		        
		        bodyLength = in.readInt();
		        currentState = DecodeState.decoding;
	    	}
	    	else {
	    		if (in.readableBytes() < bodyLength) {
		            return;
		        }
	    		
	    		String parseString = in.readBytes(bodyLength).toString();
	    		s_logger.debug(parseString);
	    		JSONObject json = new JSONObject(parseString);
	    		out.add(json);
	    		
	    		currentState = DecodeState.none;
	    	}
    }
}