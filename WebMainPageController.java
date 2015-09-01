package ru.service.web;

import org.apache.tiles.locale.LocaleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.Hibernate;

/**
 * Created by victor on 15.05.15.
 */
@Controller
public class WebMainPageController {
    @Autowired
    private GetCurrentUserService getCurrentUserService;
    @Autowired
    private LandingSlideService landingSlideService;
    @Autowired
    private org.springframework.web.servlet.LocaleResolver localeResolver;
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String getTasksForPerformer(ModelMap model,@RequestParam(value = "tag",required = false) String tag,@RequestParam(value = "lang",required = false) String lang,HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl) {
            model.put("ordersActive", true);
            model.put("tag", tag != null ? "#" + tag : "");
            return "announcmentsForPerformer";
        }
        else {
            if (lang != null) {
                return "redirect:/";
            }
            List<LandingSlideModel> allSlidesPeople = landingSlideService.findAllSlidesPeopleModel(localeResolver.resolveLocale(request).getLanguage());
            List<LandingSlideModel> allSlidesBusiness = landingSlideService.findAllSlidesBusinessModel(localeResolver.resolveLocale(request).getLanguage());
            List<LandingSlideModel> allSlidesBenefits = landingSlideService.findAllSlidesIconsModel(localeResolver.resolveLocale(request).getLanguage());
	    Hibernate.initialize(allSlidesPeople);
	    Hibernate.initialize(allSlidesBusiness);
	    Hibernate.initialize(allSlidesBenefits);
            model.put("allSlidesPeople",allSlidesPeople);
            model.put("allSlidesBusiness",allSlidesBusiness);
            model.put("allSlidesBenefits",allSlidesBenefits);
            return "landing";
        }
    }

}