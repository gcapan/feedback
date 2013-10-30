package com.discovery.feedback.rest.adapters;

import com.discovery.feedback.model.MatrixBackedDataModel;
import com.discovery.feedback.model.history.History;

import javax.ejb.Singleton;

@Singleton
class MatrixBackedDataModelBean extends MatrixBackedDataModel {
  public MatrixBackedDataModelBean() {
    super(new History(1000, 1000), new History(1000, 1000));
  }
}
