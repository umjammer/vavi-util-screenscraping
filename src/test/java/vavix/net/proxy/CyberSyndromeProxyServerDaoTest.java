/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.net.proxy;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import vavix.net.proxy.ProxyChanger.InternetAddress;


/**
 * CyberSyndromeProxyServerDaoTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/03/06 umjammer initial version <br>
 */
public class CyberSyndromeProxyServerDaoTest {

    @Test
    @Disabled("slow")
    public void test() {
        CyberSyndromeProxyServerDao proxyServerDao = new CyberSyndromeProxyServerDao();
        List<InternetAddress> proxies = proxyServerDao.getProxyInetSocketAddresses();
proxies.forEach(System.err::println);
    }

    /** */
    public static void main(String[] args) throws Exception {
        CyberSyndromeProxyServerDao proxyServerDao = new CyberSyndromeProxyServerDao();
        for (InternetAddress proxyAddress : proxyServerDao.getProxyInetSocketAddresses()) {
            System.err.println("proxy: " + proxyAddress);
        }
    }
}

/* */
