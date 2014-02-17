package es.showcase.client.esnative;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * @author: yearsaaaa
 */
public class ESTransportClient implements ESNativeClient{

    private Client client;
    private String esHost;

    public ESTransportClient(String esHost){
        this.esHost = esHost;
    }

    @Override
    public Client getEsClient() {
        if(null == client){
            final Settings settings = ImmutableSettings.settingsBuilder()
                    .put("client.transport.sniff", true)      //开启嗅探es集群状态,把集群中所有节点ip地址添加到客户端中去,并且会自动添加新加入到集群中的es节点
                    .put("cluster.name", "elasticsearch-mchen").build();
            client = new TransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(esHost, 9300));
        }
        return client;
    }

    @Override
    public void shutdown() {
        client.close();
        client = null;
    }
}
