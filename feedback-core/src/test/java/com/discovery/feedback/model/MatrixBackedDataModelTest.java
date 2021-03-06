package com.discovery.feedback.model;

import com.discovery.feedback.model.history.History;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@author} gcapan
 */
public class MatrixBackedDataModelTest {
  private MatrixBackedDataModel dataModel;

  @Before
  public void createDataModel() throws TasteException {
    History userHistory = new History(2, 3);
    History itemHistory = new History(3, 2);

    userHistory.set(12, 23, 1);
    userHistory.set(9, 100, 3);
    userHistory.set(9, 200, 4);
    userHistory.set(9, 23, 4);
    userHistory.set(12, 100, 5);

    itemHistory.set(23, 12, 1);
    itemHistory.set(100, 9, 3);
    itemHistory.set(200, 9, 4);
    itemHistory.set(23, 9, 4);
    itemHistory.set(100, 12, 5);


    dataModel = new MatrixBackedDataModel(userHistory, itemHistory);

  }

  @Test
  public void testGetPreference() throws TasteException {

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
  public void testGetItemsFromUser() throws TasteException {
    FastIDSet user9Items = dataModel.getItemIDsFromUser(9);
    assertTrue(user9Items.contains(100));
    assertTrue(user9Items.contains(200));
    assertTrue(user9Items.contains(23));

    FastIDSet user12Items = dataModel.getItemIDsFromUser(12);
    assertTrue(user12Items.contains(100));
    assertTrue(!user12Items.contains(200));
    assertTrue(user12Items.contains(23));
  }

  @Test
  public void testIterator() throws TasteException {
    Preference[] expected = new Preference[]{
       new GenericPreference(12, 23, 1.0f),
       new GenericPreference(9, 23, 4.0f),
       new GenericPreference(12, 100, 5.0f),
       new GenericPreference(9, 100, 3.0f),
       new GenericPreference(9, 200, 4.0f)
    };
    int i = 0;
    for (Preference preference : dataModel.preferences()) {
//      System.out.println(preference.getUserID()+", "+preference.getItemID()+", "+preference.getValue());
//      assertTrue(preference.getUserID() == expected[i].getUserID());
//      assertTrue(preference.getItemID() == expected[i].getItemID());
//      assertTrue(preference.getValue() == expected[i].getValue());
      i++;
    }

  }
}
