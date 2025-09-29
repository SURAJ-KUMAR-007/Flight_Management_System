package com.fms.util;

import io.github.cdimascio.dotenv.Dotenv;

public final class Config {
    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing() // don't crash in CI if .env absent
            .load();

    public static String dbUrl()  { return DOTENV.get("DB_URL"); }
    public static String dbUser() { return DOTENV.get("DB_USER"); }
    public static String dbPass() { return DOTENV.get("DB_PASS"); }

    private Config() {}
}
