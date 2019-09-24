package net.fabricmc.mapping.reader.v2;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Metadata for a Tiny V2 mapping file.
 *
 * Implementations are immutable and should be lightweight POJOs that are safe to be referenced.
 */
public interface TinyMetadata {

	/**
	 * Gets the major version of the mapping.
	 *
	 * <p>This number is supposed to be non-negative, but a negative value may appear.
	 *
	 * @return the major version
	 */
	int getMajorVersion();

	/**
	 * Gets the minor version of the mapping.
	 *
	 * <p>This number is supposed to be non-negative, but a negative value may appear.
	 *
	 * @return the minor version
	 */
	int getMinorVersion();

	/**
	 * Gets the namespaces in this mapping in the order they are listed.
	 *
	 * <p>The returned list is immutable.
	 *
	 * @return the list of namespaces
	 */
	List<String> getNamespaces();

	/**
	 * Gets the properties in this Tiny V2 mapping, appearing in key-value pairs.
	 *
	 * <p>The property map's iteration order is the same as the order
	 * they appear in the Tiny V2 mapping.
	 *
	 * <p>Note: the values in these properties may be {@code null}, meaning there
	 * is a key without an associated value. The key cannot be {@code null}.
	 *
	 * <p>The returned map is immutable.
	 *
	 * @return the properties
	 */
	Map<String, @Nullable String> getProperties();

	/**
	 * Utility method to efficiently index a namespace's position in this mapping file.
	 *
	 * <p>The implementation should be more efficient than calling {@code getNamespaces().indexOf(namespace)}.
	 *
	 * @param namespace the literal namespace
	 * @return the namespace's index
	 * @throws IllegalArgumentException if the namespace does not exist in the mapping
	 */
	int index(String namespace) throws IllegalArgumentException;
}
