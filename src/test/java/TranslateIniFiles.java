/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import vavix.util.translation.Translator;
import vavi.util.Debug;
import vavix.util.translation.InfoseekJapanTranslator;
import vavi.util.win32.WindowsProperties;


/**
 * ini 形式のファイルの値を翻訳します。
 */
public class TranslateIniFiles {

    public static void main(String[] args) throws IOException {
        WindowsProperties props = new WindowsProperties();
        props.load(Files.newInputStream(Paths.get(args[0])));

        Translator translator = new InfoseekJapanTranslator();

        for (Object o : props.keySet()) {
            String key = (String) o;
            String value = props.getProperty(key);
            String translated = null;
            try {
                translated = translator.toLocal(value);
            } catch (IOException e) {
                Debug.println(e);
                translated = "★★★ 翻訳失敗 ★★★[" + value + "]";
            }
            props.setProperty(key, translated);
        }

        props.store(Files.newOutputStream(Paths.get(args[1])), "created by t1");
    }
}
