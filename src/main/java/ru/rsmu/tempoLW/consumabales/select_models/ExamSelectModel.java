package ru.rsmu.tempoLW.consumabales.select_models;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.entities.ExamSchedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExamSelectModel extends AbstractSelectModel {
    private List<ExamSchedule> exams;

    public ExamSelectModel( List<ExamSchedule> exams ) { this.exams = exams; }

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        List<OptionModel> options = exams.stream().map( es -> {
            String name = es.getName() +
                    " (" +
                    new SimpleDateFormat( "dd.MM.yyyy" ).format( es.getExamDate() ) +
                    ") " +
                    es.getTestingPlan().getSubject().getTitle() +
                    " - " +
                    es.getTestingPlan().getName();
            return new OptionModelImpl( name, es );
        } ).collect( Collectors.toList());
        return options;
    }
}
