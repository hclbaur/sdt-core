package be.baur.sdt.serialization;

/**
 * Statements allowed by the SDT syntax. The lower-case name of a statement can
 * be accessed using the {@code toString()} method or the {@code tag} field.
 */
public enum Statements {

	NODE("node"), COPY("copy"),
	PRINT("print"), PRINTLN("println"), 
	VARIABLE("variable"), PARAM("param"),
	FOREACH("foreach"), IF("if"), CHOOSE("choose"), 
	WHEN("when", CHOOSE), OTHERWISE("otherwise", CHOOSE);

	/** The (lower-case) name tag. */
	public final String tag;
	private final Statements context; // the context that this may appear in, null means any context
	
	
	Statements(String tag) {
		this.tag = tag; this.context = null;
	}
	
	
	Statements(String tag, Statements context) {
		this.tag = tag; this.context = context;
	}
	
	
	/**
	 * Returns the (lower-case) tag of this component.
	 * 
	 * @return the name tag
	 */
	@Override
	public String toString() { 
		return tag; 
	}


	/**
	 * Returns a statement by its tag. This method returns a null reference if no
	 * statement with the specified tag is known.
	 * 
	 * @param tag a name tag
	 * @return a statement, may be null
	 */
	public static Statements get(String tag) {
		for (Statements m : values()) 
			if (m.tag.equals(tag)) return m;
		return null;
	}
	
	
	/**
	 * Returns whether this statement is allowed in the supplied context.<br>
	 * Example: <code> WHEN.isAllowed(CHOOSE)</code> returns true.
	 * 
	 * @param context the context statement, null means any
	 * @return true or false
	 */
	public boolean isAllowedIn(Statements context) {
		
		return (this.context == null || context == this.context);
	}
}
