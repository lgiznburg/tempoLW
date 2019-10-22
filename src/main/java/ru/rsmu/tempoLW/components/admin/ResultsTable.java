package ru.rsmu.tempoLW.components.admin;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.internal.ExamDaoImpl;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.entities.auth.SubjectManager;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;

import java.util.List;

public class ResultsTable {
    @Parameter ( required = true)
    @Property
    private Testee testee;

    @Property
    private List<ExamResult> results;

    @Property
    private ExamResult result;

    @Inject
    private ExamDao examDao;

    public void setupRender() {
        results = examDao.findResultsForTestee( testee );
    }
}
