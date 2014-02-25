package es.showcase.client.esnative;

import es.showcase.domain.FacetTmp;
import es.showcase.util.JacksonUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: yearsaaaa
 */
public class EsFacetTest {

    private ESClient esClient;

    private Client client;

    private static final Logger LOG = LoggerFactory.getLogger(EsFacetTest.class);

    @Before
    public void initESClient(){
        esClient = new ESTransportClient("192.168.8.247");
        client = esClient.getEsClient();
    }

    @Test
    public void indexFacetTmp(){
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for(int i = 0; i<20 ;i++){
            String json = JacksonUtils.obj2Json(new FacetTmp());
            System.out.println(json);
            IndexRequestBuilder indexRequest = client.prepareIndex("index_facet", "facetmp")
                    .setSource(json)
                    .setId(String.valueOf(i));
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            LOG.info(bulkResponse.buildFailureMessage());
        }
    }

    @Test
    public void testFacet(){
        TermsFacetBuilder facetBuilder = FacetBuilders.termsFacet("typeFacetName");
        facetBuilder.field("type").size(Integer.MAX_VALUE);
        facetBuilder.facetFilter(FilterBuilders.matchAllFilter());
        SearchResponse response = client.prepareSearch("index_facet")
                                            .setTypes("facetmp")
                                            .addFacet(facetBuilder)
                                            .setFilter(FilterBuilders.matchAllFilter())
                                            .execute()
                                            .actionGet();
        Facets facets = response.getFacets();
        TermsFacet facet = (TermsFacet)facets.getFacets().get("typeFacetName");
        for(TermsFacet.Entry tf :facet.getEntries()){
            System.out.println(tf.getTerm()+"\t:\t" + tf.getCount());
        }
    }

    @After
    public void closeEsClient(){
        client = null;
        esClient.shutdown();
    }
}
