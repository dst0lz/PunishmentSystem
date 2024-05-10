package eu.thelair.punishmentsystem.utils;

import java.util.UUID;

public final class HashGenerator {
  private HashGenerator() {
    // Util class
  }

  public static String getRandomId(int length) {
    char[] chars = UUID.randomUUID().toString().replaceAll("-", "").toCharArray();
    StringBuilder res = new StringBuilder();

    for (int i = 0; i < length; i++) {
      res.append(chars[(int) (Math.random() * chars.length)]);
    }

    return res.toString();
  }


}
