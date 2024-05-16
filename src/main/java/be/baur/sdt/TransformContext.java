package be.baur.sdt;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import be.baur.sdt.statements.Transform;

/**
 * A {@code TransformContext} is created prior to, and used during execution of
 * a {@code Transform}.
 * <p>
 * This context provides a writer for the {@code PrintStatement} to write output
 * to, and (optionally prepared) parameters to overwrite the default value of a
 * {@code ParamStatement}.
 * <p>
 * A context cannot be instantiated, but must be built using a {@link Builder}.
 * 
 * @see Transform
 */
public class TransformContext {

	private final Writer writer;
	private final Map<String, Object> parameters;

	private TransformContext(Builder builder) {

		this.writer = builder.writer;
		this.parameters = builder.parameters;
	}


	/**
	 * A builder class to build a {@code TransformContext}. The builder has methods
	 * to set a writer for textual output and/or add parameters to the context.
	 */
	 public static class Builder {
		
		private Writer writer = new PrintWriter(System.out);
		private final Map<String, Object> parameters = new HashMap<String, Object>();
		
		/**
		 * Creates an {@code Builder} that builds a {@code TransformContext} with a
		 * <i>standard output writer</i> and no parameters. To set a different writer
		 * and/or add parameters, use the provided setter methods.
		 */
		public Builder() {
		}
		
		/**
		 * Sets the {@code Writer} for the context to be built. The writer cannot be
		 * null, but a {@link SDT#nullWriter} can be used to suppress textual output.
		 * 
		 * @param writer a writer, not null
		 * @return the builder
		 */
		public Builder setWriter(Writer writer) {
			this.writer = Objects.requireNonNull(writer, "writer must not be null");
			return this;
		}
		
		/*
		 * Private helper method to set a parameter with non-null name and value. We do
		 * not supply a generic method because parameters cannot be just anything, they
		 * must be either String, Double or Boolean.
		 */
		private Builder setParameter(String name, Object value) {
			Objects.requireNonNull(name, "name must not be null");
			Objects.requireNonNull(value, "value must not be null");
			this.parameters.put(name, value);
			return this;
		}
		
		/**
		 * Sets or overwrites a String parameter for the context to be built.
		 * 
		 * @param name  the name of the parameter, not null
		 * @param value a String, not null
		 * @return the builder
		 */
		public Builder setStringParameter(String name, String value) {
			return setParameter(name, value);
		}
		
		/**
		 * Sets or overwrites a Double parameter for the context to be built.
		 * 
		 * @param name  the name of the parameter, not null
		 * @param value a Double, not null
		 * @return the builder
		 */
		public Builder setDoubleParameter(String name, Double value) {
			return setParameter(name, value);
		}
		
		/**
		 * Sets or overwrites a Boolean parameter for the context to be built.
		 * 
		 * @param name  the name of the parameter, not null
		 * @param value a Boolean, not null
		 * @return the builder
		 */
		public Builder setBooleanParameter(String name, Boolean value) {
			return setParameter(name, value);
		}
		
		/**
		 * Builds and returns a new {@code TransformContext}.
		 * 
		 * @return a transform context
		 */
		public TransformContext build() {
			return new TransformContext(this);
		}
	}


	/**
	 * Returns the {@code Writer} defined in this context.
	 * 
	 * @return a writer, never null
	 */
	public Writer getWriter() {
		return writer;
	}

	
	/**
	 * Returns an unmodifiable view of the parameters defined in this context.
	 * 
	 * @return a map, never null, can be empty
	 */
	public  Map<String, Object> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}

}
