/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.net.proxy;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import vavi.util.Debug;
import vavix.net.proxy.ProxyChanger.InternetAddress;
import vavix.util.screenscrape.annotation.HtmlXPathParser;
import vavix.util.screenscrape.annotation.InputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;
import vavix.util.selenium.SeleniumUtil;


/**
 * CyberSyndromeProxyServerDao.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 071004 nsano initial version <br>
 */
public class CyberSyndromeProxyServerDao implements ProxyServerDao {

    /** */
    public CyberSyndromeProxyServerDao() {
        try {
            updateProxyAddresses();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** */
    private List<InternetAddress> proxyAddresses = new ArrayList<>();

    @Override
    public List<InternetAddress> getProxyInetSocketAddresses() {
        return proxyAddresses;
    }

    // selenium
    public static class MyInput implements InputHandler<Reader> {
        WebDriver driver;
        {
            driver = new SeleniumUtil().getWebDriver();
        }

        @Override
        public Reader getInput(String ... args) throws IOException {
            driver.navigate().to("http://www.cybersyndrome.net/plr6.html");
            SeleniumUtil.waitFor(driver);

            return new StringReader(driver.findElement(By.tagName("html")).getAttribute("innerHTML"));
        }
    }

    @WebScraper(input = MyInput.class,
                value = "//TABLE/TBODY/TR",
                parser = HtmlXPathParser.class,
                encoding = "ISO_8859-1")
    public static class ProxyInternetAddress extends InternetAddress {
        /** */
        @Target("/TR/TD[2]/text()")
        private String address;
        @Override public String getHostName() {
            if (hostName == null) {
                String[] data = address.trim().split(":");
                hostName = data[0];
            }
            return hostName;
        }
        @Override public int getPort() {
            if (port == 0) {
                String[] data = address.trim().split(":");
                port = Integer.parseInt(data[1]);
            }
            return port;
        }
        @Override
        public String toString() {
            return getHostName() + ":" + getPort() + " " + (alive ? "OK" : "NG");
        }
        private boolean alive;
        public void setAlive(boolean alive) {
            this.alive = alive;
        }
    }

    /** TODO use timer */
    private void updateProxyAddresses() throws IOException {

        ExecutorService executorService = Executors.newCachedThreadPool();

        WebScraper.Util.foreach(ProxyInternetAddress.class, address -> {
            try {
//Debug.println("SUBMIT: " + address.address);
                if (!address.address.isEmpty()) {
                    executorService.submit(new ProxyChecker(address));
                    Thread.sleep(300);
                }
            } catch (Exception e) {
Debug.println(Level.WARNING, "ERROR: " + address.address);
            }
        });
    }

    static class ProxyChecker implements Runnable {
        ProxyInternetAddress address;
        ProxyChecker(ProxyInternetAddress address) {
            this.address = address;
        }
        @Override public void run() {
Debug.println("TRY: " + address.address);
            HttpHost proxy = new HttpHost(address.getHostName(), address.getPort());

            try (CloseableHttpClient client = HttpClients.custom().setProxy(proxy).build()) {

                HttpHead head = new HttpHead("http://www.yahoo.co.jp/");
                HttpResponse response = client.execute(head);
//Debug.println("STA: " + status);
                boolean alive = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
                address.setAlive(alive);
            } catch (Exception e) {
System.err.println("ERR: " + e);
                address.setAlive(false);
            } finally {
System.err.println("ADD: " + address);
            }
        }
    }
}

/* */
