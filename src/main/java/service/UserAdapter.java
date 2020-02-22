package service;

import model.DbUser;
import model.User;

public interface UserAdapter {
    User adapt(DbUser dbUser);
}