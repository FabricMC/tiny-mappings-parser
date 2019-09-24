package net.fabricmc.mapping.tree;

/**
 * Represents an element that can be mapped and have comments.
 */
public interface Mapped {

	/**
	 * Gets the mapped name of the element in the target namespace.
	 *
	 * @param namespace the target namespace
	 * @return the mapped name
	 */
	String getName(String namespace);

	/**
	 * Gets an explicitly mapped name of the element in the target namespace.
	 *
	 * <p>May be empty if the target namespace inherits the name from another
	 * namespace.
	 *
	 * @param namespace the target namespace
	 * @return the explicitly mapped name
	 */
	/* may be empty */ String getMappedName(String namespace);

	/**
	 * Gets the comment (JavaDoc, etc.) on an element.
	 *
	 * <p>The comment may be {@code null} if there is none.
	 *
	 * @return the comment or {@code null}
	 */
	/* Nullable */ String getComment();
}
