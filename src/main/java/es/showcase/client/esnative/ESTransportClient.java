package es.showcase.client.esnative;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * @author: yearsaaaa
 */
public class ESTransportClient implements ESClient {

    private Client client;
    private String esHost;

    public ESTransportClient(String esHost){
        this.esHost = esHost;
    }

    /**
     * 用TransportClient方式与es集群通信的话只要开启了client.transport.sniff功能就可以了
     * 或者指定集群的名称也行，两者选其一就可以了
     * 但是setting设置的其他参数却没有起效果
     *
     */
    @Override
    public Client getEsClient() {
        if(null == client){
            final Settings settings = ImmutableSettings.settingsBuilder()
                        .put("client.transport.sniff", true)      //开启嗅探es集群状态,把集群中所有节点ip地址添加到客户端中去,并且会自动添加新加入到集群中的es节点
                    //.put("number_of_shards",3)              //<<---------和Node方式一样都没有起到效果
                    //.put("number_of_replicas",0)
                    .put("cluster.name", "elasticsearch-mchen")
                    .build();
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
