package org.openmhealth.reference.concordia;

import java.util.Iterator;
import java.util.List;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.StringSchema;
import name.jenkins.paul.john.concordia.validator.DataValidator;
import name.jenkins.paul.john.concordia.validator.SchemaValidator;
import name.jenkins.paul.john.concordia.validator.ValidationController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

/**
 * <p>
 * A type and data validator for enums within string schemas.
 * </p>
 *
 * @author John Jenkins
 */
public class EnumValidator
	implements SchemaValidator<StringSchema>, DataValidator<StringSchema> {
	
	/**
	 * The schema field name for string schemas that may be present and, if so,
	 * must be an array of strings that are valid values for this data.
	 */
	public static final String ENUM_SCHEMA_FIELD = "allowed_values";

	/**
	 * Verifies that if {@link #ENUM_SCHEMA_FIELD} exists, it is an array of
	 * strings.
	 */
	@Override
	public void validate(
		final StringSchema schema,
		final ValidationController controller)
		throws ConcordiaException {

		// Attempt to get the enum definition.
		Object enumField = schema.getAdditionalFields().get(ENUM_SCHEMA_FIELD);
		
		// If the definition exists, validate it.
		if(enumField != null) {
			// It must be a JSON array.
			if(enumField instanceof List) {
				// Iterate through the elements.
				Iterator<?> enumFieldIter =
					((List<?>) enumField).iterator();
				
				// Each element must be a string.
				while(enumFieldIter.hasNext()) {
					if(! (enumFieldIter.next() instanceof String)) {
						throw
							new ConcordiaException(
								"An " +
									ENUM_SCHEMA_FIELD +
									" entry is not a string: " +
									enumField.toString());
					}
				}
			}
			// It is not a JSON array.
			else {
				throw
					new ConcordiaException(
						"The " +
							ENUM_SCHEMA_FIELD +
							" field list must be a JSON array: " +
							enumField.toString());
			}
		}
	}
	
	/**
	 * Verifies that any data point for this schema is one of the required enum
	 * values.
	 */
	@Override
	public void validate(
		final StringSchema schema,
		final JsonNode data,
		final ValidationController controller)
		throws ConcordiaException {
		
		// If the data is null, we can ignore it because that value shouldn't
		// be in our list. Null will be caught by our default, required
		// validation, which will only allow this if the schema defines this
		// field as optional.
		if((data == null) || (data instanceof NullNode)) {
			return;
		}
		
		// Get the value of this node.
		// We can also safely assume that this will return us a non-null value
		// as our default, required validation would have first run to ensure
		// that it is a TextNode.
		String value = data.textValue();
		
		// Attempt to get the enum definition.
		Object enumField = schema.getAdditionalFields().get(ENUM_SCHEMA_FIELD);
		
		// If the definition exists, validate it.
		if(enumField != null) {
			// We can safely cast here, because our validation above took care
			// of this for us.
			@SuppressWarnings("unchecked")
			List<String> enumFieldIter = (List<String>) enumField;
			
			// Check each of our allowed values against the given value.
			if(! enumFieldIter.contains(value)) {
				// If one was not found, throw an exception.
				throw
					new ConcordiaException(
						"The value, '" +
							value +
							"', is not in our list of acceptable values: " +
							enumField.toString());
			}
		}
	}
}