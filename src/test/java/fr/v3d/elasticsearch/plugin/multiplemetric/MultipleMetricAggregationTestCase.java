package fr.v3d.elasticsearch.plugin.multiplemetric;


import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ESIntegTestCase;

public class MultipleMetricAggregationTestCase extends ESIntegTestCase {


    protected final static ESLogger logger = ESLoggerFactory.getLogger("test");
    

//    @BeforeClass
//    public void startNode() {
//        Settings.Builder finalSettings = Settings.settingsBuilder()
//                .put("cluster.name", CLUSTER)
//                .put("discovery.zen.ping.multicast.enabled", false)
//                .put("node.local", true)
//                .put("script.disable_dynamic", false)
//        		.put("path.home", "/tmp/data");
//        node = nodeBuilder().settings(finalSettings.put("node.name", "node1").build()).build().start();
//        node2 = nodeBuilder().settings(finalSettings.put("node.name", "node2").build()).build().start();
//
//        client = node.client();
//    }
//
//    @AfterClass
//    public void stopNode() {
//        node.close();
//        node2.close();
//    }
    
//    @Override
//    protected Collection<Class<? extends Plugin>> nodePlugins() {
//      return pluginList(MultipleMetricPlugin.class);
//    }
    
    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
      return Settings.settingsBuilder()
               .put("plugin.types", MultipleMetricPlugin.class.getName())
               .put(super.nodeSettings(nodeOrdinal)).build();
    }
    
    public void createIndex(int numberOfShards, String indexName) {
    	client().admin().indices()
        .prepareCreate(indexName)
        .setSettings(Settings.settingsBuilder().put("index.number_of_shards", numberOfShards))
        .execute().actionGet();
    }

    public void deleteIndex(String indexName) {
    	try {
    		client().admin().indices()
	        .prepareDelete(indexName)
	        .execute().actionGet();
    	} catch (Exception e) { /* ignoring */ }
    }
    
    public void buildTestDataset(int numberOfShards, String indexName, String typeName, int size, Map<String, Integer> termsFactor) {
    	deleteIndex(indexName);
        createIndex(numberOfShards, indexName);
        
        
        for (int i=0; i < size; i++) {
            for (Map.Entry<String, Integer> entry: termsFactor.entrySet()) {
                for (int j = 0; j < 10; j++) {
                    Map<String, Object> doc = new HashMap<String, Object>();
                    doc.put("field0", entry.getKey());
                    doc.put("value1", j * entry.getValue());
                    doc.put("value2", j * 10 * entry.getValue());
                    client().prepareIndex(indexName, typeName, ("doc"+entry.getKey()+i)+j).setSource(doc).setRefresh(true).execute().actionGet();
                }
            }
        }
    }
}
