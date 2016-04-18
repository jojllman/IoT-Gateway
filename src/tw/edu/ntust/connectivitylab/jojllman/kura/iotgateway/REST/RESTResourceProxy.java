package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.REST;

import java.io.Serializable;
import javax.ejb.Local;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path( "/demo-business-resource" )
public interface RESTResourceProxy extends Serializable {

    @POST
    @Path( "/login" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response login(
            @Context HttpHeaders httpHeaders,
            @FormParam( "username" ) String username,
            @FormParam( "password" ) String password );

    @GET
    @Path( "/demo-get-method" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response demoGetMethod();

    @POST
    @Path( "/demo-post-method" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response demoPostMethod();

    @POST
    @Path( "/logout" )
    public Response logout(
            @Context HttpHeaders httpHeaders
    );
}
