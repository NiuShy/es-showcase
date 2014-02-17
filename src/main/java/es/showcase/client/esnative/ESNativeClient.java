package es.showcase.client.esnative;

import org.elasticsearch.client.Client;

/**
 * @author: yearsaaaa
 */
public interface ESNativeClient {

    /**
     * Get a reference to an ElasticSearch
     */
    Client getEsClient();

    /**
     * Shutdown the ElasticSearch client
     */
    void shutdown();
}
