package es.showcase;

import es.showcase.basetest.AbstractSearchJUnit4SpringContextTests;
import org.elasticsearch.common.network.NetworkUtils;
import org.junit.Test;

import java.io.File;

/**
 * @author: yearsaaaa
 */
public class AppTest extends AbstractSearchJUnit4SpringContextTests{

    @Test
    public void testHello(){
        System.out.println("Hello World");
    }
}
