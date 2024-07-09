package be.baur.sdt.parser;

import java.util.Arrays;
import java.util.List;

/**
 * Statement and attribute keywords allowed by the SDT syntax.
 */
public enum Keyword {
	
	CHOOSE("choose", false, false),
	COMPARATOR("comparator", true, true),	// attribute
	COPY("copy", true, false), 				// leaf statement
	FOREACH("foreach", false, false), 
	IF("if", false, false), 
	GROUP("group", true, true),				// attribute
	NODE("node", false, false), 
	OTHERWISE("otherwise", false, false),
	PARAM("param", false, false), 
	PRINT("print", true, false), 			// leaf statement
	PRINTLN("println", true, false),		// leaf statement
	REVERSE("reverse", true, true),			// attribute
	SELECT("select", true, true),			// attribute
	SORT("sort", null, false),
	VALUE("value", true, true),				// attribute
	VARIABLE("variable", false, false), 
	WHEN("when", false, false),
	;
	
	static {
		WHEN.setAllowedIn(Arrays.asList(CHOOSE));
		OTHERWISE.setAllowedIn(Arrays.asList(CHOOSE));
		
		GROUP.setAllowedIn(Arrays.asList(FOREACH));
		SORT.setAllowedIn(Arrays.asList(FOREACH));
		REVERSE.setAllowedIn(Arrays.asList(SORT));
		COMPARATOR.setAllowedIn(Arrays.asList(SORT));
		
		VALUE.setAllowedIn(Arrays.asList(NODE));
		SELECT.setAllowedIn(Arrays.asList(PARAM, VARIABLE));
	}
	

	/** The statement/attribute tag. */
	public final String tag;

	/** Whether this is a leaf statement or not (null means either is allowed). */
	public final Boolean isLeaf;
	
	/** Whether this is an attribute rather than a statement. */
	public final boolean isAttribute;
	
	/* The context statements this may appear in, null means any (default). */
	private List<Keyword> allowedIn = null;
	
	private Keyword(String tag, Boolean isLeaf, boolean isAttribute) {
		this.tag = tag; this.isLeaf = isLeaf; this.isAttribute = isAttribute;
	}
	
	/**
	 * Returns the tag of this statement/attribute.
	 * 
	 * @return a tag
	 */
	@Override
	public String toString() { 
		return tag; 
	}


	/**
	 * Returns a keyword by its tag. This method returns a null reference if the
	 * specified tag is unknown.
	 * 
	 * @param tag a tag
	 * @return a keyword, may be null
	 */
	public static Keyword get(String tag) {
		for (Keyword m : values()) 
			if (m.tag.equals(tag)) return m;
		return null;
	}
	
	
	/**
	 * Sets the context keywords in which this keyword is allowed to occur.
	 * 
	 * @param allowedIn a list of statements
	 */
	public void setAllowedIn(List<Keyword> allowedIn) {
		this.allowedIn = allowedIn;
	}
	
	
	/**
	 * Returns whether this keyword is allowed in the specified context.
	 * 
	 * @param context the context keyword
	 * @return true or false
	 */
	public boolean isAllowedIn(Keyword context) {
		return (this.allowedIn == null || this.allowedIn.contains(context));
	}
}
