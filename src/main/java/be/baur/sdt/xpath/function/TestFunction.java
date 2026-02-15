package be.baur.sdt.xpath.function;

import java.util.Arrays;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

public final class TestFunction implements Function
{

    public TestFunction() {}

	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
    {
		return Arrays.asList("abc","bca","cab");
    }

}
