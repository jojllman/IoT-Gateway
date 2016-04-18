package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.REST;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.Authenticator;

import java.security.GeneralSecurityException;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.security.auth.login.LoginException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Stateless
public class RESTResource implements RESTResourceProxy {
    private static final long serialVersionUID = -6663599014192066936L;

    @Override
    public Response login(
            HttpHeaders httpHeaders,
            String username,
            String password ) {

        Authenticator authenticator = Authenticator.getInstance();
        String serviceKey = httpHeaders.getHeaderString( HTTPHeaderNames.SERVICE_KEY );

        try {
            String authToken = authenticator.login( serviceKey, username, password );

            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add( "auth_token", authToken );
            JsonObject jsonObj = jsonObjBuilder.build();

            return getNoCacheResponseBuilder( Response.Status.OK ).entity( jsonObj.toString() ).build();

        } catch ( final LoginException ex ) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add( "message", "Problem matching service key, username and password" );
            JsonObject jsonObj = jsonObjBuilder.build();

            return getNoCacheResponseBuilder( Response.Status.UNAUTHORIZED ).entity( jsonObj.toString() ).build();
        }
    }

    @Override
    public Response demoGetMethod() {
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add( "message", "Executed demoGetMethod" );
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder( Response.Status.OK ).entity( jsonObj.toString() ).build();
    }

    @Override
    public Response demoPostMethod() {
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add( "message", "Executed demoPostMethod" );
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder( Response.Status.ACCEPTED ).entity( jsonObj.toString() ).build();
    }

    @Override
    public Response logout(
            HttpHeaders httpHeaders ) {
        try {
            Authenticator authenticator = Authenticator.getInstance();
            String serviceKey = httpHeaders.getHeaderString( HTTPHeaderNames.SERVICE_KEY );
            String authToken = httpHeaders.getHeaderString( HTTPHeaderNames.AUTH_TOKEN );

            authenticator.logout( serviceKey, authToken );

            return getNoCacheResponseBuilder( Response.Status.NO_CONTENT ).build();
        } catch ( final GeneralSecurityException ex ) {
            return getNoCacheResponseBuilder( Response.Status.INTERNAL_SERVER_ERROR ).build();
        }
    }

    private Response.ResponseBuilder getNoCacheResponseBuilder( Response.Status status ) {
        CacheControl cc = new CacheControl();
        cc.setNoCache( true );
        cc.setMaxAge( -1 );
        cc.setMustRevalidate( true );

        return Response.status( status ).cacheControl( cc );
    }
}
