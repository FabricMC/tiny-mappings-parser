package net.fabricmc.mapping.reader.v2;

import net.fabricmc.mappings.Mappings;
import net.fabricmc.mappings.model.V2MappingsProvider;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MappingsInterfaceTest {
	@Test
	public void testParsingToOldInterface() throws Exception {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(MappingsInterfaceTest.class.getResourceAsStream("/bigboi_mappings.tinyv2"), StandardCharsets.UTF_8))) {
            Mappings mappings = V2MappingsProvider.readTinyMappings(reader);
            int x = 2;
        }
    }
}
