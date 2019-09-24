package net.fabricmc.mapping.reader.v2;

/**
 * A getter for a mapping.
 *
 * Don't keep many instances of mapping getters! It will be memory-expensive!
 */
public interface MappingGetter {

	/**
	 * Gets the corresponding mapped name under a namespace with the namespace's index.
	 *
	 * <p>To retrieve a namespace's index, call {@link TinyMetadata#index(String)}.
	 *
	 * @param namespace the namespace's index
	 * @return the retrieved name
	 */
	String get(int namespace);

	/**
	 * Creates an array of all mapped names, ordered by their namespace's appearance.
	 *
	 * <p>This method returns all the mapped names in the declaration. It does not expand the
	 * array to fill trailing empty namespaces with names from the last declared namespace. Use
	 * {@link #getAll(int)} for a version that respect trailing empty namespaces.
	 *
	 * <p>This method call would be expensive as each call may create a new array.
	 *
	 * @return all mapped names
	 */
	String[] getAll();

	/**
	 * Creates an array of all mapped names, ordered by their namespace's appearance.
	 *
	 * Compared to {@link #getAll()}, this method makes sure the returned array has a
	 * length of at least {@code namespaceCount} and fills empty namespaces with the
	 * mapping from the last namespace.
	 *
	 * <p>This method call would be expensive as each call may create a new array.
	 *
	 * @return all mapped names
	 */
	String[] getAll(int namespaceCount);
}
