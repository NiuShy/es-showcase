package es.showcase.client.esnative;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.settings.UpdateSettingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author: yearsaaaa
 */
public class EsMappingTest {

    private ESClient esClient;

    private Client client;

    @Before
    public void initESClient(){
        //esClient = new ESNodeClient();
        esClient = new ESTransportClient("192.168.8.247");
        client = esClient.getEsClient();
    }

    @Test
    public void testCreateMapping() throws IOException {
        //先创建空索引库
        client.admin().indices().prepareCreate("index_mapping").execute().actionGet();
        PutMappingRequest mappingRequest = Requests.putMappingRequest("index_mapping").type("product").source(buildMapping());
        client.admin().indices().putMapping(mappingRequest).actionGet();
    }

    @Test
    public void testGetMapping() throws IOException {
        ClusterState cs = client.admin().cluster().prepareState()
                        .setFilterIndices("index_mapping").execute().actionGet().getState();
        IndexMetaData imd = cs.getMetaData().index("index_mapping");
        //type的名称
        MappingMetaData mdd = imd.mapping("product");
        System.out.println(mdd.getSourceAsMap());
    }

    @Test
    public void testMappingWithSetting() throws Exception {
        Settings settings = ImmutableSettings.settingsBuilder()
                //5个主分片
                .put("number_of_shards", 3)
                        //测试环境，减少副本提高速度
                .put("number_of_replicas", 0).build();
        //首先创建索引库
        CreateIndexResponse indexresponse = client.admin().indices()
                //这个索引库的名称还必须不包含大写字母
                .prepareCreate("index_shards").setSettings(settings)
                        //这里直接添加type的mapping
                .addMapping("testmapping", getMapping())
                .execute().actionGet();
        System.out.println(indexresponse.isAcknowledged());
    }

        @Test
    public void testUpdateMappingSetting(){
        Settings settings =  ImmutableSettings.settingsBuilder()
                //可以更新的配置还有很多，见elasticsearch官网
                .put("number_of_replicas", 4).build();
        //首先创建索引库
        UpdateSettingsResponse updateSettingsResponse = client.admin().indices()
                .prepareUpdateSettings("index_shards").setSettings(settings).execute().actionGet();
        System.out.println(updateSettingsResponse);
    }

    private static XContentBuilder getMapping() throws Exception{
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("testmapping")
                        .startObject("properties")
                            .startObject("id").field("type", "long").field("store", "yes").endObject()
                            .startObject("type").field("type", "string").field("index", "not_analyzed").endObject()
                            .startObject("catIds").field("type", "integer").endObject()
                        .endObject()
                    .endObject()
                .endObject();
        return mapping;
    }

    private XContentBuilder buildMapping() throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                                    .startObject()
                                        .startObject("product")
                                            .startObject("properties")
                                                .startObject("title").field("type", "string").field("store", "yes").endObject()
                                                .startObject("description").field("type", "string").field("index", "not_analyzed").endObject()
                                                .startObject("price").field("type", "double").endObject()
                                                .startObject("onSale").field("type", "boolean").endObject()
                                                .startObject("type").field("type", "integer").endObject()
                                                .startObject("createDate").field("type", "date").endObject()
                                            .endObject()
                                        .endObject()
                                    .endObject();
        return mapping;
    }

    @After
    public void closeEsClient(){
        client = null;
        esClient.shutdown();
    }
}
