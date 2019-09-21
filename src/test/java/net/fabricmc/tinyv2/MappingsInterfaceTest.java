package net.fabricmc.tinyv2;

import net.fabricmc.mappings.Mappings;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MappingsInterfaceTest {
    @Test
    public void testParsingToOldInterface() throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(MappingsInterfaceTest.class.getResourceAsStream("inhtree.tiny"), StandardCharsets.UTF_8))) {
            Mappings mappings = V2MappingsProvider.readTinyMappings(reader);
            int x = 2;
        }
    }
}
