package es.showcase.basetest;

import es.showcase.client.esnative.SearchClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * @author: yearsaaaa
 */
@ContextConfiguration(locations = {"classpath:applicationContext-elasticsearch.xml"})
public class AbstractSearchJUnit4SpringContextTests extends AbstractJUnit4SpringContextTests {

    @Autowired
    //@Qualifier("searchClientService")
    protected SearchClientService searchClientService;

}
