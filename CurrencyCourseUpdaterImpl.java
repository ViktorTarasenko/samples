

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.budetsdelano.startup.entities.CurrencyCourse;
import ru.budetsdelano.startup.mobile.json.model.CourseModel;
import ru.budetsdelano.startup.server.dao.CurrencyCourseService;
import ru.budetsdelano.startup.service.currency.CurrencyCourseUpdater;
import ru.budetsdelano.startup.service.configuration.ComissionConfig;

import java.util.Date;
import java.util.List;

/**
 * Created by victor on 22.05.15.
 */
@Service
public class CurrencyCourseUpdaterImpl implements CurrencyCourseUpdater {
    @Autowired
    private ComissionConfig comissionConfig;
    @Autowired
    private CurrencyCourseService currencyCourseService;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(List<CourseModel> courses) {
        for (int i = 0;i < courses.size();++i) {
            CurrencyCourse currencyCourse = currencyCourseService.findByTwoCurrencies(courses.get(i).getCurrency1(), courses.get(i).getCurrency2());
            if (currencyCourse != null) {
                currencyCourse.setRate(courses.get(i).getRate());
                currencyCourse.setLastUpdateTime(new Date().getTime());
            }

        }
    }
}
