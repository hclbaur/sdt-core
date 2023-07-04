package be.baur.sdt.serialization;

/**
 * Attributes allowed by the SDT syntax. The lower-case name of a keyword can
 * be accessed using the {@code toString()} method or the {@code tag} field.
 */
public enum Attribute {

	VALUE("value"), SELECT("select");

	/** The (lower-case) name tag. */
	public final String tag;
	
	Attribute(String tag) {
		this.tag = tag;
	}

	
	/**
	 * Returns the (lower-case) tag of this attribute.
	 * 
	 * @return the name tag
	 */
	public String toString() {
		return tag;
	}

	
	/**
	 * Returns an attribute by its tag. This method returns a null reference if no
	 * attribute with the specified tag is known.
	 * 
	 * @param tag a name tag
	 * @return an attribute, may be null
	 */
	public static Attribute get(String tag) {
		for (Attribute a : values())
			if (a.tag.equals(tag)) return a;
		return null;
	}
}
