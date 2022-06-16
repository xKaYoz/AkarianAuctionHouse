package net.akarian.auctionhouse.utils;

public enum DatabaseType {
    FILE("FILE"), MYSQL("MYSQL"), MYSQL2FILE("MYSQL2FILE"), FILE2MYSQL("FILE2MYSQL");

    private final String str;

    DatabaseType(final String str) {
        this.str = str;
    }

    public static DatabaseType getByStr(String str) {
        for (DatabaseType db : DatabaseType.values()) {
            if (db.str.equalsIgnoreCase(str)) {
                return db;
            }
        }
        return null;
    }

    public String getStr() {
        return str;
    }
}
