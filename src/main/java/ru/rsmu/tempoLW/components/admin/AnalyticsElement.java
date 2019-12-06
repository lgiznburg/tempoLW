package ru.rsmu.tempoLW.components.admin;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.TestingPlan;

import java.util.List;

public class AnalyticsElement {

    @Parameter
    @Property
    private TestingPlan plan;

    @Parameter
    @Property
    private ExamSchedule exam;

    @Property
    private List<List<String>> entries;

}
