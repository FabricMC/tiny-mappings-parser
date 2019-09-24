package net.fabricmc.mapping.tree;

import java.util.Collection;

/**
 * Represents a method element in a class.
 */
public interface MethodDef extends Signatured {

	/**
	 * Gets all parameter elements belonging to this method element.
	 *
	 * @return all parameters
	 */
	Collection<ParameterDef> getParameters();

	/**
	 * Gets all local variable elements belonging to this method element.
	 *
	 * @return all local variables
	 */
	Collection<LocalVariableDef> getLocalVariables();
}
