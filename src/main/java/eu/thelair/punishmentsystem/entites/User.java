package eu.thelair.punishmentsystem.entites;

import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.database.MySQL;

public class User {
  private String uuid;
  private String lastIp;
  private String lastName;

  public User(String uuid, String lastName, String lastIp) {
    this.uuid = uuid;
    this.lastIp = lastIp;
    this.lastName = lastName;
  }


  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getLastIp() {
    return lastIp;
  }

  public void setLastIp(String lastIp) {
    this.lastIp = lastIp;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  private MySQL sql() {
    return PunishmentSystem.getInstance().getMySQL();
  }


}
