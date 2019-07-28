package verifier;

import entity.UserService;

public class UserVerifierImpl implements UserVerifier{

    @Override
    public boolean isNotNull(UserService userService) {
        return userService != null;
    }
}
