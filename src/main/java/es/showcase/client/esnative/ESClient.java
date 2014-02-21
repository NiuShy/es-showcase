package es.showcase.client.esnative;

import org.elasticsearch.client.Client;

/**
 * @author: yearsaaaa
 * 有两种方式连接es集群
 * 1. 创建一个嵌入式es节点(Node)来与es集群通信
 * 2. 通过TransportClient来与es建立连接
 */
public interface ESClient {

    /**
     * Get a reference to an ElasticSearch
     */
    Client getEsClient();

    /**
     * Shutdown the ElasticSearch client
     */
    void shutdown();
}
