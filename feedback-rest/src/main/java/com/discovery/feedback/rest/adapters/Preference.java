package com.discovery.feedback.rest.adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Preference {
  private final long userID;
  private final long itemID;
  private final float value;

  public Preference(long userID, long itemID, float value) {
    this.userID = userID;
    this.itemID = itemID;
    this.value = value;
  }

  public Preference() {
    this.userID = Long.MIN_VALUE;
    this.itemID = Long.MIN_VALUE;
    this.value = Float.MIN_VALUE;
  }

  public long getUserID() {
    return userID;
  }

  public long getItemID() {
    return itemID;
  }

  public float getValue() {
    return value;
  }
}
