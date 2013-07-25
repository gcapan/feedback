package com.discovery.feedback.model;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.impl.TasteTestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;




/**
 * {@author} gcapan
 */
public class MatrixBackedDataModelTest extends TasteTestCase {
  private MatrixBackedDataModel dataModel;
  @Before
  public void createDataModel() throws TasteException {
    dataModel = new MatrixBackedDataModel(2, 3, 200, 12);
    dataModel.setPreference(12, 23, 1);
    dataModel.setPreference(9, 100, 3);
    dataModel.setPreference(9, 200, 4);
    dataModel.setPreference(9, 23, 4);
    dataModel.setPreference(12, 100, 5);

  }

  @Test
  public void testGetPreference() throws TasteException{

    PreferenceArray user9Preferences = dataModel.getPreferencesFromUser(9);
    assertTrue(user9Preferences.hasPrefWithItemID(100));
    assertTrue(user9Preferences.hasPrefWithItemID(200));

    PreferenceArray item100Preferences = dataModel.getPreferencesForItem(100);
    assertTrue(item100Preferences.hasPrefWithUserID(9));
    assertTrue(item100Preferences.hasPrefWithUserID(12));

    assertEquals(1f, dataModel.getPreferenceValue(12, 23), 0);
    assertEquals(3f, dataModel.getPreferenceValue(9, 100), 0);
    assertEquals(4f, dataModel.getPreferenceValue(9, 200), 0);
    assertEquals(4f, dataModel.getPreferenceValue(9, 23), 0);
    assertEquals(5f, dataModel.getPreferenceValue(12, 100), 0);


  }

  @Test
  public void testIterator() throws TasteException{
    Preference[] expected = new Preference[]{
       new GenericPreference(12, 23, 1.0f),
       new GenericPreference(9, 23, 4.0f),
       new GenericPreference(12, 100, 5.0f),
       new GenericPreference(9, 100, 3.0f),
       new GenericPreference(9, 200, 4.0f)
    };
    int i = 0;
    for(Preference preference:dataModel.preferences()){
//      System.out.println(preference.getUserID()+", "+preference.getItemID()+", "+preference.getValue());
      assertTrue(preference.getUserID() == expected[i].getUserID());
      assertTrue(preference.getItemID() == expected[i].getItemID());
      assertTrue(preference.getValue() == expected[i].getValue());
      i++;
    }

  }
}
