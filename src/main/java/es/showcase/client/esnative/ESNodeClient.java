package es.showcase.client.esnative;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * @author: yearsaaaa
 * 创建Node节点与es通信
 * Node是以虚节点的方式启动的，如果不和集群在同一个网段内的话,不能够自动发现
 * 尝试在Settings里面设置远程的es集群地址,也不能够成功启动,
 */
public class ESNodeClient implements ESClient {

    private Node node;

    @Override
    public Client getEsClient() {

        if(null == node){
            //setting设置的值居然都不起作用
            final Settings settings = ImmutableSettings.settingsBuilder()
                    //.put("index.number_of_shards",2)
                    //.put("index.number_of_replicas",0)
                    //.put("network.bind_host", "localhost")
                    //.put("network.publish_host", "localhost")
                    //.put("node.name", "test-node")
                    .build();
            /**
             * 指定es的集群名称,当启动该节点时,会自动加入指定的es集群
             * NodeBuilder自带了client、data、local、clusterName方法，不过NodeBuilder自带的方法有限
             * 推荐使用ImmutableSettings.settingsBuilder(Map map)或
             * NodeBuilder.nodeBuilder().loadConfigSettings(true)加载yml配置文件方式来实现
             * 需求丰富的配置选项
             */
            node = new NodeBuilder().settings(settings)
                    .clusterName("elasticsearch")
                    .client(true)    //node.client设置为true表示该节点仅仅作为一个客户端但不保存数据
                    .node();
            /**
             * 本地jvm级别的节点,不加入es集群
             * node = new NodeBuilder().local(true).node();
             */
        }
        return node.client();
    }

    @Override
    public void shutdown() {
        getEsClient().close();
        node.close();
        node = null;
    }

    /*public static void main(String[] args){
        ESNodeClient esNodeClient = new ESNodeClient();
        Client client = esNodeClient.getEsClient();
        client.prepareIndex("nodet","tp")
                .setSource("{\"a\":10,\"b\":\"Test\"}")
                .execute().actionGet();
    }*/
}
