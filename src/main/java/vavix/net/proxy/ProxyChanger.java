/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.net.proxy;

import java.util.List;
import java.util.Random;


/**
 * ProxyChanger.
 * <li> TODO {@link java.net.ProxySelector}
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 071004 nsano initial version <br>
 */
public class ProxyChanger {

    /** */
    private ProxyServerDao proxyServerDao;

    /** */
    public void setProxyServerDao(ProxyServerDao proxyServerDao) {
        this.proxyServerDao = proxyServerDao;
    }

    /** */
    private final Random random = new Random(System.currentTimeMillis());

    /** TODO ProxyInternetAddress で bad or quality */
    public static class InternetAddress {
        protected String hostName;
        protected int port;
        /** */
        InternetAddress() {
        }
        /** */
        InternetAddress(String hostName, int port) {
            this.hostName = hostName;
            this.port = port;
        }
        /** */
        public String getHostName() {
            return hostName;
        }
        /** */
        public void setHostName(String hostName) {
            this.hostName = hostName;
        }
        /** */
        public int getPort() {
            return port;
        }
        /** */
        public void setPort(int port) {
            this.port = port;
        }
        /** */
        public String toString() {
            return hostName + ":" + port;
        }
    }

    /** */
    public InternetAddress getInetSocketAddress() {
        List<InternetAddress> proxyAddresses = proxyServerDao.getProxyInetSocketAddresses();
        int max = proxyAddresses.size();
        int current = random.nextInt(max);
        InternetAddress proxyAddress = proxyAddresses.get(current);
//logger.log(Level.DEBUG, "proxyAddress: " + proxyAddress + " (" + current + "/" + max + ")");
        return proxyAddress;
    }
}
