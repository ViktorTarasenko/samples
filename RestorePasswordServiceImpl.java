package ru.service.auth.restore.impl;

import org.apache.commons.mail.EmailException;
import org.apache.velocity.app.VelocityEngine;
import org.hibernate.LockOptions;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;


import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RestorePasswordServiceImpl implements RestorePasswordService{
	public int linkExpirationTime;
    @Autowired
    private RestorePasswordMessageAssembler restorePasswordMessageAssembler;
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private BaseUrlCalculator baseUrlCalculator;
    @Autowired
    private RestorePasswordLinkGenerator restorePasswordLinkGenerator;
    @Autowired
    private RestorePasswordLinkService restorePasswordLinkService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private VelocityEngine velocityEngine;
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = EmailException.class)
	public void createLinkAndSend(final Long userId, HttpServletRequest request) throws EmailException{
    	final User u = (User) sessionFactory.getCurrentSession().get(User.class,userId, LockOptions.UPGRADE);
    	StringBuilder link = new StringBuilder(baseUrlCalculator.calculateBaseUrl(request));
    	link.append("/password/new/");
    	String generatedLink = null;
    	do {
    		generatedLink = restorePasswordLinkGenerator.generate(128);
    	}while(restorePasswordLinkService.findByLink(generatedLink) != null);
    	link.append(generatedLink);
        final Map<String,Object> model = new HashMap<String,Object>();
        model.put("baseurl",baseUrlCalculator.calculateBaseUrl(request));
        model.put("link",link.toString());
        model.put("text",messageSource.getMessage("restore_auth.message",new Object[]{u.getName()},u.getLocale()));
        model.put("text2",messageSource.getMessage("restore_auth.message2",null,u.getLocale()));
        model.put("label", messageSource.getMessage("email.notify.restore.label", null, u.getLocale()));
        model.put("promo",messageSource.getMessage("email.notify.promo", null, u.getLocale()));
        model.put("feedback",messageSource.getMessage("email.notify.feedback", null, u.getLocale()));
        model.put("notifySettings",messageSource.getMessage("email.notify.notifySettings", null, u.getLocale()));
        model.put("baseurl", u.getLastBaseUrl());
        model.put("more",messageSource.getMessage("email.notify.more", null, u.getLocale()));
        model.put("notifySettingUrl",new StringBuilder(u.getLastBaseUrl()).append("/profile/edit/#geotargeting").toString());
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    MimeMessage mimeMessage = emailSender.createMimeMessage();
                    InternetAddress from = new InternetAddress(((JavaMailSenderImpl)emailSender).getJavaMailProperties().getProperty("mail.smtp.from"), "");
                    mimeMessage.setFrom(from);
                    mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(u.getEmail()));
                    String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "restorePasswordTemplate.vm", "UTF-8", model);
                    mimeMessage.setContent(text, "text/html; charset=utf-8");
                    mimeMessage.setSubject(messageSource.getMessage("email.notify.standart.header",null, u.getLocale()));
                    emailSender.send(mimeMessage);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        };
        taskExecutor.execute(runnable);
		RestorePasswordLink restorePasswordLink = u.getRestorePasswordLink();
		if (restorePasswordLink == null) {
			restorePasswordLink = new RestorePasswordLink();
		}
		restorePasswordLink.setLink(generatedLink);
		Calendar c = Calendar.getInstance(); 
		c.setTime(new Date()); 
		c.add(Calendar.MINUTE,linkExpirationTime);
		restorePasswordLink.setExpires(c.getTime());
		if (u.getRestorePasswordLink() == null) {
			restorePasswordLink.setUser(u);
			u.setRestorePasswordLink(restorePasswordLink);
		}
	}
	public int getLinkExpirationTime() {
		return linkExpirationTime;
	}
	public void setLinkExpirationTime(int linkExpirationTime) {
		this.linkExpirationTime = linkExpirationTime;
	}
    

}
