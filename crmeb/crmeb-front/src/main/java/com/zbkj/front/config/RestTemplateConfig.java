package com.zbkj.front.config;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * RestTemplate配置组件
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factory) {
        try {
            RestTemplate restTemplate = new RestTemplate(factory);
            restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
            return restTemplate;
        } catch (Exception e) {
            throw new RuntimeException("Unable to initiate RestTemplate for access wechat-proxy.");
        }
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        LayeredConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

        Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", csf != null ? csf : SSLConnectionSocketFactory.getSocketFactory()).build();
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(sfr);
//        pollingConnectionManager.setMaxTotal(maxTotal);
//        pollingConnectionManager.setDefaultMaxPerRoute(perRoute);

        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).setConnectionManager(pollingConnectionManager).build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setReadTimeout(10000);//ms
        factory.setConnectTimeout(15000);//ms
        factory.setConnectionRequestTimeout(60000);

        return factory;
    }
//    @Bean
//    public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory factory) {
//        RestTemplate restTemplate = new RestTemplate(factory);
//        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
//        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
//        return restTemplate;
//    }
//
//    @Bean
//    public HttpComponentsClientHttpRequestFactory generateHttpRequestFactory()
//            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException
//    {
//        TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
//        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
//        LayeredConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
//
//        Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", csf != null ? csf : SSLConnectionSocketFactory.getSocketFactory()).build();
//        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(sfr);
//
//
//        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).setConnectionManager(pollingConnectionManager).build();
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//        factory.setHttpClient(httpClient);
//        factory.setConnectTimeout(5000);
//        factory.setReadTimeout(6000);
//        factory.setConnectionRequestTimeout(60000);
//
//        return factory;
//    }
//    @Bean
//    public ClientHttpRequestFactory httpRequestFactory() {
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setReadTimeout(10000);//ms
//        factory.setConnectTimeout(15000);//ms
//        return factory;
//    }

    //解决微信返回json Content-Type 值却是 text/plain 的问题
    public class WxMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        public WxMappingJackson2HttpMessageConverter(){
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.TEXT_PLAIN);
            mediaTypes.add(MediaType.TEXT_HTML);
            setSupportedMediaTypes(mediaTypes);
        }
    }
}
