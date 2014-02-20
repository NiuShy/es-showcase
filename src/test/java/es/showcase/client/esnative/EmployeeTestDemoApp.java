package es.showcase.client.esnative;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.showcase.domain.Employee;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
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

import static org.junit.Assert.assertEquals;


/**
 * Elasticsearch with es-native-api demo application.
 */
public class EmployeeTestDemoApp {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeTestDemoApp.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    private ESNativeClient esNativeClient;

    @Before
    public void initESClient(){
        esNativeClient = new ESTransportClient("192.168.8.247");
    }

    /**
     * 单条数据索引
     * prepareIndex().setSource()只能传入单条数据文档
     * @throws Exception
     */
    @Test
    public void indexSingle() throws Exception {
        Client client = esNativeClient.getEsClient();
        //index 1
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("id","HP-testcase")
                .field("name", "yearsaaaa")
                .field("sex", Employee.Sex.MAN)
                .field("age",20)
                .field("salary",1000)
                .field("birthday","1990-01-01")
                .field("descript","Hello World")
                .endObject();
        client.prepareIndex("company","employees","HP-0xff00")
                .setSource(builder.string())
                .execute().actionGet();

        //index 2
        String emp1 = mapper.writeValueAsString(Employee.CreateFakerEmp().get(0));
        String emp2 = mapper.writeValueAsString(Employee.CreateFakerEmp().get(1));
        String emp3 = mapper.writeValueAsString(Employee.CreateFakerEmp().get(2));
        String emp4 = mapper.writeValueAsString(Employee.CreateFakerEmp().get(3));
        String emp5 = mapper.writeValueAsString(Employee.CreateFakerEmp().get(4));
        client.prepareIndex("company","employees")
                .setSource(emp1)
                .execute().actionGet();
        client.prepareIndex("company","employees")
                .setSource(emp2)
                .execute().actionGet();
        client.prepareIndex("company","employees")
                .setSource(emp3)
                .execute().actionGet();
        client.prepareIndex("company","employees")
                .setSource(emp4)
                .execute().actionGet();
        client.prepareIndex("company","employees")
                .setSource(emp5)
                .execute().actionGet();
        LOG.info("All Document are indexd...");
        //getDocument by id
        GetResponse getResponse = client.prepareGet("company","employees","HP-0xff00")
                .execute().actionGet();
        Map<String,Object> source = getResponse.getSource();
        System.out.println("------------------------------");
        System.out.println("Index: " + getResponse.getIndex());
        System.out.println("Type: " + getResponse.getType());
        System.out.println("Id: " + getResponse.getId());
        System.out.println("Version: " + getResponse.getVersion());
        System.out.println(source);
        System.out.println("------------------------------");
    }

    /**
     * 批量创建索引
     * @throws Exception
     */
    @Test
    public void indexMultipleWithBulk() throws Exception {
        Client client = esNativeClient.getEsClient();
        List<Employee> employees = Employee.CreateFakerEmp();
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for(Employee employee : employees){
            IndexRequest indexRequest = client.prepareIndex("company","employees")
                    .setSource(mapper.writeValueAsBytes(employee))
                    .request();
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            LOG.error("批量创建索引错误！");
        }
    }

    @Test
    public void search(){
        Client client = esNativeClient.getEsClient();
        SearchResponse searchResponse = client.prepareSearch("company")
                .setTypes("employees")
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(QueryBuilders.fieldQuery("descript", "easy"))
                .setFilter(FilterBuilders.rangeFilter("salary").from(6000).to(7000))
                .setFrom(0).setSize(20).setExplain(true)        //setSize分页显示,setExplain是否按查询匹配度排序
                .execute().actionGet();
        SearchHit[] results = searchResponse.getHits().getHits();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("Current results: " + results.length);
        for (SearchHit hit : results) {
            System.out.println("------------------------------");
            Map<String,Object> result = hit.getSource();
            System.out.println(result);
        }
    }

