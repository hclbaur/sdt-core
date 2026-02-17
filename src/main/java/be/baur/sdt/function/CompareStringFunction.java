package be.baur.sdt.function;

import java.text.Collator;
import java.util.List;
import java.util.Locale;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

/**
 * <code><i>double</i> sdt:compare-string( <i>string</i>, <i>string</i> )</code><br>
 * <code><i>double</i> sdt:compare-string( <i>string</i>, <i>string</i>, <i>string language</i> )</code>
 * <p>
 * Compares two strings locale-sensitive. This function returns -1, 0 or 1,
 * depending on whether the first argument collates before, equal to, or after
 * the second in the default locale:
 * <p>
 * <code>sdt:compare-string('a', 'A')</code> returns <code>-1.0</code>.<br>
 * <code>sdt:compare-string(3, '3')</code> returns <code>0.0</code>.<br>
 * <code>sdt:compare-string('b', 'A')</code> returns <code>1.0</code>.
 * <p>
 * An optional third argument specifies the language tag (IETF BCP 47) to obtain
 * a collation strategy that best fits the tag:
 * <p>
 * <code>sdt:compare-string('Ångström', 'Zulu', 'sv')</code> returns
 * <code>1.0</code> in accordance with Swedish collation rules.
 * <p>
 * This function can be used as a comparator in a sort statement.
 */
public final class CompareStringFunction implements Function
{
	public static final String NAME = "compare-string";
	
    /**
     * Create a new <code>CompareStringFunction</code> object.
     */
    public CompareStringFunction() {}

    
	/**
	 * Compares two arguments, returning -1, 0 or 1. An optional third argument
	 * specifies a language tag to obtain a collator (other than the default).
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains two or three items
	 * @return a signum value
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		final int argc = args.size();
		if (argc < 2 || argc > 3)
			throw new FunctionCallException(NAME + "() requires two or three arguments.");

		final Navigator nav = context.getNavigator();
		
		return evaluate(args.get(0), args.get(1), 
			argc == 3 ? StringFunction.evaluate(args.get(2), nav) : "", nav);
	}
    

	/**
	 * Compares two strings using a default or language dependent collator,
	 * returning -1, 0 or 1.
	 *
	 * @param str1 the first string
	 * @param obj2 the second string
	 * @param lang a language tag, not null
	 * @param nav  the navigator used
	 * @return a signum value
	 */
	private static Double evaluate(Object str1, Object obj2, String lang, Navigator nav) {

		final String s1 = StringFunction.evaluate(str1, nav);
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
