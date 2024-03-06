package org.hanghae.markethub.global.elasticsearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

public class ElasticConnect extends ElasticsearchConfiguration {

	@Value("${spring.elasticsearch.rest.uris}")
	private String server;
	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder().connectedTo(server).build();
	}

}
