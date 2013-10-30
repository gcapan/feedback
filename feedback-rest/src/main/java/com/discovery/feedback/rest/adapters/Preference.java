package com.discovery.feedback.rest.adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class Preference {
  private final long userId;
  private final long itemId;
  private final float value;

  public Preference(long userId, long itemId, float value) {
    this.userId = userId;
    this.itemId = itemId;
    this.value = value;
  }

  public Preference() {
    this.userId = Long.MIN_VALUE;
    this.itemId = Long.MIN_VALUE;
    this.value = Float.MIN_VALUE;
  }

  public long getUserId() {
    return userId;
  }

  public long getItemId() {
    return itemId;
  }

  public float getValue() {
    return value;
  }
}
