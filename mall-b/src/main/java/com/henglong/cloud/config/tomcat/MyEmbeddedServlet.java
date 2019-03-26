//package com.henglong.cloud.config.tomcat;
//
//import org.apache.catalina.connector.Connector;
//import org.apache.coyote.http11.Http11NioProtocol;
//import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.WebServerFactory;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//
//public class MyEmbeddedServlet implements WebServerFactoryCustomizer {
//
//    @Override
//    public void customize(WebServerFactory factory) {
//        ((TomcatServletWebServerFactory)factory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
//            @Override
//            public void customize(Connector connector) {
//                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//                // 设置最大连接数
//                protocol.setMaxConnections(2000);
//                // 设置最大线程数
//                protocol.setMaxThreads(500);
//
////                protocol.setSelectorTimeout(3000);
////                protocol.setSessionTimeout(3000);
//                protocol.setConnectionTimeout(30000);
//                //设置端口号
//                protocol.setPort(4548);
////                protocol.setPort(80);
//            }
//        });
//    }
//}
