package com.discovery.feedback.rest.adapters;

import org.apache.mahout.cf.taste.model.PreferenceArray;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Preference {
  private long userId;
  private long itemId;
  private float value;

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

  public static Preference[] toPreferenceArray(PreferenceArray pa) {

    if(pa == null || pa.length() == 0)
      return new Preference[0];

    Preference[] prefArr = new Preference[pa.length()];
    int i = 0;
    for (org.apache.mahout.cf.taste.model.Preference preference : pa) {
      prefArr[i] = new Preference(preference.getUserID(), preference.getItemID(), preference.getValue());
      i++;
    }

    return prefArr;
  }
}
