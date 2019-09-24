package net.fabricmc.mapping.reader.v2;

/**
 * An exception encountered during mapping parsing.
 */
public final class MappingParseException extends RuntimeException {

	MappingParseException(String message) {
		super(message);
	}

	MappingParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
