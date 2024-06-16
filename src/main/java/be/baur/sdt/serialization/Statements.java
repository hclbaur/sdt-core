package be.baur.sdt.serialization;

import java.util.Arrays;
import java.util.List;

/**
 * Statements and keywords allowed by the SDT syntax.
 */
public enum Statements {
	
	CHOOSE("choose", false),
	COMPARATOR("comparator", true),
	COPY("copy", true), 
	FOREACH("foreach", false), 
	IF("if", false), 
	NODE("node", false), 
	OTHERWISE("otherwise", false),
	PARAM("param", false), 
	PRINT("print", true), 
	PRINTLN("println", true),
	REVERSE("reverse", true),
	SELECT("select", true),
	SORT("sort", null),
	VALUE("value", true),
	VARIABLE("variable", false), 
	WHEN("when", false)
	;
	
	static {
		WHEN.setAllowedIn(Arrays.asList(CHOOSE));
		OTHERWISE.setAllowedIn(Arrays.asList(CHOOSE));
		
		SORT.setAllowedIn(Arrays.asList(FOREACH));
		REVERSE.setAllowedIn(Arrays.asList(SORT));
		COMPARATOR.setAllowedIn(Arrays.asList(SORT));
		
		VALUE.setAllowedIn(Arrays.asList(NODE));
		SELECT.setAllowedIn(Arrays.asList(PARAM, VARIABLE));
	}
	

	/** The statement tag. */
	public final String tag;

	/** Whether this is a leaf statement or not (null means either is allowed). */
	public final Boolean isLeaf;
	
	/* The context nodes this may appear in, null means any (default). */
	private List<Statements> allowedIn = null;
	
	private Statements(String tag, Boolean isLeaf) {
		this.tag = tag; this.isLeaf = isLeaf;
	}
	
	
	/**
	 * Returns the tag of this statement.
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
	 * Sets the context statements in which this statement is allowed to occur.
	 * 
	 * @param allowedIn a list of statements
	 */
	public void setAllowedIn(List<Statements> allowedIn) {
		this.allowedIn = allowedIn;
	}
	
	
	/**
	 * Returns whether this statement is allowed in the specified context statement.
	 * 
	 * @param context the context statement
	 * @return true or false
	 */
	public boolean isAllowedIn(Statements context) {
		return (this.allowedIn == null || this.allowedIn.contains(context));
	}
}
