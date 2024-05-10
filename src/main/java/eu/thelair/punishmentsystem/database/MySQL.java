package eu.thelair.punishmentsystem.database;

import eu.thelair.api.database.HikariAdapter;

public final class MySQL extends HikariAdapter {

  public MySQL() {
    super("localhost", "3306", "punishmentsystem");
  }

  @Override
  public void createTables() {
    update("CREATE TABLE IF NOT EXISTS user ("
            + "uuid VARCHAR(36) NOT NULL PRIMARY KEY,"
            + "last_name VARCHAR(16),"
            + "last_ip VARCHAR(15));"
    );

    update("CREATE TABLE IF NOT EXISTS ban ("
            + "ban_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,"
            + "symbol VARCHAR(20),"
            + "banned VARCHAR(36),"
            + "banner VARCHAR(36),"
            + "since TIMESTAMP,"
            + "until TIMESTAMP NULL DEFAULT NULL,"
            + "template INT,"
            + "addendum TEXT);"
    );

    update("CREATE TABLE IF NOT EXISTS template ("
            + "template_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,"
            + "abbreviation VARCHAR(20),"
            + "reason TEXT,"
            + "type VARCHAR(20),"
            + "times VARCHAR(255),"
            + "permanent BOOLEAN);"
    );

    update("CREATE TABLE IF NOT EXISTS log_entry ("
            + "log_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,"
            + "banSymbol VARCHAR(20),"
            + "template_id INT NOT NULL,"
            + "since TIMESTAMP,"
            + "bannerName VARCHAR(16),"
            + "logUserUUID VARCHAR(36),"
            + "duration BIGINT,"
            + "addendum TEXT)"
    );
  }
}
