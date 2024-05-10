package eu.thelair.punishmentsystem.cache;

import java.util.Map;

public interface Cache<T> {

  void add(T obj);

  void remove(T obj);

  boolean contains(T obj);

  boolean containsKey(String key);

  Map<?, T> getMap();

  T get(String key);

}
