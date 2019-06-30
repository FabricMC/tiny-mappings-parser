package net.fabricmc.tinyv2;

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
	 * <p>This method call would be expensive as each call may create a new array.
	 *
	 * @return all mapped names
	 */
	String[] getAll();
}
