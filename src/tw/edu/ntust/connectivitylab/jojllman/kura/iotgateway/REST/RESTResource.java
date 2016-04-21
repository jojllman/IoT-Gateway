package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.REST;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.*;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.DeviceManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile;

import java.security.GeneralSecurityException;
import java.util.List;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.security.auth.login.LoginException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Stateless
public class RESTResource implements RESTResourceProxy {
    private static final long serialVersionUID = -6663599014192066936L;

    @Override
    public Response login(
            HttpHeaders httpHeaders,
            String username,
            String password) {

        Authenticator authenticator = Authenticator.getInstance();
        String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);

        try {
            String authToken = authenticator.login(serviceKey, username, password);

            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("auth_token", authToken);
            JsonObject jsonObj = jsonObjBuilder.build();

            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();

        } catch (final LoginException ex) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "Problem matching service key, username and password");
            JsonObject jsonObj = jsonObjBuilder.build();

            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
        }
    }

    @Override
    public Response demoGetMethod() {
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "Executed demoGetMethod");
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
    }

    @Override
    public Response demoPostMethod() {
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "Executed demoPostMethod");
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
    }

    @Override
    public Response queryUserList(HttpHeaders httpHeaders) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if (caller.isAdministrator()) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            List<User> users = UserManager.getInstance().getAllUser();
            for (User user : users) {
                jsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("username", user.getUsername())
                        .add("id", user.getUserId()));
            }

            jsonObjBuilder.add("users", jsonArrayBuilder);
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }
        return null;
    }

    @Override
    public Response queryGroupList(HttpHeaders httpHeaders) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if (caller.isAdministrator()) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            List<Group> groups = GroupManager.getInstance().getAllGroup();
            for (Group group : groups) {
                jsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("groupname", group.getGroupName())
                        .add("id", group.getGroupId()));
            }

            jsonObjBuilder.add("groups", jsonArrayBuilder);
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }
        return null;
    }

    @Override
    public Response queryChannelList(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response queryDeviceList(@Context HttpHeaders httpHeaders) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        List<IDeviceProfile> devices = DeviceManager.getInstance().getDeviceProfiles();
        if (caller.isAdministrator()) {
            for (IDeviceProfile device : devices) {
                jsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("name", device.getName())
                        .add("id", device.getId())
                .add("description", device.getDescription())
                .add("type", device.getType().toString())
                .add("uuid", device.getUUID().toString())
                .add("protocol", device.getDataExchangeProtocol().toString())
                .add("channelnum", device.getChannels().size()));
            }
        }
        else {
            AccessControlManager accessControlManager = AccessControlManager.getInstance();
            for (IDeviceProfile device : devices) {
                boolean access = false;
                if(accessControlManager.getDeviceOwner(device) == caller) {
                    if(accessControlManager.getDeviceReadPermission(device, Permission.PermissionType.Own))
                        access = true;
                }
                if(accessControlManager.getDeviceGroup(device) == caller.getGroup()) {
                    if(accessControlManager.getDeviceReadPermission(device, Permission.PermissionType.Group))
                        access = true;
                }
                if(accessControlManager.getDeviceReadPermission(device, Permission.PermissionType.All))
                    access = true;

                if(access) {
                    jsonArrayBuilder.add(Json.createObjectBuilder()
                            .add("name", device.getName())
                            .add("id", device.getId())
                            .add("description", device.getDescription())
                            .add("type", device.getType().toString())
                            .add("uuid", device.getUUID().toString())
                            .add("protocol", device.getDataExchangeProtocol().toString())
                            .add("channelnum", device.getChannels().size()));
                }
            }
        }
        jsonObjBuilder.add("devices", jsonArrayBuilder);
        JsonObject jsonObj = jsonObjBuilder.build();
        return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
    }

    @Override
    public Response queryEventList(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response writeChannelData(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response addUser(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response removeUser(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response addGroup(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response removeGroup(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response setUserGroup(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response setDeviceOwner(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response getDeviceOwner(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response setChannelOwner(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response getChannelOwner(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response setChannelGroup(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response getChannelGroup(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response addEvent(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response removeEvent(@Context HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response logout(
            HttpHeaders httpHeaders) {
        try {
            Authenticator authenticator = Authenticator.getInstance();
            String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
            String authToken = httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN);

            authenticator.logout(serviceKey, authToken);

            return getNoCacheResponseBuilder(Response.Status.NO_CONTENT).build();
        } catch (final GeneralSecurityException ex) {
            return getNoCacheResponseBuilder(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status) {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setMaxAge(-1);
        cc.setMustRevalidate(true);

        return Response.status(status).cacheControl(cc);
    }
}
