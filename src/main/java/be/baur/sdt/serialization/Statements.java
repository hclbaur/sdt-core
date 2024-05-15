package be.baur.sdt.serialization;

import java.util.Arrays;
import java.util.List;

/**
 * Statements allowed by the SDT syntax. The lower-case name of a statement can
 * be accessed using the {@code toString()} method or the {@code tag} field.
 */
public enum Statements {

	CHOOSE("choose", false), 
	COPY("copy", false),
	FOREACH("foreach", false), 
	IF("if", false), 
	NODE("node", false), 
	OTHERWISE("otherwise", false, Arrays.asList(CHOOSE)),
	PARAM("param", false), VARIABLE("variable", false), 
	PRINT("print", true), PRINTLN("println", true),
	SELECT("select", true, Arrays.asList(COPY,PARAM,VARIABLE)),
	VALUE("value", true, Arrays.asList(NODE)),
	WHEN("when", false, Arrays.asList(CHOOSE));

	/** The (lower-case) name tag. */
	public final String tag;
	/** Whether this is a leaf statement (no sub-statements allowed). */
	public final boolean isLeaf;
	/* The context nodes this may appear in, null means any context. */
	private final List<Statements> context;
	
	
	private Statements(String tag, boolean isLeaf) {
		this.tag = tag; this.isLeaf = isLeaf; this.context = null;
	}
	
	private Statements(String tag, boolean isLeaf, List<Statements> context) {
		this.tag = tag; this.isLeaf = isLeaf; this.context = context;
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
		return (this.context == null || this.context.contains(context));
	}
}
