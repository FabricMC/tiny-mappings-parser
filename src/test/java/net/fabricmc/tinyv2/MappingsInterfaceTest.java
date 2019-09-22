package net.fabricmc.tinyv2;

import net.fabricmc.mappings.Mappings;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MappingsInterfaceTest {
	@Test
	public void testParsingToOldInterface() throws Exception {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(MappingsInterfaceTest.class.getResourceAsStream("/bigboi_mappings.tinyv2"), StandardCharsets.UTF_8))) {
            Mappings mappings = V2MappingsProvider.readTinyMappings(reader);
            int x = 2;
        }
    }
}
