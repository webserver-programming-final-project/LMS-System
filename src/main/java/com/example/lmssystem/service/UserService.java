package com.example.lmssystem.service;

import com.example.lmssystem.entity.User;
import com.example.lmssystem.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    public User login(String email, String password) {
        UserRepository userRepository = new UserRepository();
        User user = userRepository.findUserByEmail(email);
        if (user == null) throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        boolean isPasswordMatch = BCrypt.checkpw(password, user.getPassword());
        if (!isPasswordMatch) throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        return user;
    }
    public User register(String email, String password, String name, boolean isProfessor) {
        UserRepository userRepository = new UserRepository();
        boolean isAlreadyExist = userRepository.findUserByEmail(email) != null;
        if (isAlreadyExist) throw new IllegalArgumentException("이미 존재하는 계정 입니다.");
        User user = new User();
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setName(name);
        user.setProfessor(isProfessor);
        return userRepository.addUser(user);
    }
}
