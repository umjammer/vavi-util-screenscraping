/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.util.screenscrape.annotation;

import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.JsonPath;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * JsonPathParserTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/05/15 umjammer initial version <br>
 */
class JsonPathParserTest {

    @Test
    void test() {
        final String json = "{\"resultID\":\"167d42f30842\",\"status\":1,\"message\":\"pointed out\",\"inputSentence\":\"薄茶色のシミがあちこちについた掛け布団。座ったら、五分でお尻が序くなってきそうだ。\",\"normalizedSentence\":\"薄茶色のシミがあちこちについた掛け布団。座ったら、五分でお尻が序くなってきそうだ。\",\"checkedSentence\":\"薄茶色のシミがあちこちについた掛け布団。座ったら、五分でお尻が <<序>> くなってきそうだ。\",\"alerts\":[{\"pos\":31,\"word\":\"序\",\"score\":0.8094954274798273,\"suggestions\":[\"な\",\"き\",\"し\"]}]}"; 
        String text = JsonPath.read(json, "$.resultID");
System.err.println(text);
        assertEquals("167d42f30842", text);
    }
}

/* */
