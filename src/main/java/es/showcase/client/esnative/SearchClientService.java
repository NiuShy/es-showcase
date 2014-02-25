package es.showcase.client.esnative;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

/**
 * @author: yearsaaaa
 */
public interface SearchClientService {

    Client getClient();

    void addNewNode(String name);

    void removeNode(String nodeName);

}
