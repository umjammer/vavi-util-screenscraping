/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.test.findmyiphone;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 * FindMyiPhone2. find my iphone
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/08/10 umjammer initial version <br>
 */
public class FindMyiPhone2 {

    private static final String DeviceUUID = "0000000000000000000000000000000000000000";
    private static final String AppName = "FindMyiPhone";
    private static final String AppVersion = "1.4";
    private static final String BuildVersion = "145";

    private String partition = null;
    private final Map<String, Device> devices = new HashMap<>();
    private final String username;
    private final String password;

    /**
     * @param username iCloud account
     * @param password iCloud password
     */
    public FindMyiPhone2(String username, String password) throws IOException {
        this.username = username;
        this.password = password;
        getPartition();
        updateDevices();
    }

    /**
     * @param name device name
     */
    public Device getDevice(String name) {
        return devices.get(name);
    }

    /** */
    private void getPartition() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ObjectNode body = root.putObject("clientContext");
        body.put("appName", AppName);
        body.put("appVersion", AppVersion);
        body.put("buildVersion", BuildVersion);
        body.put("deviceUDID", DeviceUUID);
        body.put("inactiveTime", 2147483647);
        body.put("osVersion", "4.2.1");
        body.put("personID", 0);
        body.put("productType", "iPad1,1");

        HttpResponse response = post(String.format("/fmipservice/device/%s/initClient", username), root.toString());
        if (response.getHeaders("X-Apple-MMe-Host") != null && response.getHeaders("X-Apple-MMe-Host").length > 0) {
            this.partition = response.getHeaders("X-Apple-MMe-Host")[0].getValue();
System.err.println(this.partition);
        }
    }

    /** */
    private void updateDevices() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ObjectNode body = root.putObject("clientContext");
        body.put("appName", AppName);
        body.put("appVersion", AppVersion);
        body.put("buildVersion", BuildVersion);
        body.put("deviceUDID", DeviceUUID);
        body.put("inactiveTime", 2147483647);
        body.put("osVersion", "4.2.1");
        body.put("personID", 0);
        body.put("productType", "iPad1,1");

        HttpResponse response = post(String.format("/fmipservice/device/%s/initClient", username), root.toString());
        JsonNode node = mapper.readTree(response.getEntity().getContent());
//System.err.println(node);

        String statusCode = node.get("statusCode").asText();
//System.err.println(statusCode);
        if (!statusCode.equals("200")) {
            throw new IOException("statusCode: " + statusCode);
        }
        JsonNode contents = node.get("content");
//System.err.println(contents.size());
        for (JsonNode content : contents) {
            Device device = new Device();
//System.err.println(content);
            JsonNode location = content.get("location");
            if (!location.isNull()) {
                device.locationTimestamp = location.get("timeStamp").asLong();
                device.locationType = location.get("positionType").asText();
                device.horizontalAccuracy = location.get("horizontalAccuracy").asDouble();
                device.locationFinished = location.get("locationFinished").asBoolean();
                device.longitude = location.get("longitude").asDouble();
                device.latitude = location.get("latitude").asDouble();
            }
            device.isLocating = content.get("isLocating").asBoolean();
            device.deviceModel = content.get("deviceModel").asText();
            device.deviceStatus = content.get("deviceStatus").asText();
            device.id = content.get("id").asText();
            device.name = content.get("name").asText();
            device.deviceClass = content.get("deviceClass").asText();
            device.chargingStatus = content.get("batteryStatus").asText();
            device.batteryLevel = content.get("batteryLevel").asDouble();

            devices.put(device.name, device);
            System.err.println(device);
        }
    }

    /** */
    private HttpResponse post(String url, String body) throws IOException {
        if (this.partition != null) {
            url = "https://" + this.partition + url;
        } else {
            url = "https://fmipmobile.icloud.com" + url;
        }

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        post.addHeader("Content-type", "application/json; charset=utf-8");
        post.addHeader("X-Apple-Find-Api-Ver", "2.0");
        post.addHeader("X-Apple-Authscheme", "UserIdGuest");
        post.addHeader("X-Apple-Realm-Support", "1.0");
        post.addHeader("User-agent", "Find iPhone/1.2 MeKit (iPad: iPhone OS/4.2.1)");
        post.addHeader("X-Client-Name", "iPad");
        post.addHeader("X-Client-UUID", "0cf3dc501ff812adb0b202baed4f37274b210853");
        post.addHeader("Accept-Language", "en-us");
        post.addHeader("Connection", "keep-alive");
        String auth = Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).getBytes()).trim();
