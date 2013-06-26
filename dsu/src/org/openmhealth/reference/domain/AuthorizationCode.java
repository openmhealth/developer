package org.openmhealth.reference.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.openmhealth.reference.data.ThirdPartyBin;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The authorization code generated for a third-party to give to a user when
 * attempting to gain authorization to some set of data.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
public class AuthorizationCode implements OmhObject {
	/**
	 * The version of this class used for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The default number of milliseconds that a token should live.
	 */
	public static final long DEFAULT_CODE_LIFETIME_MILLIS = 1000 * 60 * 5;

	/**
	 * The JSON key for the identifier of a third-party that this token
	 * references.
	 */
	public static final String JSON_KEY_THIRD_PARTY = "third_party";

	/**
	 * The JSON key for the code.
	 */
	public static final String JSON_KEY_CODE = "code";
	
	/**
	 * The JSON key for the time the token was created.
	 */
	public static final String JSON_KEY_CREATION_TIME = "creation_time";
	/**
	 * The JSON key for the time the token expires.
	 */
	public static final String JSON_KEY_EXPIRATION_TIME = "expiration_time";
	/**
	 * The JSON key for the list of scopes to which this key applies.
	 */
	public static final String JSON_KEY_SCOPES = "scopes";
	/**
	 * The JSON key for the state that was given to the server when the code
	 * was created.
	 */
	public static final String JSON_KEY_STATE = "state";
	
	/**
	 * The unique identifier for the third-party to which this token applies. 
	 */
	@JsonProperty(JSON_KEY_THIRD_PARTY)
	private final String thirdParty;
	/**
	 * The code value.
	 */
	@JsonProperty(JSON_KEY_CODE)
	private final String code;
	/**
	 * The number of milliseconds since the epoch at which time this token was
	 * created.
	 */
	@JsonProperty(JSON_KEY_CREATION_TIME)
	private final long creationTime;
	/**
	 * The number of milliseconds since the epoch at which time this token
	 * expires.
	 */
	@JsonProperty(JSON_KEY_EXPIRATION_TIME)
	private final long expirationTime;
	/**
	 * The set of scopes to which this token applies.
	 */
	@JsonProperty(JSON_KEY_SCOPES)
	private final Set<String> scopes = new HashSet<String>();
	/**
	 * The state given by the third-party when this code was created.
	 */
	@JsonProperty(JSON_KEY_STATE)
	private final String state;

	/**
	 * Creates a new, valid authorization code.
	 * 
	 * @param thirdParty
	 *        The third-party to which this token will apply.
	 * 
	 * @param scopes
	 *        The set of scopes, e.g. schema IDs, that apply to this
	 *        authorization token.
	 * 
	 * @throws OmhException
	 *         A parameter is invalid.
	 */
	public AuthorizationCode(
		final ThirdParty thirdParty,
		final Set<String> scopes,
		final String state) 
		throws OmhException {
		
		// Validate the parameters.
		if(thirdParty == null) {
			throw new OmhException("The third-party is null.");
		}
		else if(scopes == null) {
			throw new OmhException("The scopes is null.");
		}
		else if(scopes.size() == 0) {
			throw
				new OmhException(
					"An authorization token cannot be created without any " +
						"scope.");
		}
		
		// Store the relevant information.
		this.thirdParty = thirdParty.getId();
		this.code = UUID.randomUUID().toString();
		this.creationTime = DateTime.now().getMillis();
		this.expirationTime =
			this.creationTime + DEFAULT_CODE_LIFETIME_MILLIS;
		this.scopes.addAll(scopes);
		this.state = state;
	}
	
