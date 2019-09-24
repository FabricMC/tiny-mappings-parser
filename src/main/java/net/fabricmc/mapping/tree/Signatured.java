package net.fabricmc.mapping.tree;

/**
 * Represents a mapping element that has a descriptor.
 */
public interface Signatured extends Mapped {

	/**
	 * Maps the signature to the target namespace.
	 *
	 * @param namespace the target namespace
	 * @return the mapped signature
	 */
	String getSignature(String namespace);
}
