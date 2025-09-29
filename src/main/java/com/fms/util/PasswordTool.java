package com.fms.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordTool {
    public static void main(String[] args) {
        String plain = "Admin@123";             // change if you want
        String hash = BCrypt.hashpw(plain, BCrypt.gensalt(12));
        System.out.println(hash);
    }
}
