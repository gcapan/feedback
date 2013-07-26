package com.discovery.feedback.rest.adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RecommendedItem {
  private final long itemID;
  private final float value;

  public RecommendedItem(long itemID, float value) {
    this.itemID = itemID;
    this.value = value;
  }

  public RecommendedItem() {
    this.itemID = Long.MIN_VALUE;
    this.value = Float.MIN_VALUE;
  }
}
