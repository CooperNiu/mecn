package com.mecn.i18n;

import org.junit.jupiter.api.Test;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ConsoleMessage 国际化工具测试
 */
class ConsoleMessageTest {

    @Test
    void testDefaultLocale() {
        ConsoleMessage msg = new ConsoleMessage();
        assertNotNull(msg.get("app.name"));
    }

    @Test
    void testChineseLocale() {
        ConsoleMessage msg = new ConsoleMessage(Locale.CHINESE);
        String name = msg.get("app.name");
        assertNotNull(name);
        // Contains Chinese characters
        assertTrue(name.contains("经济") || name.contains("MECN"));
        assertFalse(name.startsWith("!"));
    }

    @Test
    void testEnglishLocale() {
        ConsoleMessage msg = new ConsoleMessage(Locale.ENGLISH);
        String name = msg.get("app.name");
        assertNotNull(name);
        assertTrue(name.contains("Economic") || name.contains("MECN"));
        assertFalse(name.startsWith("!"));
    }

    @Test
    void testWithArgs() {
        ConsoleMessage msg = new ConsoleMessage(Locale.ENGLISH);
        String result = msg.fmt("cli.data.loaded", 10);
        assertTrue(result.contains("10"));
    }

    @Test
    void testMissingKeyReturnsPlaceholder() {
        ConsoleMessage msg = new ConsoleMessage();
        String result = msg.get("nonexistent.key");
        assertTrue(result.startsWith("!"));
    }

    @Test
    void testUnknownLocaleFallsBackToEnglish() {
        ConsoleMessage msg = new ConsoleMessage(Locale.JAPANESE);
        String name = msg.get("app.name");
        assertNotNull(name);
        assertFalse(name.startsWith("!"));
    }

    @Test
    void testFmtReturnsSameAsGetWhenNoArgs() {
        ConsoleMessage msg = new ConsoleMessage();
        assertEquals(msg.get("app.name"), msg.fmt("app.name"));
    }

    @Test
    void testRawReturnsCorrectValue() {
        ConsoleMessage msg = new ConsoleMessage(Locale.ENGLISH);
        String val = msg.raw("cli.analysis.complete");
        assertNotNull(val);
        assertFalse(val.startsWith("!"));
        assertTrue(val.contains("Analysis") || val.contains("OK"));
    }
}
