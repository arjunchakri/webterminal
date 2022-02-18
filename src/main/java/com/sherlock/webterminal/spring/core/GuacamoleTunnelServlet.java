package com.sherlock.webterminal.spring.core;

import javax.servlet.http.HttpServletRequest;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.GuacamoleSocket;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.InetGuacamoleSocket;
import org.apache.guacamole.net.SimpleGuacamoleTunnel;
import org.apache.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.apache.guacamole.servlet.GuacamoleHTTPTunnelServlet;

public class GuacamoleTunnelServlet extends GuacamoleHTTPTunnelServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  protected GuacamoleTunnel doConnect(HttpServletRequest request) throws GuacamoleException {

    // Create our configuration
    GuacamoleConfiguration config = new GuacamoleConfiguration();
//    config.setProtocol("rdp");
//    config.setParameter("hostname", "");
//    config.setParameter("port", "3389");
//    config.setParameter("password", "Test");
//    config.setParameter("username", "Administrator");
//////    config.setParameter("domain", "");
//    config.setParameter("security", "nla");
//    config.setParameter("ignore-cert", "true");

//    config.setParameter("preconnection-blob", "9cb03042-d5ff-4f9d-9fed-ea3f580bebbf");

    config.setProtocol("ssh");
    config.setParameter("hostname", ""); //http://10.129.148.71:9000/
    config.setParameter("port", "22");
    config.setParameter("username", "root");

    config.setParameter("password", "<ROOT PASSWORD HERE>");

    // Connect to guacd - everything is hard-coded here.
    GuacamoleSocket socket = new ConfiguredGuacamoleSocket(new InetGuacamoleSocket("localhost", 4822), config);

    // Return a new tunnel which uses the connected socket
    return new SimpleGuacamoleTunnel(socket);

  }

}
