package es.showcase.client.esnative;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * @author: yearsaaaa
 * 通过创建Node节点方式与es通信
 */
public class ESNodeClient implements ESNativeClient{

    private Node node;

    @Override
    public Client getEsClient() {

        if(null == node){
            final Settings settings = ImmutableSettings.settingsBuilder()
                    .put("node.name","test-node").build();
            /**
             * 指定es的集群名称,当启动该节点时,会自动加入指定的es集群
             * NodeBuilder自带了client、data、local、clusterName方法，不过NodeBuilder自带的方法有限
             * 推荐使用ImmutableSettings.settingsBuilder(Map map)或
             * NodeBuilder.nodeBuilder().loadConfigSettings(true)加载yml配置文件方式来实现
             * 需求丰富的配置选项
             */
            node = new NodeBuilder().settings(settings)
                    .clusterName("elasticsearch-mchen")
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
}
