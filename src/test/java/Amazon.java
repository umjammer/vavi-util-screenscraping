/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;

import org.openqa.selenium.WebDriver;

import vavi.net.auth.UserCredential;
import vavi.net.auth.web.amazon.AmazonLocalAuthenticator;
import vavi.net.auth.web.amazon.AmazonLocalUserCredential;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import vavix.util.screenscrape.annotation.HtmlXPathParser;
import vavix.util.screenscrape.annotation.SeleniumInputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * Amazon Having List. 2020 version.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/03/12 umjammer initial version <br>
 */
public class Amazon {

    /** */
    public static class MyInput extends SeleniumInputHandler {
        private static WebDriver driver;

        @PropsEntity(url = "file://${user.dir}/local.properties")
        public static class Id {
            @Property(name = "java.test.amazon.email")
            String email;
        }

        static {
            try {
                Id bean = new Id();
                PropsEntity.Util.bind(bean);
                String url = "https://www.amazon.co.jp/ap/signin?openid.return_to=https%3A%2F%2Fwww.amazon.co.jp%2Fref%3Dgw_sgn_ib%2F358-4710901-2880702&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=jpflex&openid.mode=checkid_setup&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0";
                UserCredential credential = new AmazonLocalUserCredential(bean.email);
                driver = new AmazonLocalAuthenticator(url).authorize(credential);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> driver.quit()));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        protected WebDriver getDriver() {
            return driver;
        }
    }

    @WebScraper(url = "https://www.amazon.co.jp/gp/yourstore/iyr/ref=pd_ys_iyr_next?ie=UTF8&collection=owned&iyrGroup=&maxItem={0}&minItem={1}",
                input = MyInput.class,
                parser = HtmlXPathParser.class,
                isDebug = false,
                value = "//DIV[@id='iyrCenter']/TABLE[2]/TBODY/TR[starts-with(@id, 'iyrListItem')]")
    public static class Result {
        @Target(value = "//B/A/text()")
        String title;
        // TODO url
        String author = "";
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(title);
            sb.append(", ");
            sb.append(author);
            return sb.toString();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 193; i++) {
            int min = i * 15 + 1;
            int max = (i + 1) * 15;
            WebScraper.Util.foreach(Result.class, System.out::println, String.valueOf(max), String.valueOf(min));
        }
    }
}
