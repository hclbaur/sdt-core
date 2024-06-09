package be.baur.sdt.xpath.function;

import java.text.Collator;
import java.util.List;
import java.util.Locale;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

/**
 * <code><i>double</i> sdt:compare-string( <i>object</i>, <i>object</i> )</code><br>
 * <code><i>double</i> sdt:compare-string( <i>object</i>, <i>object</i>, <i>string language</i> )</code>
 * <p>
 * Compares two objects locale-sensitive. This function converts its arguments
 * to strings and returns -1 if the second argument precedes the first, 1 if it
 * exceeds it, and 0 if the arguments are considered equal in the default
 * locale:
 * <p>
 * <code>sdt:compare-string('a','A')</code> returns <code>-1.0</code>.<br>
 * <code>sdt:compare-string(3,'3')</code> returns <code>0.0</code>.<br>
 * <code>sdt:compare-string('b','A')</code> returns <code>1.0</code>.
 * <p>
 * An optional third argument specifies the language tag (IETF BCP 47) to obtain
 * a collation strategy that best fits the tag:
 * <p>
 * <code>sdt:compare-string('Ångström','Zulu','sv')</code> returns
 * <code>1.0</code> in accordance with Swedish collation rules.
 * <p>
 * This function can be used as a comparator in a sort statement.
 */
public class CompareStringFunction implements Function
{

    /**
     * Create a new <code>CompareStringFunction</code> object.
     */
    public CompareStringFunction() {}

    
	/**
	 * Compares two arguments, returning -1, 0 or 1. An optional third argument
	 * specifies a language tag to obtain a collator (other than the default).
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains two or three items.
	 * 
	 * @return a <code>Double</code>
	 * 
	 * @throws FunctionCallException if <code>args</code> has more than three or
	 *                               less than two items.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		final int argc = args.size();
		if (argc < 2 || argc > 3)
			throw new FunctionCallException("compare-string() requires two or three arguments.");

		final Navigator nav = context.getNavigator();
		
		return evaluate(args.get(0), args.get(1), 
			argc == 3 ? StringFunction.evaluate(args.get(2), nav) : "", nav);
	}
    

	/**
	 * Compares two objects using a default or language dependent collator,
	 * returning -1, 0 or 1.
	 *
	 * @param obj1 the first object to be compared
	 * @param obj2 the second object to be compared
	 * @param lang a language tag, not null
	 * @param nav  the navigator used
	 * 
	 * @return a <code>Double</code>
	 */
	public static Double evaluate(Object obj1, Object obj2, String lang, Navigator nav) {

		final String s1 = StringFunction.evaluate(obj1, nav);
		final String s2 = StringFunction.evaluate(obj2, nav);

		Collator c;
		if (lang.isEmpty())
			c = Collator.getInstance();
		else
			c = Collator.getInstance(Locale.forLanguageTag(lang));

		return Math.signum((double) c.compare(s1, s2));
	}



//	public static void main(String[] args) {
//
//		Locale[] locales = Collator.getAvailableLocales();
//		// for (Locale loc : locales) System.out.println(loc.getDisplayName());
//
//		//doCompare("de", "ähnlich", "after");
//		//doCompare("de", "füße", "fussing");
//		//doCompare("de", "töne", "tofu");
//		doCompare("sv", "Ångström", "Zulu");
//	}
//
//    private static void doCompare(String lang, String str1, String str2) {
//    	Locale loc1 = Locale.getDefault(), loc2=Locale.forLanguageTag(lang);
//        System.out.println(loc1.getDisplayName()+":");
//        compareStrings(loc1, Collator.PRIMARY, str1, str2);
//        compareStrings(loc1, Collator.SECONDARY, str1, str2);
//        compareStrings(loc1, Collator.TERTIARY, str1, str2);
//        System.out.println(loc2.getDisplayName()+":");
//        compareStrings(loc2, Collator.PRIMARY, str1, str2);
//        compareStrings(loc2, Collator.SECONDARY, str1, str2);
//        compareStrings(loc2, Collator.TERTIARY, str1, str2);
//    }
//    
//    private static void compareStrings(Locale locale, int strength, String str1, String str2) {
//    	Collator collator = Collator.getInstance(locale); 
//    	collator.setStrength(strength); 
//        int result = collator.compare(str1, str2);
//        if (result < 0) {
//            System.out.println("\"" + str1 + "\" less than \"" + str2 + "\"");
//        } else if (result > 0) {
//            System.out.println("\"" + str1 + "\" greater than \"" + str2 + "\"");
//        } else {
//            System.out.println("\"" + str1 + "\" equal to \"" + str2 + "\"");
//        }
//    }
    
}
