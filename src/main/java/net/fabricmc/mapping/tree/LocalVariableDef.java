package net.fabricmc.mapping.tree;

/**
 * Represents a local variable element in a method.
 */
public interface LocalVariableDef extends Mapped {

	/**
	 * Gets the local variable index of this local variable.
	 *
	 * @return the local variable index
	 */
	int getLocalVariableIndex();

	/**
	 * Gets the local variable start offset of this local variable.
	 *
	 * @return the local variable start offset
	 */
	int getLocalVariableStartOffset();

	/**
	 * Gets the index of this local variable in the local variable table.
	 *
	 * <p>This may be absent; when absent, {@code -1} is returned.
	 *
	 * @return the index in the local variable table or {@code -1}
	 */
	/* optional */ int getLocalVariableTableIndex();
}
