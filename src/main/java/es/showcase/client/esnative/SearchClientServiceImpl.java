package es.showcase.client.esnative;

import com.google.common.collect.Maps;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * @author: yearsaaaa
 */
@Service
public class SearchClientServiceImpl implements SearchClientService {

    private Map<String,Node> nodes = Maps.newHashMap();

    private Map<String,Client> clients = Maps.newHashMap();

    private Settings defaultSettings = ImmutableSettings.settingsBuilder()
                                            .put("cluster.name", "test-cluster")
                                            .put("path.data", "E:/elasticSearch/esintegrationtest/data")
                                            .put("path.work", "E:/elasticSearch/esintegrationtest/work")
                                            .put("path.log", "E:/elasticSearch/esintegrationtest/log")
                                            .build();

    @PostConstruct
    public void createNodes() throws Exception{
        System.out.println("==================");
        Settings settings = settingsBuilder()
                                    .put("number_of_shards", 3)
                                    .put("number_of_replicas", 1)
                                    .build();
        startNode("server1",settings);
    }

    public Node startNode(String id, Settings settings) {
        return buildNode(id, settings).start();
    }

    public Node buildNode(String id, Settings settings) {
        String settingsSource = getClass().getName().replace('.', '/') + ".yml";
        Settings finalSettings = settingsBuilder()
                .loadFromClasspath(settingsSource)
                .put(defaultSettings)
                .put(settings)
                .put("name", id)
                .build();

        if (finalSettings.get("gateway.type") == null) {
            // default to non gateway
            finalSettings = settingsBuilder().put(finalSettings).put("gateway.type", "none").build();
        }
        if (finalSettings.get("cluster.routing.schedule") != null) {
            // decrease the routing schedule so new nodes will be added quickly
            finalSettings = settingsBuilder().put(finalSettings).put("cluster.routing.schedule", "50ms").build();
        }
        System.out.println(finalSettings.getAsMap());

        Node node = nodeBuilder()
                .settings(finalSettings)
                .build();
        //nodes.put(id, node);
        //clients.put(id, node.client());
        return node;
    }

    public Client getClient() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addNewNode(String name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeNode(String nodeName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