	/**
	 * Creates an authorization code presumably from an existing one since all
	 * of the fields are given. To create a new code, it is recommended that
	 * {@link #AuthorizationCode(ThirdParty, Set, String)} be used.
	 * 
	 * @param thirdParty
	 *        The unique identifier for the third-party to which this token
	 *        pertains.
	 * 
	 * @param code
	 *        The code value for this authorization token.
	 * 
	 * @param creationTime
	 *        The number of milliseconds since the epoch at which time this
	 *        token was created.
	 * 
	 * @param expirationTime
	 *        The number of milliseconds since the epoch at which time this
	 *        token expires.
	 * 
	 * @param scopes
	 *        The set of scopes for this token.
	 * 
	 * @param state
	 *        The state given by the third-party when this code was created.
	 * 
	 * @throws OmhException
	 *         A parameter is invalid.
	 * 
	 * @see #AuthorizationCode(ThirdParty, Set, String)
	 */
	@JsonCreator
	public AuthorizationCode(
		@JsonProperty(JSON_KEY_THIRD_PARTY) final String thirdParty,
		@JsonProperty(JSON_KEY_CODE) final String code,
		@JsonProperty(JSON_KEY_CREATION_TIME) final long creationTime,
		@JsonProperty(JSON_KEY_EXPIRATION_TIME) final long expirationTime,
		@JsonProperty(JSON_KEY_SCOPES) final Set<String> scopes,
		@JsonProperty(JSON_KEY_STATE) final String state)
		throws OmhException {

		// Validate the third-party.
		if(thirdParty == null) {
			throw new OmhException("The third-party is null.");
		}
		else {
			this.thirdParty = thirdParty;
		}
		
		// Validate the code.
		if(code == null) {
			throw new OmhException("The code is null.");
		}
		else {
			this.code = code;
		}
		
		// Validate the creation time.
		DateTime creationTimeDateTime = new DateTime(creationTime);
		if(creationTimeDateTime.isAfterNow()) {
			throw
				new OmhException(
					"The token's creation time cannot be in the future.");
		}
		else {
			this.creationTime = creationTime;
		}
		
		// Validate the expiration time.
		if(creationTimeDateTime.isAfter(expirationTime)) {
			throw
				new OmhException(
					"The token's expiration time cannot be before its " +
						"creation time.");
		}
		else {
			this.expirationTime = expirationTime;
		}
		
		// Validate the scopes.
		if(scopes == null) {
			throw new OmhException("The scopes set is null.");
		}
		else if(scopes.size() == 0) {
			throw
				new OmhException(
					"An authorization token cannot be created without any " +
						"scope.");
		}
		else {
			this.scopes.addAll(scopes);
		}
		
		// The state can be anything, so we don't validate it.
		this.state = state;
	}
	
	/**
	 * Returns the unique identifier for the third-party entity.
	 * 
	 * @return The unique identifier for the third-party entity.
	 */
	public String getThirdPartyId() {
		return thirdParty;
	}
	
	/**
	 * Returns the third-party associated with this authorization code.
	 * 
	 * @return The third-party associated with this authorization code. 
	 */
	public ThirdParty getThirdParty() {
		return ThirdPartyBin.getInstance().getThirdParty(thirdParty);
	}

	/**
	 * Returns the code.
	 * 
	 * @return The code.
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Returns the time at which the code was created.
	 * 
	 * @return The time, in milliseconds since the epoch, when this code was
	 * 		   created.
	 */
	public long getCreationTime() {
		return creationTime;
	}
	
	/**
	 * Returns the time at which the code expires.
	 * 
	 * @return The time, in milliseconds since the epoch, when this code
	 *         expires.
	 */
	public long getExpirationTime() {
		return expirationTime;
	}
	
	/**
	 * Returns the set of scopes.
	 * 
	 * @return The, unmodifiable, set of scopes.
	 */
	public Set<String> getScopes() {
		return Collections.unmodifiableSet(scopes);
	}
	
	/**
	 * Returns the state given by the third-party when this request was made.
	 * 
	 * @return The state that is being passed around while the authorization is
	 *         being verified.
	 */
	public String getState() {
		return state;
	}
}