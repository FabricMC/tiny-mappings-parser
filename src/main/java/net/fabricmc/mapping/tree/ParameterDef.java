package net.fabricmc.mapping.tree;

/**
 * Represents a method parameter element.
 */
public interface ParameterDef extends Mapped {

	/**
	 * Gets the index of this parameter.
	 *
	 * @return the index
	 */
	int getLocalVariableIndex();
}
