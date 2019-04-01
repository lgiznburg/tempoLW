package ru.rsmu.tempoLW.dao;

import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Match;
import ru.rsmu.tempoLW.dao.internal.ExamDaoImpl;
import ru.rsmu.tempoLW.dao.internal.QuestionDaoImpl;
import ru.rsmu.tempoLW.dao.internal.TesteeDaoImpl;
import ru.rsmu.tempoLW.dao.internal.UserDaoImpl;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRole;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;
import ru.rsmu.tempoLW.seedentity.hibernate.SeedEntity;
import ru.rsmu.tempoLW.seedentity.hibernate.SeedEntityImpl;

import java.util.LinkedList;

/**
 * @author leonid.
 */
public class HibernateModule {
    public static void bind(ServiceBinder binder)
    {
        binder.bind( QuestionDao.class, QuestionDaoImpl.class);
        binder.bind( UserDao.class, UserDaoImpl.class );
        binder.bind( TesteeDao.class, TesteeDaoImpl.class );
        binder.bind( ExamDao.class, ExamDaoImpl.class );

        //seed entity - initial DB population
        binder.bind(SeedEntity.class, SeedEntityImpl.class);
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

    //populate with initial values
    @Contribute(SeedEntity.class)
    public static void addSeedEntities(OrderedConfiguration<Object> configuration) {
        //Roles:
        UserRole adminRole = new UserRole();
        adminRole.setRoleName( UserRoleName.admin );
        configuration.add( "adminRole", adminRole );

        UserRole subjectAdminRole = new UserRole();
        subjectAdminRole.setRoleName( UserRoleName.subject_admin );
        configuration.add( "subjectAdminRole", subjectAdminRole );


        UserRole teacherRole = new UserRole();
        teacherRole.setRoleName( UserRoleName.teacher );
        configuration.add( "teacherRole", teacherRole );

        User admin = new User();
        admin.setUsername( "prk_admin@rsmu.ru" );
        admin.setFirstName( "Эдельвейс" );
        admin.setMiddleName( "Захарович" );
        admin.setLastName( "Машкин" );
        admin.setPassword( "6A8AEAFFD00E99F5B377B084FA577E3B" );
        admin.setRoles( new LinkedList<>() );
        admin.getRoles().add( adminRole );
        configuration.add( "admin", admin );

        User me = new User();
        me.setUsername( "ginzburg_ld@rsmu.ru" );
        me.setFirstName( "Леонид" );
        me.setMiddleName( "Давидович" );
        me.setLastName( "Гинзбург" );
        me.setPassword( "33A1628D8BF774777141A6AF3B283DC4" );
        me.setRoles( new LinkedList<>() );
        me.getRoles().add( adminRole );
        configuration.add( "me", me );


    }
}
