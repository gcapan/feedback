package com.discovery.feedback.rest;

import com.discovery.feedback.model.HBaseDataModel;
import com.discovery.feedback.model.MatrixBackedDataModel;
import org.apache.mahout.cf.taste.common.TasteException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("users")
public class Users {

  @GET
  public String getNumItems() throws TasteException {
    return "";
    //return String.valueOf(new MatrixBackedDataModel().getNumItems());
  }
}