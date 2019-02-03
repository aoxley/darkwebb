package dark.webb.spider.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dark.webb.spider.elasticsearch.data.SiteData;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentType.JSON;

/**
 * Created by Alistair Oxley on 2/10/2017.
 */
@Service
public class ElasticsearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchService.class);

    @Value("${elasticsearch.host}")
    private String elasticsearchHost;
    @Value("${elasticsearch.port}")
    private int elasticsearchPort;
    @Value("${elasticsearch.index}")
    private String elasticsearchIndex;
    @Value("${elasticsearch.type}")
    private String elasticsearchType;

    private RestClient lowLevelRestClient;
    private RestHighLevelClient highLevelRestClient;

    private ObjectMapper mapper;

    @PostConstruct
    public void postConstruct() {
        lowLevelRestClient = RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort, "http")).build();
        highLevelRestClient = new RestHighLevelClient(lowLevelRestClient);

        mapper = new ObjectMapper();
    }

    @PreDestroy
    public void preDestroy() throws Exception {
        lowLevelRestClient.close();
    }

    public void saveSiteDataToElasticsearch(SiteData siteData) {
        String json = null;
        try {
            json = mapper.writeValueAsString(siteData);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Unable to parse siteData object to json!");
            e.printStackTrace();
        }
        if (json != null) {
            IndexRequest request = new IndexRequest(elasticsearchIndex, elasticsearchType);
            request.source(json, JSON);
            IndexResponse response = null;
            try {
                response = highLevelRestClient.index(request);
            } catch (IOException e) {
                LOGGER.warn("Error when trying to index the json: " + json);
                e.printStackTrace();
            }
            if (response != null) {
                LOGGER.debug("Successfully saved onion site to elasticsearch: " + siteData.getUrl());
                LOGGER.trace("Successfully saved data to elasticsearch. Id: " + response.getId());
            }
        }
    }
}
