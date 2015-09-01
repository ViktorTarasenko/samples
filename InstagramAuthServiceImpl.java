package ru.test.geoname.impl;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by victor on 14.04.15.
 */
@Service
public class InstagramAuthServiceImpl implements InstagramAuthService {
    private static final Logger logger = Logger.getLogger(VkAuthService.class);
    @Autowired
    private InstagramConfiguration instagramConfiguration;
    @Autowired
    private BaseUrlCalculator baseUrlCalculator;
    private DefaultHttpClient httpClient = new DefaultHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();
    public String getUserId(String code, HttpServletRequest request) throws IOException, InstagramAuthException {
        StringBuilder url= new StringBuilder(instagramConfiguration.getTokenUrl());
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("client_id", instagramConfiguration.clientId));
        postParameters.add(new BasicNameValuePair("client_secret", instagramConfiguration.clientSecret));
        postParameters.add(new BasicNameValuePair("redirect_uri",new StringBuilder().append(baseUrlCalculator.calculateBaseUrl(request)).append("/login/instagram").toString()));
        postParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
        postParameters.add(new BasicNameValuePair("code", code));
        HttpPost httpPost = new HttpPost(url.toString());
        httpPost.setEntity(new UrlEncodedFormEntity(postParameters, Consts.UTF_8));
        HttpResponse httpResponse = httpClient.execute(httpPost);
        JsonNode root = objectMapper.readTree(httpResponse.getEntity().getContent());
        if ((root.get("user") != null) && (root.get("user").get("id") != null)){
                return root.get("user").get("id").asText();
        }
        else {
            String errorMessage = root.get("error_message") != null ? root.get("error_message").asText(): null;
            throw new InstagramAuthException(errorMessage);
        }

    }
}

