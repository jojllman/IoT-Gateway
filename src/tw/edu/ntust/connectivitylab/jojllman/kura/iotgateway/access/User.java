package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

/**
 * Created by jojllman on 2016/4/15.
 */

public class User {
    private static final Logger s_logger = LoggerFactory.getLogger(User.class);
    private static final int iterations = 20*1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;
    private static String getSaltedHash(String passwd) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        // store the salt with the password
        return Base64.encodeBase64String(salt) + "$" + hash(passwd, salt);
    }
    private static boolean check(String passwd, String stored) throws Exception{
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException(
                    "The stored password have the form 'salt$hash'");
        }
        String hashOfInput = hash(passwd, Base64.decodeBase64(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }
    private static String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, iterations, desiredKeyLen)
        );
        return Base64.encodeBase64String(key.getEncoded());
    }

    private String username;
    private String userId;
    private String password;
    private Group group;

    public User() {

    }

    public Group setGroup(Group group) {
        this.group = group;
        return this.group;
    }
    public Group getGroup() {
        return  group;
    }
    public String setUsername(String name) {
        this.username = name;
        return this.username;
    }
    public String getUsername() {
        return this.username;
    }
    public String setUserId(String id) {
        this.userId = id;
        return this.userId;
    }
    public String getUserId() {
        return this.userId;
    }
}
