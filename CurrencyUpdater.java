package ru.test.service.scheduler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import ru.budetsdelano.startup.entities.CurrencyCourse;
import ru.budetsdelano.startup.mobile.json.model.CourseModel;
import ru.budetsdelano.startup.server.dao.CurrencyCourseService;
import ru.budetsdelano.startup.service.currency.CourseSource;
import ru.budetsdelano.startup.service.currency.CurrencyCourseUpdater;
import ru.budetsdelano.startup.service.currency.FetchCourseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Created by victor on 22.05.15.
 */
public class CurrencyUpdater {
    private static final Logger logger = Logger.getLogger(CurrencyUpdater.class);
    private Date lastUpdateDate;
    @Autowired
    private CourseSource courseSource;
    @Autowired
    private CurrencyCourseService currencyCourseService;
    @Autowired
    private CurrencyCourseUpdater currencyCourseUpdater;
    public void updateCourse() {
        logger.debug("started update course");
        if (currencyCourseService.findCountOfNotUpdatedManyTimes() <=0) {
           // logger.debug("failed2");
            return;
        }
        try {
            List<CourseModel> courses = courseSource.getCourses();
            currencyCourseUpdater.update(courses);
        }catch (FetchCourseException e) {
            logger.error("failed to fetch cources",e);
        }
    }
}
