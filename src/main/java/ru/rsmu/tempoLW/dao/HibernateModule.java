package ru.rsmu.tempoLW.dao;

import org.apache.tapestry5.beanvalidator.BeanValidatorConfigurer;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Match;
import ru.rsmu.tempoLW.dao.internal.ImplQuestionDao;

/**
 * @author leonid.
 */
public class HibernateModule {
    public static void bind(ServiceBinder binder)
    {
        binder.bind( QuestionDao.class, ImplQuestionDao.class);
    }

/*
    public static void contributeBeanValidatorSource(
            OrderedConfiguration<BeanValidatorConfigurer> configuration)
    {
        configuration.add("HotelBookingConfigurer", new BeanValidatorConfigurer()
        {
            public void configure(javax.validation.Configuration<?> configuration)
            {
                configuration.ignoreXmlConfiguration();
            }
        });
    }
*/

    @Match("*Dao")
    public static void adviseTransactions(HibernateTransactionAdvisor advisor,
                                          MethodAdviceReceiver receiver)
    {
        advisor.addTransactionCommitAdvice(receiver);
    }
}
