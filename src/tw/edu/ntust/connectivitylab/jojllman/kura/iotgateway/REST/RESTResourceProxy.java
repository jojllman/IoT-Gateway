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

    @GET
    @Path( "/query-user-list" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response queryUserList(@Context HttpHeaders httpHeaders);

    @GET
    @Path( "/query-group-list" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response queryGroupList(@Context HttpHeaders httpHeaders);

    @GET
    @Path( "/query-channel-list" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response queryChannelList(@Context HttpHeaders httpHeaders);

    @GET
    @Path( "/query-device-list" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response queryDeviceList(@Context HttpHeaders httpHeaders);

    @GET
    @Path( "/query-event-list" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response queryEventList(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/write-channel-data" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response writeChannelData(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/add-user" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response addUser(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/remove-user" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response removeUser(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/add-group" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response addGroup(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/remove-group" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response removeGroup(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/set-user-group" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response setUserGroup(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/set-device-owner" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response setDeviceOwner(@Context HttpHeaders httpHeaders);

    @GET
    @Path( "/get-device-owner" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response getDeviceOwner(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/set-channel-owner" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response setChannelOwner(@Context HttpHeaders httpHeaders);

    @GET
    @Path( "/get-channel-owner" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response getChannelOwner(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/set-channel-group" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response setChannelGroup(@Context HttpHeaders httpHeaders);

    @GET
    @Path( "/get-channel-group" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response getChannelGroup(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/add-event" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response addEvent(@Context HttpHeaders httpHeaders);

    @GET
    @Path( "/remove-event" )
    @Produces (MediaType.APPLICATION_JSON )
    public Response removeEvent(@Context HttpHeaders httpHeaders);

    @POST
    @Path( "/logout" )
    public Response logout(
            @Context HttpHeaders httpHeaders
    );
}
