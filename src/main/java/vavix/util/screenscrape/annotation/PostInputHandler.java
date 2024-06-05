/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.logging.Level;

import vavi.util.Debug;


/**
 * PostInputHandler.
 * <p>
 * strings "{args_index}" specified in {@link WebScraper#url()} at {@link WebScraper} will be replaced by `args` by its order.
 * </p>
 * @see PostInputHandler#dealUrlAndArgs(String, String...)
 *
 * TODO auto create body
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/01 nsano initial version <br>
 */
public class PostInputHandler implements InputHandler<InputStream> {

    /**
     * @param args 0: url, 1: body, 2: content-type, 3...: {@link WebScraper.Util#scrape(Class, String...)}'s args or
     *             {@link WebScraper.Util#foreach(Class, java.util.function.Consumer, String...)}'s args.
     */
    public InputStream getInput(String ... args) throws IOException {
        String url = args[0];
        String body = dealBodyAndArgs(args[1], Arrays.copyOfRange(args, 3, args.length));
        String contentType = args[2];
        String userAgent = System.getProperty("vavix.util.screenscrape.annotation.PostInputHandler.userAgent");
Debug.println(Level.INFO, "url: " + url);
Debug.println(Level.INFO, "body: " + body);
Debug.println(Level.INFO, "contentType: " + contentType);
Debug.println(Level.INFO, "userAgent: " + userAgent);
        URLConnection connection = new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", contentType);
        if (userAgent != null) {
            connection.setRequestProperty("User-Agent", userAgent);
        }

        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
        writer.write(body);
        writer.flush();
        writer.close();
if (connection instanceof HttpURLConnection) {
 Debug.println(Level.FINE, "responseCode: " + ((HttpURLConnection) connection).getResponseCode());
}
        InputStream is = connection.getInputStream();
//System.err.println(StringUtil.getDump(baos.toByteArray()));
        // CAUTION!!! InputStreamReader depends on `-Dfile.encoding`.
        return new BufferedInputStream(is);
    }

    /**
     * args will be embedded in url.
     * @param args 0: body, 1: content-type, 2... (will be url-encoded)
     * @return 0: url, 1: body, 2: content-type, 3...
     */
    public String[] dealUrlAndArgs(String url, String ... args) {
        String[] tmp = InputHandler._dealUrlAndArgs(url, Arrays.copyOfRange(args, 2, args.length));
        String[] result = new String[args.length + 1];
        result[0] = tmp[0];
        System.arraycopy(args, 0, result, 1, args.length);
        return result;
    }

    /**
     * @param args will be embedded just as it is.
     */
    private static String dealBodyAndArgs(String body, String... args) {
        if (body != null && !body.isEmpty()) {
            int c = 0;
            for (String arg : args) {
                body = body.replace("{" + c + "}", arg);
                c++;
            }
            return body;
        } else {
            throw new IllegalArgumentException("body should not be null or empty");
        }
    }
}
