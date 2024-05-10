package eu.thelair.punishmentsystem.entites;

import eu.thelair.punishmentsystem.entites.enums.TemplateType;

public class Template {
  private long id;
  private String abbreviation;
  private String reason;
  private Long[] times;
  private TemplateType templateType;
  private boolean permanent;

  public Template(Long id, String abbreviation, String reason, Long[] times, TemplateType templateType, boolean permanent) {
    this.id = id;
    this.abbreviation = abbreviation;
    this.reason = reason;
    this.times = times;
    this.templateType = templateType;
    this.permanent = permanent;
  }

  public long getId() {
    return this.id;
  }

  public String getAbbreviation() {
    return this.abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public String getReason() {
    return this.reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Long[] getTimes() {
    return this.times;
  }

  public void setTimes(Long[] times) {
    this.times = times;
  }

  public TemplateType getTemplateType() {
    return this.templateType;
  }

  public void setTemplateType(TemplateType templateType) {
    this.templateType = templateType;
  }

  public boolean isPermanent() {
    return permanent;
  }

  public void setPermanent(boolean permanent) {
    this.permanent = permanent;
  }
}
