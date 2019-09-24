package net.fabricmc.mapping.tree;

import net.fabricmc.mapping.reader.v2.TinyMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a tiny mapping tree.
 */
public interface TinyTree {

	/**
	 * Gets the metadata of this mapping tree.
	 *
	 * @return the metadata
	 */
	TinyMetadata getMetadata();

	/**
	 * Gets the map from the default namespace class names to the class mappings.
	 *
	 * @return the name to class map
	 */
	Map<String, ClassDef> getDefaultNamespaceClassMap();

	/**
	 * Gets all the classes in the mappings.
	 *
	 * @return all the classes
	 */
	Collection<ClassDef> getClasses();
}