    /**
     * 高亮显示
     * @throws IOException
     */
    @Test
    public void searchWithHighlighted() throws IOException {
        Client client = esNativeClient.getEsClient();
        SearchResponse searchResponse = client.prepareSearch("company")
                .setTypes("employees")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.fieldQuery("descript","的"))
                .addHighlightedField("descript")
                .setHighlighterPreTags("<span style=\"color:red\">")
                .setHighlighterPostTags("</span>")
                .setFilter(FilterBuilders.rangeFilter("age").from(25).to(30))
                .setFrom(0).setSize(20).setExplain(true)
                .execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        System.out.println("Current results: " + hits.totalHits());
        for(SearchHit searchHit : hits){
            String json = searchHit.getSourceAsString();
            Employee employee = mapper.readValue(json, Employee.class);
            Map<String, HighlightField> result = searchHit.highlightFields();
            HighlightField titleField = result.get("descript");
            Text[] titleTexts =  titleField.fragments();
            String discript = "";
            for(Text text : titleTexts){
                discript += text;
            }
            employee.setDescript(discript);
            System.out.println(employee.getDescript());
        }
    }

    /**
     * QueryBuilders.matchAllQuery()
     * 匹配所有Document的Query
     * @throws IOException
     */
    @Test
    public void testMatchAllQuery() throws IOException {
        QueryBuilder builder = QueryBuilders.matchAllQuery();
        testQuery(builder);
    }

    /**
     * matchQuery根据field的值对Document进行查询
     * 可以看到默认情况下汉字通过matchQuery方法查询时会被分词器分词
     */
    @Test
    public void testMatchQuery() throws IOException {
        QueryBuilder builder1 = QueryBuilders.matchQuery("name","吕梓");
        testQuery(builder1);
        System.out.println("=====================");
        QueryBuilder builder2 = QueryBuilders.matchQuery("descript","抠脚大雪worry");
        testQuery(builder2);
    }

    /**
     * multiMatchQuery中可以指定多个field,是matchQuery的增强版
     * 汉字通过matchQuery方法查询时会被分词器分词
     */
    @Test
    public void testMultiMatchQuery() throws IOException {
        QueryBuilder builder = QueryBuilders.multiMatchQuery("superman再见jack","name","descript");
        testQuery(builder);
    }

    /**
     * matchPhraseQuery是以短语查询
     * 创建索引所使用的field的value中如果有这么一个短语（顺序无差，且连接在一起）
     * 才会被matchPhraseQuery查询出来
     */
    @Test
    public void testMatchPhraseQuery() throws IOException {
        QueryBuilder builder1 = QueryBuilders.matchPhraseQuery("descript","super");
        testQuery(builder1);
        System.out.println("=========================");
        QueryBuilder builder2 = QueryBuilders.matchPhraseQuery("descript","而抠");
        testQuery(builder2);
    }

    /**
     * 从matchQuery和termQuery的查询结果可以看出两者的差别
     * 默认情况下汉字通过termQuery查询时不会被分词(作为完整的词条)
     * 适合单个汉字或是个单词查询
     */
    @Test
    public void testTermQuery() throws IOException {
        QueryBuilder builder1 = QueryBuilders.termQuery("name","吕涵紫");
        testQuery(builder1);
        System.out.println("==========================");
        QueryBuilder builder2 = QueryBuilders.termQuery("name","紫");
        testQuery(builder2);

    }

    /**
     * 指定type和索引的id查询,个人感觉用处不大
     */
    @Test
    public  void testIdsQuery() throws IOException {
        QueryBuilder builder = QueryBuilders.idsQuery("employees").ids("HP-0xff00");
        testQuery(builder);
    }

    public void testQuery(QueryBuilder queryBuilder) throws IOException {
        Client client = esNativeClient.getEsClient();
        SearchResponse response = client.prepareSearch("company")
                .setTypes("employees")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(queryBuilder)
                .setFilter(FilterBuilders.rangeFilter("age").from(20).to(30))
                .setFrom(0).setSize(20).setExplain(true)
                .execute().actionGet();
        SearchHits hits = response.getHits();
        System.out.println("Current results: " + hits.totalHits());
        for(SearchHit searchHit : hits){
            String json = searchHit.getSourceAsString();
            Employee employee = mapper.readValue(json, Employee.class);
            System.out.println(employee);
        }
    }

    @Test
    public void testDeleteIndex(){
        Client client = esNativeClient.getEsClient();
        DeleteResponse response = client.prepareDelete("company", "employees", "HP-0xff00")
                .execute().actionGet();
        System.out.println(response.getId());
        System.out.println(response.isNotFound());
        assertEquals(response.getId(), "HP-0xff00");
    }

    @After
    public void closeEsClient(){
        esNativeClient.shutdown();
    }

}