//        System.err.println(auth);
        post.addHeader("Authorization", String.format("Basic %s", auth));

System.err.println("body: " + body);
        post.setEntity(new StringEntity(body, "UTF-8"));

        HttpResponse response = client.execute(post);
//System.err.println("post: " + response.getStatusLine());
        int code = response.getStatusLine().getStatusCode();
        if (code == 330) {
            code = 204;
        }
        // According to RFC 2616, "2xx" code indicates that the driver"s
        // request was successfully received, understood, and accepted.
        if (200 > code || code >= 300) {
            throw new IOException(response.getStatusLine().toString());
        }

        return response;
    }

    /** */
    public class Device {

        String id;
        String name;

        boolean locationFinished;
        double latitude;
        double longitude;
        double horizontalAccuracy;
        long locationTimestamp;
        double batteryLevel;
        String chargingStatus;
        String deviceClass;
        String deviceStatus;
        String deviceModel;
        boolean isLocating;
        String locationType;

        /**
         * @param maxWait mill seconds
         */
        public void locate(int maxWait) throws IOException {
            long start = System.currentTimeMillis();

            while (!locationFinished) {
                if (System.currentTimeMillis() - start > maxWait) {
                    throw new IOException(String.format("Unable to find location within \"%d\" seconds", maxWait));
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                updateDevices();
            }
        }

        /** */
        public void sendMessage(String text, boolean sound, String subject) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            ObjectNode body = root.putObject("clientContext");
            body.put("appName", AppName);
            body.put("appVersion", AppVersion);
            body.put("buildVersion", BuildVersion);
            body.put("deviceUDID", DeviceUUID);
            body.put("inactiveTime", 5911);
            body.put("osVersion", "3.2");
            body.put("productType", "iPad1,1");
            body.put("selectedDevice", id);
            body.put("shouldLocate", false);
            root.put("device", id);
            body = root.putObject("serverContext");
            body.put("callbackIntervalInMS", 3000);
            body.put("clientId", DeviceUUID);
            body.put("deviceLoadStatus", "203");
            body.put("hasDevices", true);
            body.putNull("lastSessionExtensionTime");
            body.put("maxDeviceLoadTime", 60000);
            body.put("maxLocatingTime", 90000);
            body.put("preferredLanguage", "en");
            body.put("prefsUpdateTime", 1276872996660L);
            body.put("sessionLifespan", 900000);
            ObjectNode sub = body.putObject("timezone");
            sub.put("currentOffset", -25200000);
            sub.put("previousOffset", -28800000);
            sub.put("previousTransition", 1268560799999L);
            sub.put("tzCurrentName", "Pacific Daylight Time");
            sub.put("tzName", "America/Los_Angeles");
            body.put("validRegion", true);
            root.put("sound", sound);
            root.put("subject", subject);
            root.put("text", text);

            post(String.format("/fmipservice/device/%s/sendMessage", username), root.toString());
        }

        /**
         * @param notDryrun false means dry run
         */
        public void remoteLock(String passcode, boolean notDryrun) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            ObjectNode body = root.putObject("clientContext");
            body.put("appName", AppName);
            body.put("appVersion", AppVersion);
            body.put("buildVersion", BuildVersion);
            body.put("deviceUDID", DeviceUUID);
            body.put("inactiveTime", 5911);
            body.put("osVersion", "3.2");
            body.put("productType", "iPad1,1");
            body.put("selectedDevice", id);
            body.put("shouldLocate", false);
            root.put("device", id);
            root.put("oldPasscode", "");
            root.put("passcode", passcode);
            body = root.putObject("serverContext");
            body.put("callbackIntervalInMS", 3000);
            body.put("clientId", DeviceUUID);
            body.put("deviceLoadStatus", "203");
            body.put("hasDevices", true);
            body.putNull("lastSessionExtensionTime");
            body.put("maxDeviceLoadTime", 60000);
            body.put("maxLocatingTime", 90000);
            body.put("preferredLanguage", "en");
            body.put("prefsUpdateTime", 1276872996660L);
            body.put("sessionLifespan", 900000);
            ObjectNode sub = body.putObject("timezone");
            sub.put("currentOffset", -25200000);
            sub.put("previousOffset", -28800000);
            sub.put("previousTransition", 1268560799999L);
            sub.put("tzCurrentName", "Pacific Daylight Time");
            sub.put("tzName", "America/Los_Angeles");
            body.put("validRegion", true);

            if (notDryrun) {
                post(String.format("/fmipservice/device/%s/remoteLock", username), root.toString());
            }
        }

        /**
         * @param notDryrun false means dry run
         */
        public void remoteWipe(boolean notDryrun) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            ObjectNode body = root.putObject("clientContext");
            body.put("appName", AppName);
            body.put("appVersion", AppVersion);
            body.put("buildVersion", BuildVersion);
            body.put("deviceUDID", DeviceUUID);
            body.put("inactiveTime", 5911);
            body.put("osVersion", "3.2");
            body.put("productType", "iPad1,1");
            body.put("selectedDevice", id);
            body.put("shouldLocate", false);
            root.put("device", id);
            body = root.putObject("serverContext");
            body.put("callbackIntervalInMS", 3000);
            body.put("clientId", DeviceUUID);
            body.put("deviceLoadStatus", "203");
            body.put("hasDevices", true);
            body.putNull("lastSessionExtensionTime");
            body.put("maxDeviceLoadTime", 60000);
            body.put("maxLocatingTime", 90000);
            body.put("preferredLanguage", "en");
            body.put("prefsUpdateTime", 1276872996660L);
            body.put("sessionLifespan", 900000);
            ObjectNode sub = body.putObject("timezone");
            sub.put("currentOffset", -25200000);
            sub.put("previousOffset", -28800000);
            sub.put("previousTransition", 1268560799999L);
            sub.put("tzCurrentName", "Pacific Daylight Time");
            sub.put("tzName", "America/Los_Angeles");
            body.put("validRegion", true);

            if (notDryrun) {
                post(String.format("/fmipservice/device/%s/remoteWipe", username), root.toString());
            }
        }

        /* */
        public String toString() {
            String sb = "deviceStatus: " +
                    deviceStatus +
                    ", " +
                    "name: " +
                    name +
                    ", " +
                    "deviceClass: " +
                    deviceClass +
                    ", " +
                    "deviceModel: " +
                    deviceModel +
                    ", " +
                    "latitude: " +
                    latitude +
                    ", " +
                    "longitude: " +
                    longitude +
                    ", " +
                    "horizontalAccuracy: " +
                    horizontalAccuracy +
                    ", " +
                    "locationTimestamp: " +
                    new Date(locationTimestamp) +
                    ", " +
                    "batteryLevel: " +
                    batteryLevel +
                    ", " +
                    "chargingStatus: " +
                    chargingStatus +
                    ", " +
                    "isLocating: " +
                    isLocating +
                    ", " +
                    "locationType: " +
                    locationType +
                    ", " +
                    "locationFinished: " +
                    locationFinished +
                    ", " +
                    "id: " +
                    id;

            return sb;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        FindMyiPhone2 app = new FindMyiPhone2(args[0], args[1]);
        Device device = app.getDevice("nsanoi8");
//        device.remoteLock("test", false);
//        device.remoteWipe(false);
//        device.sendMessage("何人のもん取っとんじゃ(#゜Д゜)ゴルァ!!", true, "日本民事警察");
//        device.sendMessage("Heart Beat Check!", false, "Vavi");
        device.locate(10000);
    }
}
