package org.openmhealth.reference.domain;

import java.net.URI;
import java.util.UUID;

import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A third-party system that has registered itself to request access for users'
 * data.
 * </p>
 * 
 * @author John Jenkins
 */
public class ThirdParty implements OmhObject {
	/**
	 * The version of this class used for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The JSON key for the identifier of a user that created this third-party.
	 */
	public static final String JSON_KEY_OWNER = "owner";
	/**
	 * The JSON key for the ID.
	 */
	public static final String JSON_KEY_ID = "uid";
	/**
	 * The JSON key for the shared secret.
	 */
	public static final String JSON_KEY_SHARED_SECRET = "sharedSecret";
	/**
	 * The JSON key for the URI.
	 */
	public static final String JSON_KEY_REDIRECT_URI = "redirectUri";
	/**
	 * The JSON key for the name.
	 */
	public static final String JSON_KEY_NAME = "name";
	/**
	 * The JSON key for the description.
	 */
	public static final String JSON_KEY_DESCRIPTION = "description";

	/**
	 * The user that created this third-party identity.
	 */
	@JsonProperty(JSON_KEY_OWNER)
	private final String owner;
	/**
	 * The unique identifier for this third-party.
	 */
	@JsonProperty(JSON_KEY_ID)
	private final String id;
	/**
	 * The shared secret for this third-party that will be used
	 */
	@JsonProperty(JSON_KEY_SHARED_SECRET)
	private final String sharedSecret;
	/**
	 * The URI to use to redirect the user after authorization has been granted
	 * or not.
	 */
	@JsonProperty(JSON_KEY_REDIRECT_URI)
	private final URI redirectUri;
	/**
	 * A user-friendly name for this third-party.
	 */
	@JsonProperty(JSON_KEY_NAME)
	private final String name;
	/**
	 * A user-friendly description for this third-party.
	 */
	@JsonProperty(JSON_KEY_DESCRIPTION)
	private final String description;

	/**
	 * Creates a new third-party entity created by some user with a given
	 * redirect URI.
	 * 
	 * @param owner
	 *        The user creating this third-party entity.
	 * 
	 * @param name
	 *        A user-friendly name for this third-party.
	 * 
	 * @param description
	 *        A user-friendly explanation of who this third-party is.
	 * 
	 * @param redirectUri
	 *        The URI to use to redirect the user to after authorization has or
	 *        has not been granted.
	 * 
	 * @throws OmhException
	 *         Any of the parameters is null or empty.
	 */
	public ThirdParty(
		final String owner,
		final String name,
		final String description,
		final URI redirectUri)
		throws OmhException {

		this(
			owner, 
			UUID.randomUUID().toString(), 
			UUID.randomUUID().toString(), 
			name,
			description,
			redirectUri);
	}
	
	/**
	 * Creates a new third-party from an already serialized third-party object.
	 * This should only be used by the deserializer and should not be exposed
	 * to external users even through child classes.
	 * 
	 * @param owner
	 *        The ID for the user that created this third-party.
	 * 
	 * @param id
	 *        The unique ID for this third-party
	 * 
	 * @param sharedSecret
	 *        The secret that will be used to authenticate this third-party.
	 * 
	 * @param name
	 *        A user-friendly name for this third-party.
	 * 
	 * @param description
	 *        A user-friendly explanation of who this third-party is.
	 * 
	 * @param redirectUri
	 *        The URI to redirect the user back to after they have granted or
	 *        rejected this third-party's authorization request.
	 * 
	 * @throws OmhException
	 *         Any of the parameters is null or empty.
	 *         
	 * @see #ThirdParty(String, String, String, URI)
	 */
	@JsonCreator
	protected ThirdParty(
		@JsonProperty(JSON_KEY_OWNER) final String owner,
		@JsonProperty(JSON_KEY_ID) final String id,
		@JsonProperty(JSON_KEY_SHARED_SECRET) final String sharedSecret,
		@JsonProperty(JSON_KEY_NAME) final String name,
		@JsonProperty(JSON_KEY_DESCRIPTION) final String description,
		@JsonProperty(JSON_KEY_REDIRECT_URI) final URI redirectUri)
		throws OmhException {

		// Validate the owner.
		if(owner == null) {
			throw new OmhException("The owner is null.");
		}
		else {
			this.owner = owner;
		}
		
		// Validate the redirect URI.
		if(redirectUri == null) {
			throw new OmhException("The redirect URI is null.");
		}
		else {
			this.redirectUri = redirectUri;
		}
		
		// Validate the ID.
		if(id == null) {
			throw new OmhException("The ID is null.");
		}
		else {
			String idTrimmed = id.trim();
			
			if(idTrimmed.length() == 0) {
				throw new OmhException("The ID is empty.");
			}
			else {
				this.id = idTrimmed;
			}
		}
		
		// Validate the shared secret.
		if(sharedSecret == null) {
			throw new OmhException("The shared secret is null.");
		}
		else {
			String sharedSecretTrimmed = sharedSecret.trim();
			
			if(sharedSecretTrimmed.length() == 0) {
				throw new OmhException("The shared secret is empty.");
			}
			else {
				this.sharedSecret = sharedSecretTrimmed;
			}
		}
		
		// Validate the name.
		if(name == null) {
			throw new OmhException("The name is null.");
		}
		else {
			String nameTrimmed = name.trim();
			
			if(nameTrimmed.length() == 0) {
				throw new OmhException("The name is empty.");
			}
			else {
				this.name = nameTrimmed;
			}
		}
		
		// Validate the description.
		if(description == null) {
			throw new OmhException("The description is null.");
		}
		else {
			String descriptionTrimmed = description.trim();
			
			if(descriptionTrimmed.length() == 0) {
				throw new OmhException("The description is empty.");
			}
			else {
				this.description = descriptionTrimmed;
			}
		}
	}
	
	/**
	 * Returns the unique identifier for this third-party.
	 * 
	 * @return The unique identifier for this third-party.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the shared secret.
	 * 
	 * @return The shared secret for the third-party.
	 */
	public String getSecret() {
		return sharedSecret;
	}
	
	/**
	 * Returns the redirect URI.
	 * 
	 * @return The redirect URI.
	 */
	public URI getRedirectUri() {
		return redirectUri;
	}
	
	/**
	 * Returns the name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}
}