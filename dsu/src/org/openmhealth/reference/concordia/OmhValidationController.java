/*******************************************************************************
 * Copyright 2013 Open mHealth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmhealth.reference.concordia;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.schema.StringSchema;
import name.jenkins.paul.john.concordia.validator.ValidationController;
import name.jenkins.paul.john.concordia.validator.ValidationController.Builder;

/**
 * <p>
 * This class creates and memoizes the custom {@link ValidationController} that
 * will be used throughout this web app for schema validation. This class has
 * no other functionality and is strictly used to directly access the custom
 * {@link ValidationController}.
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