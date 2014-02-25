package es.showcase.client.esnative;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: yearsaaaa
 * ik分词测试
 */
public class EsIKAnalyzerTest {

    private ESClient esClient;

    private Client client;

    private static final Logger LOG = LoggerFactory.getLogger(EsIKAnalyzerTest.class);

    @Before
    public void initESClient(){
        esClient = new ESTransportClient("192.168.8.247");
        client = esClient.getEsClient();
    }

    @Test
    public void testCreateIKMapping() throws IOException {
        //先创建空索引库
        client.admin().indices().prepareCreate("index_ik").execute().actionGet();
        PutMappingRequest mappingRequest = Requests.putMappingRequest("index_ik").type("news").source(buildIKMapping());
        client.admin().indices().putMapping(mappingRequest).actionGet();
    }

    @Test
    public void testIndexDoc() throws IOException {
        XContentBuilder builder1 = XContentFactory.jsonBuilder()
                                    .startObject()
                                        .field("title", "来自星星的你")
                                        .field("content", "美国留给伊拉克的是个烂摊子吗")
                                    .endObject();
        XContentBuilder builder2 = XContentFactory.jsonBuilder()
                                    .startObject()
                                        .field("title","砖家的呼吁")
                                        .field("content", "公安部：各地校车将享最高路权")
                                    .endObject();
        XContentBuilder builder3 = XContentFactory.jsonBuilder()
                                    .startObject()
                                        .field("title","中方表示强烈抗议")
                                        .field("content", "中韩渔警冲突调查：韩警平均每天扣1艘中国渔船")
                                    .endObject();
        XContentBuilder builder4 = XContentFactory.jsonBuilder()
                                    .startObject()
                                        .field("title","国内人民幸福安康")
                                        .field("content", "中国驻洛杉矶领事馆遭亚裔男子枪击 嫌犯已自首")
                                    .endObject();
        XContentBuilder builder5 = XContentFactory.jsonBuilder()
                                     .startObject()
                                        .field("title","国外人民水深火热")
                                        .field("content", "我爱我的中国 我的中国也爱我")
                                     .endObject();

        BulkRequestBuilder bulkRequest = client.prepareBulk()
                .add(client.prepareIndex("index_ik", "news", "1").setSource(builder1))
                .add(client.prepareIndex("index_ik", "news", "2").setSource(builder2))
                .add(client.prepareIndex("index_ik", "news", "3").setSource(builder3))
                .add(client.prepareIndex("index_ik", "news", "4").setSource(builder4))
                .add(client.prepareIndex("index_ik", "news", "5").setSource(builder5));
        BulkResponse bulkResponse =  bulkRequest.execute().actionGet();

        if(bulkResponse.hasFailures()){

        }
        LOG.info("bulk state : {}",bulkResponse.hashCode());
    }

    @Test
    public void testQueryHighlighting(){
        SearchResponse searchResponse = client.prepareSearch("index_ik","index")
                                            //.setTypes("news","fulltext")  //<<-----不指定表示查询所有类型
                                            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                                            .setQuery(QueryBuilders.queryString("中国"))
                                            .addHighlightedField("content")
                                            .setHighlighterPreTags("<span style=\"color:red\">")
                                            .setHighlighterPostTags("</span>")
                                            .setFrom(0).setSize(20).setExplain(true)
                                            .execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        System.out.println("Current results: " + hits.totalHits());
        for (SearchHit hit : hits) {
            Map<String, HighlightField> result = hit.highlightFields();
            HighlightField titleField = result.get("content");
            Text[] titleTexts =  titleField.fragments();
            String content = "";
            for(Text text : titleTexts){
                content += text;
            }
            System.out.println(content);
        }
    }

    @Test
    public void testIKQuery(){
        AnalyzeResponse analyzeResponse = client.admin().indices().prepareAnalyze("index_ik", "测试elasticsearch中文分词器IK的效果")
                                .setAnalyzer("ik")
                                .execute()
                                .actionGet();
        System.out.println("Token size : " + analyzeResponse.getTokens().size());
        List<AnalyzeResponse.AnalyzeToken> list = analyzeResponse.getTokens();
        for (AnalyzeResponse.AnalyzeToken token : list) {
            System.out.println(token.getTerm());
        }
    }

    private XContentBuilder buildIKMapping() throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("news")
                        .startObject("_all")
                            .field("indexAnalyzer", "ik")
                            .field("searchAnalyzer", "ik")
                            .field("term_vector", "no")
                            .field("store", "false")
                        .endObject()
                        .startObject("properties")
                            .startObject("title")
                                .field("type", "string")
                                .field("store", "yes")
                            .endObject()
                            .startObject("content")
                                .field("type", "string")
                                .field("store", "no")
                                .field("term_vector", "with_positions_offsets")
                                .field("indexAnalyzer", "ik")
                                .field("searchAnalyzer", "ik")
                                .field("include_in_all","true")
                                .field("boost", 8)
                            .endObject()
                            .startObject("createDate")
                                .field("type", "date")
                            .endObject()
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
