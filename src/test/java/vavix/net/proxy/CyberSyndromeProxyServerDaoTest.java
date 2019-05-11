/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.net.proxy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import vavix.net.proxy.ProxyChanger.InternetAddress;


/**
 * CyberSyndromeProxyServerDaoTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/03/06 umjammer initial version <br>
 */
public class CyberSyndromeProxyServerDaoTest {

    @Test
    public void test() {
        fail("Not yet implemented");
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
