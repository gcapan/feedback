package com.discovery.feedback.rest.adapters;

import org.apache.mahout.cf.taste.impl.common.FastIDSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class Id {
  private final long id;

  public Id(long id) {
    this.id = id;
  }

  public Id() {
    this.id = Long.MIN_VALUE;
  }

  public long getId() {
    return id;
  }

  public static Id[] toIdArray(FastIDSet fastIDSet) {
    if(fastIDSet == null || fastIDSet.size() == 0)
      return new Id[0];

    Id[] ids = new Id[fastIDSet.size()];
    int i = 0;
    for(Long id : fastIDSet) {
      ids[i++] = new Id(id);
    }
    return ids;
  }
}
