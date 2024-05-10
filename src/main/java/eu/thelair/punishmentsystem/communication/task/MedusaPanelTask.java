package eu.thelair.punishmentsystem.communication.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import eu.thelair.punishmentsystem.PunishmentSystem;
import eu.thelair.punishmentsystem.communication.response.ReportResponse;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MedusaPanelTask implements Runnable {
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final OkHttpClient CLIENT = new OkHttpClient();

  @Override
  public void run() {
    while (true) {

      for (ReportResponse reportResponse : post()) {
        if (reportResponse != null) {
          for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
            if (!all.hasPermission("punishment.notify.report")) continue;
            TextComponent textComponent = new TextComponent();
            if (reportResponse.reason().equals("Custom") || reportResponse.reason().equals("Intern") || reportResponse.reason().equals("EML")) {
              textComponent.setText(PunishmentSystem.PREFIX + "§7ID: §8[§c#" + reportResponse.reportId() + "§8] §7(" + reportResponse.system() + ") §7(" + reportResponse.reason() + "§7)");
            } else {
              textComponent.setText(PunishmentSystem.PREFIX + "§7ID: §8[§c#" + reportResponse.reportId() + "§8] §7(" + reportResponse.system() + ")");
            }

            if (reportResponse.system().equals("Medusa")) {
              textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://intern-medusa.thelair.eu/report?id=" + reportResponse.reportId()));
            } else if (reportResponse.system().equals("Poseidon")) {
              textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://intern-medusa.thelair.eu/poseidon?id=" + reportResponse.reportId()));
            }

            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aKlick zum kopieren")));

            all.sendMessage(PunishmentSystem.PREFIX + "§c" + reportResponse.editor() + " §7hat den Report von §9" + reportResponse.reported() + " §7bearbeitet");
            all.sendMessage(textComponent);
          }
        }
      }

      try {
        Thread.sleep(TimeUnit.SECONDS.toMillis(10));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private ReportResponse[] post() {
    RequestBody body = RequestBody.create("", JSON);
    Request request = new Request.Builder()
            .url("http://193.187.255.86:8888/proxy")
            .post(body)
            .build();
    try (Response response = CLIENT.newCall(request).execute()) {
      try {
        if (response.body() != null) {
          String bodyString = response.body().string();
          return GSON.fromJson(bodyString, ReportResponse[].class);
        }
      } catch (JsonSyntaxException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ReportResponse[]{};
  }


}
