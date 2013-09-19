package org.openmhealth.reference.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.SerializeExceptFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * <p>
 * A custom ObjectMapper for Open mHealth that adds functionality like
 * filtering fields on HTTP-level serialization.
 * </p>
 *
 * @author John Jenkins
 */
public class OmhObjectMapper extends ObjectMapper {
	/**
	 * <p>
	 * A custom annotation for fields that should not be serialized by Jackson
	 * when their encapsulating object is being serialized by Spring.
	 * </p> 
	 *
	 * @author John Jenkins
	 */
	@Documented
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface JacksonFieldFilter {		
		/**
		 * The filter group to which the field belongs. All fields that belong
		 * to the same class should have the same filter group name and that
		 * class should be annotated with {@link JsonFilter}, which specifies
		 * the same group name. 
		 * 
		 * @return The filter group to which the field belongs.
		 */
		String value();
	}
	
	/**
	 * <p>
	 * The custom filter that allows for the addition of new field names.
	 * </p>
	 *
	 * @author John Jenkins
	 */
	private static class ExtendableSerializeExceptFilter
		extends SerializeExceptFilter {
		
		/**
		 * Creates a new filter with the field name.
		 * 
		 * @param fieldName
		 *        The first field for this filter.
		 */
		public ExtendableSerializeExceptFilter(final String fieldName) {
			super(new HashSet<String>(Arrays.asList(fieldName)));
		}
		
		/**
		 * Adds another field to this filter.
		 * 
		 * @param fieldName
		 *        The new field.
		 */
		public void addField(final String fieldName) {
			_propertiesToExclude.add(fieldName);
		}
	}
    
	/**
     * A default version UID to use when serializing an instance of this class.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The set of private fields that should never be serialized.
     */
    private static final SimpleFilterProvider FILTER_PROVIDER =
    	new SimpleFilterProvider();

    /**
     * Creates the object mapper and initializes the filters. This is private
     * as it should only ever be called by Spring via reflection.
     */
    private OmhObjectMapper() {
    	// Ensure that unknown fields are ignored.
    	FILTER_PROVIDER.setFailOnUnknownId(false);
    	
    	// Save the FilterProvider in this ObjectMapper.
        setFilters(FILTER_PROVIDER);
    }
    
    /**
	 * Registers a new field filter and ensures that its desired fields are
	 * never serialized.
	 * 
	 * @param filter
	 *        The filter to add to this ObjectMapper.
	 */
    public static synchronized void register(final Class<?> filterClass) {
    	// Sanitize the input.
    	if(filterClass == null) {
    		throw new IllegalArgumentException("The filter class is null.");
    	}
    	
    	// Cycle through each of the class's fields.
    	for(Field field : filterClass.getFields()) {
    		// Determine if that field has a JacksonFieldFilter annotation.
    		JacksonFieldFilter filter =
    			field.getAnnotation(JacksonFieldFilter.class);
    		
    		// If it does have the annotation.
    		if(filter != null) {
    			// Get the serialized field name.
    			String fieldName;
    			JsonProperty jsonProperty = 
    				field.getAnnotation(JsonProperty.class);
    			if(jsonProperty == null) {
    				fieldName = field.getName();
    			}
    			else {
    				fieldName = jsonProperty.value();
    			}
    			
    			// Get the given group name.
    			String filterGroup = filter.value();
    			
    			// Get the existing filter, if one exists.
    			BeanPropertyFilter propertyFilter =
    				FILTER_PROVIDER.findFilter(filterGroup);
    			
    			// If no such filter exists, update the filter provider with a
    			// new one.
    			if(propertyFilter == null) {
    				FILTER_PROVIDER
    					.addFilter(
    						filterGroup,
    						new ExtendableSerializeExceptFilter(fieldName));
    			}
    			else {
    				// Safely cast the filter as we are the only maintainers of
    				// it, and this is the only type of filter we use.
    				ExtendableSerializeExceptFilter amenableFilter =
    					(ExtendableSerializeExceptFilter) propertyFilter;
    				
    				// Add the field.
    				amenableFilter.addField(fieldName);
    			}
    		}
    	}
    }
}