/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import vavi.util.CharNormalizerJa;


/**
 * UnitTest1.
 *
 * <h3>vavi style</h3>
 * <p>
 * <pre>
 * music [ / lyrics (omit when same as music) ] [ / arrange (omit almost) ]
 * </pre>
 * all part are separated by ", "
 *
 * <h4>ex.</h4>
 * <pre>
 * Naohide Sano, Showji Kumi / azumi / so-to
 * so-to
 * so-to / azumi
 * </pre>
 * </p>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/10/09 nsano initial version <br>
 */
public class UnitTest1 {

    @Test
    public void test01() throws Exception {
        String[] titles = {
            "test -XXX",
            "test -XXX-",
            "test 〜XXX",
            "test 〜XXX〜",
            "test ーXXX",
            "test ーXXXー",
            "test ~XXX",
            "test ~XXX~",
            "test feat. XXX",
            "test featuring XXX",
            "test (XXX",
            "test (XXX)",
            "test （XXX",
            "test （XXX）",
            "test /XXX",
            "test ／XXX",
            "The test",
            "A test",
            "An test",
        };
        String normalizedName;
        for (String title : titles) {
            Matcher matcher = iTunes.normalizeArticlePattern.matcher(title);
            if (matcher.matches()) {
                normalizedName = matcher.group(2);
System.err.println("A: " + title + " -> " + normalizedName);
                continue;
            } else {
                normalizedName = title;
            }
            matcher = iTunes.normalizeNamePattern.matcher(normalizedName);
            if (matcher.matches()) {
                normalizedName = matcher.group(1).trim();
System.err.println("N: " + title + " -> " + normalizedName);
                Assertions.assertEquals("test", normalizedName);
            } else {
                throw new IllegalArgumentException(title);
            }
        }
    }

    @Test
    public void test03() throws Exception {
        String[] titles = {
            "ひらがな",
            "漢字",
            "漢字とひらがな",
            "漢字AND ENGLISH",
            "漢字AND ーENGLISH",
            "sano",
            "ＳＡＮＯ",
            "ｓａｎｏ",
        };
        for (String title : titles) {
            System.err.println(title);
            Matcher matcher = iTunes.normalizeComposerPattern.matcher(title);
            if (matcher.matches()) {
                Assertions.fail();
            }
        }
        titles = new String[] {
            "SANO NAOHIDE",
            "SANO NAOHI'DE",
            "SANO 123 NAOHI'DE",
            "S. NAOHIDE",
            "S. NAOーHIDE",
            "S. NAO_HIDE",
            "S. NAO-HIDE",
            "SANO NAOHIDE (US1)",
        };
        for (String title : titles) {
            System.err.println(title);
            Matcher matcher = iTunes.normalizeComposerPattern.matcher(title);
            if (!matcher.matches()) {
                Assertions.fail();
            }
        }
    }

    public void test02() throws Exception {
        iTunes.Title each = new iTunes.Title();
        each.artist = "モダーン今夜";
        each.name = "風の道しるべ";
        each.composer = "";
        each.album = "";
        each.albumArtist = "";
        iTunes.doEach(each);
    }

    public void test07() throws Exception {
        String[][] titles = new String[][] {
            { "R.E.M.", "Disturbance At The Heron House" },
            { "R.E.M.", "Feeling Gravity's Pull" },
            { "R.E.M.", "Exhuming McCarthy" },
            { "", "" },
        };
        for (String[] title : titles) {
            if (!title[0].isEmpty()) {
                iTunes.Title each = new iTunes.Title();
                each.artist = title[0];
                each.name = title[1];
                each.composer = "";
                each.album = "";
                each.albumArtist = "";
                iTunes.doEach(each);
            }
        }
    }

    /** jasrac composer format to vavi style */
    public void test04() throws Exception {

      String[] us = {
          "http://www2.jasrac.or.jp/eJwid/main.jsp?trxID=F20101&WORKS_CD=0X173806&subSessionID=001&subSession=start",
          "",
          "",
          "",
          "",
          "",
      };

      for (String url : us) {
          Pattern pattern = Pattern.compile("(.*)(main\\.jsp.*)");
          Matcher matcher = pattern.matcher(url);
          if (matcher.matches()) {
              url = matcher.group(2);
          }
          iTunes.init();
          System.out.println(iTunes.getComposer(url));
      }
    }

    /** allmusic.com composer format to vavi style */
    public void test05() throws Exception {

      String[] csx = {
          "Abdul-Basit / Jenkins / James Poyser / Ahmir \"?uestlove\" Thompson / Tariq Trotter",
          "",
          "",
      };

      for (String cs : csx) {
          String[] css = cs .split("/");
          StringBuilder rr = new StringBuilder();
          for (String c : css) {
              String[] ps = c.split(",");
              String r;
              if (ps.length == 2) {
                  r = ps[1].trim() + " " + ps[0].trim();
              } else {
                  r = c.trim();
              }
              rr.append(r);
              rr.append(", ");
          }
          if (rr.length() > 2) {
              rr.setLength(rr.length() - 2);
          }
          System.out.println(rr);
        }
    }

    /** tutaya composer format to vavi style */
    public void test06() throws Exception {

        String[] csx = {
          "",
          "",
        };

        for (String cs : csx) {
            System.out.println(iTunes.normalizeComposer(CharNormalizerJa.ToHalfAns2.normalize(cs)));
        }
    }

    public static void main(String[] args) throws Exception {
        UnitTest1 app = new UnitTest1();
        app.test04();
    }
}
