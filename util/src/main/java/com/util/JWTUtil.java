package com.util;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Utility class to generate/verify RSA keys and JWT tokens.
 * 
 */
public class JWTUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JWTUtil.class);
	private static final String ISSUER = "ETELLER";
	private static final String SUBJECT = "ETELLER";
	private static final String AUDIENCE = "SalesPlatformCampaigns";
	private static final String CLAIM_NAME_COUNTRY = "ctry";
	private static final String ALGORITHM_RSA = "RSA";
	
	private static final String PRIVATE_KEY_FILE_NAME = "private.key";
	private static final String PUBLIC_KEY_FILE_NAME = "public_base64.key";

	/**
	 * Generates RSA keys (public and private) and save them in context path.
	 * Public key will be stored in Base64 format for portability.
	 * @param keyAlgorithm
	 * @param numBits
	 * @throws IOException
	 */
	private static void generateKeys(String keyAlgorithm, int numBits, File privateKeyFile, File publicKeyFile) throws IOException {

		try {
			
			// Get the public/private key pair
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyAlgorithm);
			keyGen.initialize(numBits);
			KeyPair keyPair = keyGen.genKeyPair();
			// PrivateKey format will be PKCS#8
			PrivateKey privateKey = keyPair.getPrivate();
			// PublicKey format will be X.509
			PublicKey publicKey = keyPair.getPublic();
			LOGGER.debug("\n\nGenerating key/value pair using " + privateKey.getAlgorithm() + " algorithm.");

			// Get the bytes of the public and private keys
			byte[] privateKeyBytes = privateKey.getEncoded();
			byte[] publicKeyBytes = publicKey.getEncoded();

			FileUtils.writeByteArrayToFile(privateKeyFile, privateKeyBytes);
			LOGGER.debug("PrivateKey file generated @ " + privateKeyFile.getAbsolutePath());		
			FileUtils.writeByteArrayToFile(publicKeyFile, Base64.encodeBase64(publicKeyBytes));
			LOGGER.debug("PublicKey with Base64 encoding file generated @ "	+ publicKeyFile.getAbsolutePath());

		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("No such algorithm: " + keyAlgorithm);
		}
	}

	/**
	 * Generates JWT token.
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 */
	private static String generateJWTToken(String keyAlgorithm,
			File privateKeyFile,
			File publicKeyFile,
			String issuer,
			String subject,
			String[] audience,
			Date expiresAt,
			String countryCode) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		
		KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(FileUtils.readFileToByteArray(privateKeyFile));
		PrivateKey privateKeyObj = keyFactory.generatePrivate(privateKeySpec);
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(FileUtils.readFileToString(publicKeyFile)));
		PublicKey publicKeyObj = keyFactory.generatePublic(publicKeySpec);

		RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyObj;
		RSAPublicKey publicKey = (RSAPublicKey) publicKeyObj;

		Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
		
		return JWT.create()
				.withIssuer(issuer)
				.withSubject(subject)
				.withAudience(audience)
				.withExpiresAt(expiresAt)
				.withClaim(CLAIM_NAME_COUNTRY, countryCode)
				.sign(algorithm);
	}

	/**
	 * Validates only token payload structure.
	 * @param token
	 * @return
	 * @throws JWTDecodeException
	 */
	private static boolean isValidTokenPayloadStructure(String token) throws JWTDecodeException {
		
		DecodedJWT jwt = JWT.decode(token);
		String issuer = jwt.getIssuer();
		String subject = jwt.getSubject();
		List<String> audience = jwt.getAudience();
		Date expiresAt = jwt.getExpiresAt();
				
		return ISSUER.equalsIgnoreCase(issuer) 
				&& ISSUER.equalsIgnoreCase(subject) 
				&& audience != null
				&& audience.size() > 0 && AUDIENCE.equalsIgnoreCase(audience.get(0))				
				&& expiresAt != null
				&& expiresAt.after(Calendar.getInstance().getTime())
				&& StringUtils.isNotEmpty(jwt.getClaim(CLAIM_NAME_COUNTRY).asString());
	}

	/**
	 * Verifies JWT token.
	 * @param token
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 */
	private static void verifyToken(String keyAlgorithm, 			
			File publicKeyFile,
			String token,
			String issuer,
			String subject,
			String[] audience,			
			String countryCode
			) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException{
		
		LOGGER.debug("\n\nVerifying JWT token.");
		KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);	
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(FileUtils.readFileToString(publicKeyFile)));
		PublicKey publicKeyObj = keyFactory.generatePublic(publicKeySpec);	
		RSAPublicKey publicKey = (RSAPublicKey) publicKeyObj;

		Algorithm algorithm = Algorithm.RSA256(publicKey, null);
		
		JWTVerifier verifier = JWT.require(algorithm)
								.withIssuer(issuer)
								.withSubject(subject)
								.withAudience(audience)
								.withClaim(CLAIM_NAME_COUNTRY, countryCode)
								.build();		
		verifier.verify(token);
		//Verify with multiple audiences
		for (String audi : audience) {
			try {
				verifier = JWT.require(algorithm)
						.withAudience(audi)
						.acceptLeeway(300)
						.build();
				verifier.verify(token);
				break;
			}catch (Exception e) {
				LOGGER.error("Exception occured while verifying JWT token.", e);
				return;
			}
		}
		LOGGER.debug("\n\nJWT token verified successfully.");
	}
	
	private static void displayToken(String token){
		
		DecodedJWT jwt = JWT.decode(token);
		String issuer = jwt.getIssuer();
		String subject = jwt.getSubject();
		List<String> audience = jwt.getAudience();
		Date expiresAt = jwt.getExpiresAt();
		LOGGER.debug("\n\nJWT Token Content:");
		LOGGER.debug("================================= :: JWT Token Header :: =================================");
		LOGGER.debug("algorithm (alg) : " + jwt.getAlgorithm());
		LOGGER.debug("type (typ) : " + jwt.getAlgorithm());
		
		LOGGER.debug("\n\n================================= :: JWT Token Payload :: =================================");
		LOGGER.debug("issuer (iss) : " + issuer);
		LOGGER.debug("subject (sub) : " + subject);
		LOGGER.debug("audience (aud) : " + audience);
		LOGGER.debug("expiresAt (exp) : " + expiresAt);
		LOGGER.debug("country (ctry) : " + jwt.getClaim(CLAIM_NAME_COUNTRY).asString());
	}
	
	/**
	 * Displays available security providers.
	 */
	private static void displayProviders(){
		StringBuffer sb = new StringBuffer();
		Provider[] p = Security.getProviders();
		for (int i = 0; i < p.length; i++) {
		  sb.append("\n\nProvider : " + p[i].toString() + "\n");
		  Set s = p[i].keySet();
		  Object[] o = s.toArray();
		  Arrays.sort(o);
		  for (int j = 1; j < o.length; j++) {
		    sb.append(o[j].toString() + ", ");
		  }
		}
		LOGGER.debug(sb.toString());
	}
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		// 0. Display Security providers
		// JWTUtil.displayProviders();
		
		// 1. Generate a 1024-bit RSA key pair
		JWTUtil.generateKeys(ALGORITHM_RSA, 
				2048, 
				new File(PRIVATE_KEY_FILE_NAME), 
				new File(PUBLIC_KEY_FILE_NAME));
		
		// 2. Generate JWT Token
		Date expiresAt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(expiresAt);
		c.add(Calendar.YEAR, 1);
		expiresAt = c.getTime(); 		
		String token = JWTUtil.generateJWTToken(ALGORITHM_RSA,
				new File(PRIVATE_KEY_FILE_NAME),
				new File(PUBLIC_KEY_FILE_NAME),
				ISSUER,
				SUBJECT,
				new String[] {AUDIENCE},
				expiresAt,
				"HU");			
		LOGGER.debug("\n\nJWT Token : "+token);
		
		// 3. Validate Token payload structure
		boolean isValidPayload = JWTUtil.isValidTokenPayloadStructure(token);
		LOGGER.debug("\n\nPayload structure is valid : "+ isValidPayload);
		
		// 4. Verify Token
		JWTUtil.verifyToken(ALGORITHM_RSA,			
				new File(PUBLIC_KEY_FILE_NAME),
				token,
				ISSUER,
				SUBJECT,
				new String[] {AUDIENCE},			
				"HU"
				);		
		
		// 5. Display token
		JWTUtil.displayToken(token);
	}
}