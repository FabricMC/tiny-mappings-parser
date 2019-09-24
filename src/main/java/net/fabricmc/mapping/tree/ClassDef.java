package net.fabricmc.mapping.tree;

import java.util.Collection;

/**
 * Represents a class element.
 */
public interface ClassDef extends Mapped {

	/**
	 * Gets all method elements belonging to this class element.
	 *
	 * @return all methods
	 */
	Collection<MethodDef> getMethods();

	/**
	 * Gets all field elements belonging to this class element.
	 *
	 * @return all fields
	 */
	Collection<FieldDef> getFields();
}
