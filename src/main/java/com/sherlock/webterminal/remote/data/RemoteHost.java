package com.sherlock.webterminal.remote.data;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.sherlock.webterminal.formulas.data.FormulaWebInput;

public class RemoteHost {

  private String host;
  private String trimmedHost;
  private Map<String, FormulaWebInput> cachedResults;

  private transient ExecutorService processService;

  public RemoteHost() {
  }

  public RemoteHost(String host, String trimmedHost, Map<String, FormulaWebInput> cachedResults,
      ExecutorService processService) {
    this.host = host;
    this.trimmedHost = trimmedHost;
    this.cachedResults = cachedResults;
    this.processService = processService;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getTrimmedHost() {
    return trimmedHost;
  }

  public void setTrimmedHost(String trimmedHost) {
    this.trimmedHost = trimmedHost;
  }

  public Map<String, FormulaWebInput> getCachedResults() {
    return cachedResults;
  }

  public void setCachedResults(Map<String, FormulaWebInput> cachedResults) {
    this.cachedResults = cachedResults;
  }

  public ExecutorService getProcessService() {
    return processService;
  }

  public void setProcessService(ExecutorService processService) {
    this.processService = processService;
  }

}
