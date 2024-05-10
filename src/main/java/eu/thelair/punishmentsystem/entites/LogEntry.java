package eu.thelair.punishmentsystem.entites;

import java.sql.Timestamp;

public class LogEntry {
  private long id;
  private String banSymbol;
  private Template template;
  private Timestamp since;
  private String bannerName;
  private String logUserUUID;
  private Long duration;
  private String addendum;

  public LogEntry(String banSymbol, Template template, Timestamp since, String bannerName, String logUserUUID, Long duration, String addendum) {
    this.banSymbol = banSymbol;
    this.template = template;
    this.since = since;
    this.bannerName = bannerName;
    this.logUserUUID = logUserUUID;
    this.duration = duration;
    this.addendum = addendum;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getBanSymbol() {
    return banSymbol;
  }

  public void setBanSymbol(String banSymbol) {
    this.banSymbol = banSymbol;
  }

  public Template getTemplate() {
    return template;
  }

  public void setTemplate(Template template) {
    this.template = template;
  }

  public Timestamp getSince() {
    return since;
  }

  public void setSince(Timestamp since) {
    this.since = since;
  }

  public String getBannerName() {
    return bannerName;
  }

  public void setBannerName(String bannerName) {
    this.bannerName = bannerName;
  }

  public String getLogUserUUID() {
    return logUserUUID;
  }

  public void setLogUserUUID(String logUserUUID) {
    this.logUserUUID = logUserUUID;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  public String getAddendum() {
    return addendum;
  }

  public void setAddendum(String addendum) {
    this.addendum = addendum;
  }
}
