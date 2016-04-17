package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class SampleResource {

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSampleText() {
        return "My Sample Text";
    }

}
