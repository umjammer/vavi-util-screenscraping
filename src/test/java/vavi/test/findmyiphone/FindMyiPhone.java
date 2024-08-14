/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.test.findmyiphone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * FindMyiPhone. find my iphone [obsolete]
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/08/10 umjammer initial version <br>
 */
@SuppressWarnings("deprecation")
@PropsEntity(url = "file://${user.dir}/local.properties")
public class FindMyiPhone {

    @Property(name = "test.fmi.email")
    String email;
    @Property(name = "test.fmi.password")
    String password;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        FindMyiPhone app = new FindMyiPhone();
        PropsEntity.Util.bind(app);
        System.exit(0);
        List<JsonNode> locations = getDeviceLocations(app.email, app.password);
        for (JsonNode node : locations) {
            System.err.println(node);
        }
    }

    private static List<JsonNode> getDeviceLocations(String username, String password) throws IOException {
        // Initialize the HTTP driver
        DefaultHttpClient hc = new DefaultHttpClient();

        // Authorize with Apple's mobile me service
        HttpGet auth = new HttpGet("https://auth.apple.com/authenticate?service=DockStatus&realm=primary-me&formID=loginForm&username="
                                   + username
                                   + "&password="
                                   + password
                                   + "&returnURL=aHR0cHM6Ly9zZWN1cmUubWUuY29tL3dvL1dlYk9iamVjdHMvRG9ja1N0YXR1cy53b2Evd2EvdHJhbXBvbGluZT9kZXN0aW5hdGlvblVybD0vYWNjb3VudA%3D%3D");
        hc.execute(auth);
        auth.abort();

        // Pull the isc-secure.me.com cookie out so we can set the X-Mobileme-Isc header properly
        String isc = extractIscCode(hc);

        // Get access to the devices and find out their ids
        HttpPost devicemgmt = new HttpPost("https://secure.me.com/wo/WebObjects/DeviceMgmt.woa/?lang=en");
        devicemgmt.addHeader("X-Mobileme-Version", "1.0");
        devicemgmt.addHeader("X-Mobileme-Isc", isc);
        devicemgmt.setEntity(new UrlEncodedFormEntity(new ArrayList<NameValuePair>()));
        HttpResponse devicePage = hc.execute(devicemgmt);

        // Extract the device ids from their html encoded in json
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        devicePage.getEntity().writeTo(os);
        os.close();
        Matcher m = Pattern.compile("DeviceMgmt.deviceIdMap\\['[0-9]+'\\] = '([a-z0-9]+)';")
                .matcher(os.toString());
        List<String> deviceList = new ArrayList<>();
        while (m.find()) {
            deviceList.add(m.group(1));
        }

        List<JsonNode> results = new ArrayList<>();

        // For each available device, get the location
        JsonFactory jf = new JsonFactory();
        for (String device : deviceList) {
            HttpPost locate = new HttpPost("https://secure.me.com/wo/WebObjects/DeviceMgmt.woa/wa/LocateAction/locateStatus");
            locate.addHeader("X-Mobileme-Version", "1.0");
            locate.addHeader("X-Mobileme-Isc", isc);
            locate.setEntity(new StringEntity("postBody={\"deviceId\": \"" + device + "\", \"deviceOsVersion\": \"7A341\"}"));
            locate.setHeader("Content-type", "application/json");
            HttpResponse location = hc.execute(locate);
            InputStream inputStream = location.getEntity().getContent();
            JsonParser jp = jf.createJsonParser(inputStream);
            jp.nextToken(); // ugly

            results.add(jp.readValueAsTree());

            inputStream.close();
        }

        return results;
    }

    private static String extractIscCode(DefaultHttpClient hc) {
        CookieStore cookies = hc.getCookieStore();
        for (Cookie cookie : cookies.getCookies()) {
            if (cookie.getName().equals("isc-secure.me.com")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
