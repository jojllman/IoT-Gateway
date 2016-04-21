package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.REST;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.*;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.DeviceManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.event.Event;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.event.EventManager;

import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
                        .add("user_name", user.getUsername())
                        .add("user_id", user.getUserId()));
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
                        .add("group_name", group.getGroupName())
                        .add("group_id", group.getGroupId()));
            }

            jsonObjBuilder.add("groups", jsonArrayBuilder);
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }
        return null;
    }

    @Override
    public Response queryChannelList(HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response queryDeviceList(HttpHeaders httpHeaders) {
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
                        .add("channel_num", device.getChannels().size()));
            }
        } else {
            AccessControlManager accessControlManager = AccessControlManager.getInstance();
            for (IDeviceProfile device : devices) {
                if (accessControlManager.canUserReadDevice(caller, device)) {
                    jsonArrayBuilder.add(Json.createObjectBuilder()
                            .add("name", device.getName())
                            .add("id", device.getId())
                            .add("description", device.getDescription())
                            .add("type", device.getType().toString())
                            .add("uuid", device.getUUID().toString())
                            .add("protocol", device.getDataExchangeProtocol().toString())
                            .add("channel_num", device.getChannels().size()));
                }
            }
        }
        jsonObjBuilder.add("devices", jsonArrayBuilder);
        JsonObject jsonObj = jsonObjBuilder.build();
        return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
    }

    @Override
    public Response queryEventList(HttpHeaders httpHeaders) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        if (caller.isAdministrator()) {
            Map<String, List<Event>> userEvents = EventManager.getInstance().getAllEvents();
            Iterator<Map.Entry<String, List<Event>>> it = userEvents.entrySet().iterator();
            while (it.hasNext()) {
                JsonArrayBuilder eventJsonArrayBuilder = Json.createArrayBuilder();
                Map.Entry<String, List<Event>> entry = it.next();
                String userid = entry.getKey();
                User user = UserManager.getInstance().findUserById(userid);
                List<Event> events = entry.getValue();
                for (Event event : events) {
                    eventJsonArrayBuilder.add(Json.createObjectBuilder()
                            .add("name", event.getEventId())
                            .add("if", event.getIfString())
                            .add("then", event.getThenString()));
                }
                jsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("user_id", userid)
                        .add("events", eventJsonArrayBuilder));
            }
        } else {
            List<Event> events = EventManager.getInstance().getUserEvents(caller.getUserId());
            JsonArrayBuilder eventJsonArrayBuilder = Json.createArrayBuilder();
            for (Event event : events) {
                eventJsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("name", event.getEventId())
                        .add("if", event.getIfString())
                        .add("then", event.getThenString()));
            }
            jsonArrayBuilder.add(Json.createObjectBuilder()
                    .add("user_id", caller.getUserId())
                    .add("events", eventJsonArrayBuilder));
        }
        jsonObjBuilder.add("user_events", jsonArrayBuilder);
        JsonObject jsonObj = jsonObjBuilder.build();
        return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
    }

    @Override
    public Response writeChannelData(HttpHeaders httpHeaders, String channelId, String type, String value) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));
        TopicChannel channel = DeviceManager.getInstance().findChannelById(channelId);
        if(channel == null || caller == null)
            return null;
        if(channel.getType() != TopicChannel.ChannelDataType.valueOf(type))
            return null;
        if(!AccessControlManager.getInstance().canUserWriteChannel(caller, channel))
            return null;

        switch (channel.getType()) {
            case Boolean:
                channel.setValue(Boolean.valueOf(value));
                break;
            case Short:
                channel.setValue(Short.valueOf(value));
                break;
            case Integer:
                channel.setValue(Integer.valueOf(value));
                break;
            case String:
                channel.setValue(value);
                break;
            default:
                return null;
        }

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "value has changed");
        jsonObjBuilder.add("value", channel.getValue().toString());
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
    }

    @Override
    public Response readChannelData(HttpHeaders httpHeaders, String channelId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));
        TopicChannel channel = DeviceManager.getInstance().findChannelById(channelId);
        if(channel == null || caller == null)
            return null;
        if(!AccessControlManager.getInstance().canUserReadChannel(caller, channel))
            return null;

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "value has acquired");
        jsonObjBuilder.add("value", channel.getValue().toString());
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
    }

    @Override
    public Response addUser(HttpHeaders httpHeaders) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        UserManager manager = UserManager.getInstance();
        manager.addUser();

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "user has been added");
        jsonObjBuilder.add("user_name", );
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
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
