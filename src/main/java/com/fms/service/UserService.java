package com.fms.service;

import com.fms.model.LoggedInUser;

import java.util.Scanner;

public interface UserService {
    LoggedInUser login(Scanner scanner);
    void register(Scanner scanner);
}