package ru.test.service.gcm.impl;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by victor on 07.11.14.
 */
@Service
public class GcmServiceImpl implements GcmService {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private AndroidRegKeyService androidRegKeyService;

    @Transactional
    public void addGcmRegKeyToUser(String key, User user) {
        AndroidRegKey androidRegKey = androidRegKeyService.findByKey(key);
        if ((androidRegKey != null) && (androidRegKey.getUser().getId() != user.getId())) {
            androidRegKey.setUser(user);
        } else if (androidRegKey == null) {
            AndroidRegKey newKey = new AndroidRegKey();
            newKey.setKey(key);
            newKey.setUser(user);
            user.getAndroidRegKeys().add(newKey);
            if (user.getAndroidRegKeys().size()> 5) {
                user.getAndroidRegKeys().remove(0);
            }
        }


    }

    @Transactional
    public void deleteGcmKeyFromUser(String key, User user) {
        AndroidRegKey androidRegKey = androidRegKeyService.findByKey(key);
        if ((androidRegKey != null) && (androidRegKey.getUser().getId() == user.getId())) {
            user.getAndroidRegKeys().remove(androidRegKey);
        }

    }
    @Transactional
    public void deleteGcmKeyFromUser(String key, Long userId) {
        User user = (User) sessionFactory.getCurrentSession().get(User.class,userId);
        AndroidRegKey androidRegKey = androidRegKeyService.findByKey(key);
        if ((androidRegKey != null) && (androidRegKey.getUser().getId() == userId)) {
            user.getAndroidRegKeys().remove(androidRegKey);
        }

    }


}
