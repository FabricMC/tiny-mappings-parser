package net.fabricmc.tinyv2;

/**
 * A visitor that explores Tiny V2 mappings.
 *
 * <p>State information should be stored in the visitor. This visitor preferably should use
 * a stack structure to keep track of its visit state so that it collects context correctly.
 *
 * <p>The caller of the visitor needs to guarantee the state of the mapping is correct.
 *
 * <p>All descriptors in the visits are in the format of the namespace at
 * {@link TinyMetadata#index(String)} index} {@code 0}. Do not remap these descriptors as
 * you don't have access to all class mappings when you received the descriptors! It's
 * recommended to put the collected mappings in a {@link java.util.Map} and then use a
 * {@link ClassMapper} to remap descriptors when the mapping visit is finished.
 */
public interface TinyVisitor {

	/**
	 * Start visiting a new mapping and collect basic mapping information.
	 *
	 * <p>It's recommended to collect the index of namespaces you want to track
	 * from the metadata.
	 *
	 * @param metadata the metadata of the Tiny V2 mapping
	 */
	default void start(TinyMetadata metadata) {
	}

	/**
	 * Visit a class.
	 *
	 * <p>{@link #start(TinyMetadata)} is called before this call and the visit stack
	 * is empty.
	 *
	 * @param name the mappings
	 */
	default void pushClass(MappingGetter name) {
	}

	/**
	 * Visit a field.
	 *
	 * <p>{@link #pushClass(MappingGetter)} is called before this call and the last
	 * element in the visit stack is a class.
	 *
	 * @param name the mappings
	 * @param descriptor the descriptor in the index 0 namespace's mapping
	 */
	default void pushField(MappingGetter name, String descriptor) {
	}

	/**
	 * Visit a method.
	 *
	 * <p>{@link #pushClass(MappingGetter)} is called before this call and the last
	 * element in the visit stack is a class.
	 *
	 * @param name the mappings
	 * @param descriptor the descriptor in the index 0 namespace's mapping
	 */
	default void pushMethod(MappingGetter name, String descriptor) {
	}

	/**
	 * Visits a method parameter.
	 *
	 * <p>{@link #pushMethod(MappingGetter, String)} (MappingGetter)} is called
	 * before this call and the last element in the visit stack is a method.
	 *
	 * @param name the mappings
	 * @param localVariableIndex the local variable index
	 */
	default void pushParameter(MappingGetter name, int localVariableIndex) {
	}

	/**
	 * Visits a method's local variable.
	 *
	 * <p>{@link #pushMethod(MappingGetter, String)} (MappingGetter)} is called
	 * before this call and the last element in the visit stack is a method.
	 *
	 * @param name the mappings
	 * @param localVariableIndex the local variable index
	 * @param localVariableStartOffset the local variable start offset
	 * @param localVariableTableIndex the local variable table index
	 */
	default void pushLocalVariable(MappingGetter name, int localVariableIndex, int localVariableStartOffset, int localVariableTableIndex) {
	}

	/**
	 * Visits a comment (JavaDoc).
	 *
	 * <p>One of the other push methods is called before this call; the visit stack is not empty, and
	 * the last element in the visit stack can be commented (i.e. everything other than a comment).
	 *
	 * @param comment the comment
	 */
	default void pushComment(String comment) {
	}

	/**
	 * Remove a few elements from the context stack.
	 *
	 * <p>The sum of {@code count} of all pop calls should equal to the number of push calls when
	 * each class visit is finished.
	 *
	 * @param count the number of elements removed
	 */
	default void pop(int count) {
	}
}
