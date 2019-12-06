package ru.rsmu.tempoLW.consumabales.select_models;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.entities.ExamSubject;

import java.util.ArrayList;
import java.util.List;

public class SubjectSelectModel extends AbstractSelectModel {
    private List<ExamSubject> subjects;

    public SubjectSelectModel( List<ExamSubject> subjects ) { this.subjects = subjects; }

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        List<OptionModel> options = new ArrayList<OptionModel>();
        for (ExamSubject subject : subjects) {
            options.add(new OptionModelImpl(subject.getTitle() + " (" + subject.getLocale() + ")", subject));
        }
        return options;
    }
}
