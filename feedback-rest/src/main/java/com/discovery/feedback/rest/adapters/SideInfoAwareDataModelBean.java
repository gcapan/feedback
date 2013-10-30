package com.discovery.feedback.rest.adapters;

import com.discovery.contentdb.matrix.SolrFieldMatrix;
import com.discovery.contentdb.matrix.SolrMatrix;
import com.discovery.contentdb.matrix.TYPE;
import com.discovery.feedback.model.SideInfoAwareDataModel;
import com.discovery.feedback.model.history.History;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import java.io.IOException;

public class SideInfoAwareDataModelBean extends SideInfoAwareDataModel {
  private static SideInfoAwareDataModelBean instance;
  static final SolrServer server = new HttpSolrServer("http://localhost:8983/solr/movielens10m/");
  public SideInfoAwareDataModelBean() throws IOException, SolrServerException {
    super(new History(71567, 65134 /* maxitemid + 1 */), new History(10681, 71568 /* maxuserid + 1 */), null, new SolrMatrix(new SolrFieldMatrix[]{
       new SolrFieldMatrix(server, "id", "name", TYPE.TEXT, false),
       new SolrFieldMatrix(server, "id", "age", TYPE.NUMERICAL, false),
       new SolrFieldMatrix(server, "id", "age", TYPE.NUMERICAL, false),
       new SolrFieldMatrix(server, "id", "categories", TYPE.MULTINOMIAL, true),
       new SolrFieldMatrix(server, "id", "name", TYPE.MULTINOMIAL, true)
    }));
  }

  public static SideInfoAwareDataModelBean getInstance() throws IOException, SolrServerException {
    if(instance == null) {
      instance = new SideInfoAwareDataModelBean();
    }

    return instance;
  }
}
