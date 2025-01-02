/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.net.proxy;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import vavix.net.proxy.ProxyChanger.InternetAddress;


/**
 * ProxyChangerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/03/09 umjammer initial version <br>
 */
public class ProxyChangerTest {

    @Test
    @DisabledIfEnvironmentVariable(named = "GITHUB_WORKFLOW", matches = ".*")
    public void test() throws Exception {
        main(new String[0]);
    }

    /** */
    public static void main(String[] args) throws Exception {
        ProxyChanger proxyChanger = new ProxyChanger();
        ProxyServerDao proxyServerDao = new CyberSyndromeProxyServerDao();
        proxyChanger.setProxyServerDao(proxyServerDao);

//        while (proxyServerDao.getProxyInetSocketAddresses().size() < 5) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                System.err.println(e);
//            }
//        }

        List<InternetAddress> proxyAddresses = proxyServerDao.getProxyInetSocketAddresses();
        for (InternetAddress proxyAddress : proxyAddresses) {
            System.err.println("proxy: " + proxyAddress);
        }
    }
}
