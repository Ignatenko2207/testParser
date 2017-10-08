package com.mywork.finance.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mywork.finance.utils.JacksonUtils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.PropertySource;

import java.io.File;
import java.io.IOException;

@Getter
@Setter
@PropertySource(value= {"classpath:app.properties"})
public class ServerSettings {

	@JsonIgnore
	private String path;

	@JsonProperty
	private String host;

	@JsonProperty
	private String mainUrl;

	@JsonProperty
	private boolean useSSL = false;

	@JsonProperty
	private String keyStorePassword;

	@JsonProperty
	private String keyStoreLocation;

	@JsonProperty
	private Integer sslPort;

	@JsonProperty
	private String tempDirectory = "data/tmp";

	@JsonProperty
	private String gcLogsDirectory = "gclogs";

	@JsonProperty
	private Integer port;

	@JsonProperty
	private String cookieHost = "";

	@JsonProperty
	public int cookieMaxAge = 24 * 60 * 60;

	@JsonProperty
	private Integer expirationDate = 1;

	@JsonProperty
	private boolean useElasticsearchProperties = false;

	@JsonProperty
	private String elasticsearchHost;

	@JsonProperty
	private Integer elasticsearchPort;

	@JsonProperty
	private String elasticsearchCluster;

	@JsonProperty
	private String elasticsearchIndex;

	public void init() throws IOException {
		ServerSettings fileSettings = JacksonUtils.fromJson(ServerSettings.class, new File(path));
		// we need to hold path just for the save method
		fileSettings.setPath(path);
		updateSettings(fileSettings);
	}

	public void updateSettings(ServerSettings newSettings) {
		BeanUtils.copyProperties(newSettings, this);
	}

}
