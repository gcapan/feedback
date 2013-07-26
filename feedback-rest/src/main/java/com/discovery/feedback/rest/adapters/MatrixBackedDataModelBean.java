package com.discovery.feedback.rest.adapters;

import com.discovery.feedback.model.MatrixBackedDataModel;

import javax.ejb.Singleton;
import javax.ejb.Startup;

//@Startup
@Singleton
public class MatrixBackedDataModelBean extends MatrixBackedDataModel {
  public MatrixBackedDataModelBean() {
    super(1000, 1000, 1000, 1000);
  }
}
