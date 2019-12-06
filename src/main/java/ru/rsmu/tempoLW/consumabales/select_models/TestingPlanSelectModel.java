package ru.rsmu.tempoLW.consumabales.select_models;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.entities.TestingPlan;

import java.util.ArrayList;
import java.util.List;

public class TestingPlanSelectModel extends AbstractSelectModel {
    private List<TestingPlan> plans;

    public TestingPlanSelectModel( List<TestingPlan> plans ) { this.plans = plans; }

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        List<OptionModel> options = new ArrayList<OptionModel>();
        for (TestingPlan plan : plans) {
            options.add(new OptionModelImpl(plan.getName() + " (" + plan.getSubject().getTitle() + ")", plan));
        }
        return options;
    }
}
