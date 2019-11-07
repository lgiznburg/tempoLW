package ru.rsmu.tempoLW.pages.admin;

import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.dao.internal.BaseDaoImpl;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.TestingPlan;

import java.util.List;

/**
 * @author polyakov_ps
 */

public class Analytics {

    @Property
    private List<ExamSubject> subjects;

    @Property
    private List<TestingPlan> plans;

    @Property
    private List<ExamSchedule> exams;

    public void onActivate() {
        subjects = new BaseDaoImpl().findAll( ExamSubject.class );
    }

}
