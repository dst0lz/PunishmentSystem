package eu.thelair.punishmentsystem.entites;

import java.sql.Timestamp;

public class Ban {
  private long id;
  private String symbol;
  private User banned;
  private User banner;
  private Timestamp from;
  private Timestamp to;
  private Template template;
  private String addendum;

  public Ban(String symbol, User banned, User banner, Timestamp from, Timestamp to, Template template, String addendum) {
    this.symbol = symbol;
    this.banned = banned;
    this.banner = banner;
    this.from = from;
    this.to = to;
    this.template = template;
    this.addendum = addendum;
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getSymbol() {
    return this.symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public User getBanned() {
    return this.banned;
  }

  public void setBanned(User banned) {
    this.banned = banned;
  }

  public User getBanner() {
    return this.banner;
  }

  public void setBanner(User banner) {
    this.banner = banner;
  }

  public Timestamp getFrom() {
    return this.from;
  }

  public void setFrom(Timestamp from) {
    this.from = from;
  }

  public Timestamp getTo() {
    return this.to;
  }

  public void setTo(Timestamp to) {
    this.to = to;
  }

  public Template getTemplate() {
    return this.template;
  }

  public void setTemplate(Template template) {
    this.template = template;
  }

  public String getAddendum() {
    return this.addendum;
  }

  public void setAddendum(String addendum) {
    this.addendum = addendum;
  }
}
