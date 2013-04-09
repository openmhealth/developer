package org.openmhealth.reference.concordia;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.StringSchema;
import name.jenkins.paul.john.concordia.validator.ValidationController;
import name.jenkins.paul.john.concordia.validator.ValidationController.Builder;

/**
 * <p>
 * This class creates the custom {@link ValidationController} that will be used
 * throughout this web app. The idea is that the validation controller is
 * static for the system, so there is only one function which retrieves that
 * controller.
 * </p>
 *
 * @author John Jenkins
 */
public class OmhValidationController {
	/**
	 * An immutable validation controller to be used for all Open mHealth
	 * schemas.
	 */
	public static final ValidationController VALIDATION_CONTROLLER;
	static {
		// Create the builder.
		Builder controllerBuilder = new ValidationController.Builder();
		
		// Add our custom validation.
		try {
			controllerBuilder
				.addValidator(StringSchema.class, new EnumValidator());
		}
		catch(ConcordiaException e) {
			throw
				new IllegalStateException(
					"The validation controller could not be built.");
		}
		
		// Memoize the controller.
		VALIDATION_CONTROLLER = controllerBuilder.build();
	}
	
	/**
	 * The default constructor made private so that this class can never be
	 * instantiated.
	 */
	private OmhValidationController() {
		// Do nothing.
	}
}