/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import vavix.util.translation.Translator;
import vavi.util.Debug;
import vavix.util.translation.InfoseekJapanTranslator;


/**
 * properties 形式のファイルの値を翻訳します。
 */
public class TranslateProperties {
    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
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
