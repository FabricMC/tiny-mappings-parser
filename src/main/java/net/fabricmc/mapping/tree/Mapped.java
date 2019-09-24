package net.fabricmc.mapping.tree;

public interface Mapped {

	String getName(String namespace);

	/* Nullable */ String getMappedName(String namespace);

	/* Nullable */ String getComment();
}
