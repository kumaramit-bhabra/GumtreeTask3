package service;

import model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> getUser(long userId);
}