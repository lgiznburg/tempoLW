package ru.rsmu.tempoLW.consumabales.select_models;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.entities.ExamSchedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExamSelectModel extends AbstractSelectModel {
    private List<ExamSchedule> exams;

    public ExamSelectModel( List<ExamSchedule> exams ) { this.exams = exams; }

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        List<OptionModel> options = new ArrayList<OptionModel>();
        for (ExamSchedule exam : exams) {
            options.add(new OptionModelImpl(exam.getName() + " (" + new SimpleDateFormat("dd.MM.yyyy").format( exam.getExamDate() ) + ")", exam));
        }
        return options;
    }
}
