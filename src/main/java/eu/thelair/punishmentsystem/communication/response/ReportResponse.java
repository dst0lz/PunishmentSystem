package eu.thelair.punishmentsystem.communication.response;

public class ReportResponse {
  private String reported;
  private String editor;
  private String reportId;
  private String system;
  private String reason;

  public ReportResponse(String reported, String editor,
                        String reportId, String system, String reason) {
    this.reported = reported;
    this.editor = editor;
    this.reportId = reportId;
    this.system = system;
    this.reason = reason;
  }

  public String reported() {
    return reported;
  }

  public String editor() {
    return editor;
  }

  public String reportId() {
    return reportId;
  }

  public String system() {
    return system;
  }

  public String reason() {
    return reason;
  }

}
