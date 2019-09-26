package net.fabricmc.mapping.tree;

import net.fabricmc.mapping.reader.v2.TinyMetadata;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmptyTinyTree implements TinyTree {
    public static class Metadata implements TinyMetadata{
         @Override
            public int getMajorVersion() {
                return 2;
            }

            @Override
            public int getMinorVersion() {
                return 0;
            }

            @Override
            public List<String> getNamespaces() {
                return Collections.emptyList();
            }

            @Override
            public Map<String, @Nullable String> getProperties() {
                return Collections.emptyMap();
            }

            @Override
            public int index(String namespace) throws IllegalArgumentException {
                return 0;
            }
    }

    @Override
    public TinyMetadata getMetadata() {
        return TinyMappingFactory.EMPTY_METADATA;
    }

    @Override
    public Map<String, ClassDef> getDefaultNamespaceClassMap() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<ClassDef> getClasses() {
        return Collections.emptyList();
    }
}
