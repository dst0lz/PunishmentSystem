package eu.thelair.punishmentsystem.communication;

import eu.thelair.punishmentsystem.communication.task.MedusaPanelTask;

public class MedusaPanelCommunication {

  public void startTask() {
    new Thread(new MedusaPanelTask()).start();
  }

}
