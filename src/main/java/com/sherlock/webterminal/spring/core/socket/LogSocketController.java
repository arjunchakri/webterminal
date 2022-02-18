package com.sherlock.webterminal.spring.core.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.sherlock.webterminal.spring.core.socket.folderlistener.FolderListenerSocket;
import com.sherlock.webterminal.spring.core.socket.formula.FormulaSocket;
import com.sherlock.webterminal.spring.core.socket.remote.RemoteControlSocket;
import com.sherlock.webterminal.spring.core.socket.remote.RemoteLogDownloadSocket;

/**
 * @author anunugonda
 *
 */
@Configuration
@EnableWebSocket
public class LogSocketController implements WebSocketConfigurer {

  //To avoid CORS issue for proxy/cross origin access
  private static final String SOCKET_ORIGINS = "*";

  @Bean
  public WebSocketHandler logSocket() {
    return new LogSocket();
  }

  @Bean
  public WebSocketHandler remoteLogDownloadSocket() {
    return new RemoteLogDownloadSocket();
  }

  @Bean
  public WebSocketHandler actionExecutionSocket() {
    return new ActionExecutionSocket();
  }

  @Bean
  public WebSocketHandler folderListenerSocket() {
    return new FolderListenerSocket();
  }

  @Bean
  public WebSocketHandler formulaSocket() {
    return new FormulaSocket();
  }

  @Bean
  public WebSocketHandler remoteControlSocket() {
    return new RemoteControlSocket();
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(logSocket(), "/log_socket").setAllowedOrigins(SOCKET_ORIGINS);
    registry.addHandler(remoteLogDownloadSocket(), "/remote_log_socket_socket").setAllowedOrigins(SOCKET_ORIGINS);
    registry.addHandler(actionExecutionSocket(), "/action_execution_socket").setAllowedOrigins(SOCKET_ORIGINS);
    registry.addHandler(folderListenerSocket(), "/folder_listener_socket").setAllowedOrigins(SOCKET_ORIGINS);
    registry.addHandler(formulaSocket(), "/formula_listener_socket").setAllowedOrigins(SOCKET_ORIGINS);
    registry.addHandler(remoteControlSocket(), "/remote_control_socket").setAllowedOrigins(SOCKET_ORIGINS);

  }
}