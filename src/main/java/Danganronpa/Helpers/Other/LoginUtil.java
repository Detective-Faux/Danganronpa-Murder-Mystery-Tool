package Danganronpa.Helpers.Other;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LoginUtil {
    private static final int SALT_LENGTH = 10;
    private static final LoginUtil instance = new LoginUtil();
    private final HashMap<String, String> userPasswordMap = new HashMap<>(), userSaltMap = new HashMap<>();
    private final HashMap<String, Boolean> userActiveHost = new HashMap<>();

    public LoginUtil(){}
    public static LoginUtil getInst() {
        return instance;
    }

    public boolean isUsernameTaken(String username) {
        return userPasswordMap.containsKey(username);
    }
    public boolean isUserActiveHost(String username) {
        return isUsernameTaken(username) ? userActiveHost.get(username) : false;
    }

    public void registerUser(String username, String password) {
        String salt = RandomStringUtils.randomAlphanumeric(SALT_LENGTH);
        userPasswordMap.put(username, DigestUtils.sha256Hex(password + salt));
        userSaltMap.put(username, salt);
        userActiveHost.put(username, false);
    }
    public boolean isLoginCorrect(String username, String password) {
        if(!userPasswordMap.containsKey(username) || !userSaltMap.containsKey(username)) return false;
        return DigestUtils.sha256Hex(password + userSaltMap.get(username)).equals(userPasswordMap.get(username));
    }
    public void addUserToMaps(List<Object> credentials) {
        userSaltMap.putIfAbsent(credentials.get(0).toString(), credentials.get(1).toString());
        userPasswordMap.putIfAbsent(credentials.get(0).toString(), credentials.get(2).toString());
        userActiveHost.putIfAbsent(credentials.get(0).toString(), Boolean.parseBoolean(credentials.get(3).toString()));
    }
    public List<Object> getUserAsList(String username) {
        return new ArrayList<>(Arrays.asList(username, userSaltMap.get(username), userPasswordMap.get(username), userActiveHost.get(username)));
    }
}
