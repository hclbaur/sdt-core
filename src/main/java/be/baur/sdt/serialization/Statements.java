package be.baur.sdt.serialization;

import java.util.Arrays;
import java.util.List;

/**
 * Statements allowed by the SDT syntax. The lower-case name of a statement can
 * be accessed using the {@code toString()} method or the {@code tag} field.
 */
public enum Statements {

	IF("if", false), CHOOSE("choose", false),
	WHEN("when", false, Arrays.asList(CHOOSE)),
	OTHERWISE("otherwise", false, Arrays.asList(CHOOSE)),
	
	FOREACH("foreach", false), 
	SORT("sort", null, Arrays.asList(FOREACH)),
	REVERSE("reverse", true, Arrays.asList(SORT)),
	COMPARATOR("comparator", true, Arrays.asList(SORT)),
	
	PRINT("print", true), PRINTLN("println", true),
	
	NODE("node", false), COPY("copy", false), 
	VALUE("value", true, Arrays.asList(NODE)),
	
	PARAM("param", false), VARIABLE("variable", false), 
	SELECT("select", true, Arrays.asList(COPY, PARAM, VARIABLE))
	;

	/** The (lower-case) name tag. */
	public final String tag;
	/** Whether this is a leaf statement or not (null means either is allowed). */
	final Boolean isLeaf;
	/* The context nodes this may appear in, null means any. */
	private final List<Statements> context;
	
	
	private Statements(String tag, Boolean isLeaf) {
		this.tag = tag; this.isLeaf = isLeaf; this.context = null;
	}
	
	private Statements(String tag, Boolean isLeaf, List<Statements> context) {
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
	 * Returns whether this statement is allowed in the supplied parent.<br>
	 * Example: <code> WHEN.isAllowed(CHOOSE)</code> returns true.
	 * 
	 * @param parent the parent statement, null means any
	 * @return true or false
	 */
	public boolean isAllowedIn(Statements parent) {
		return (this.context == null || this.context.contains(parent));
	}
}
