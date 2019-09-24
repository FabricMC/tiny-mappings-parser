package net.fabricmc.mapping.tree;

import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Represents a local variable element in a method.
 */
public interface LocalVariableDef extends Mapped {

	/**
	 * Gets the local variable index of this local variable.
	 *
	 * @return the local variable index
	 */
	@NonNegative int getLocalVariableIndex();

	/**
	 * Gets the local variable start offset of this local variable.
	 *
	 * @return the local variable start offset
	 */
	@NonNegative int getLocalVariableStartOffset();

	/**
	 * Gets the index of this local variable in the local variable table.
	 *
	 * <p>This may be absent; when absent, {@code -1} is returned.
	 *
	 * @return the index in the local variable table or {@code -1}
	 */
	int getLocalVariableTableIndex();
}
