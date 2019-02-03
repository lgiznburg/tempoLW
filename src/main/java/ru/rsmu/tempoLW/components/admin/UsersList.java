package ru.rsmu.tempoLW.components.admin;

import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.hibernate.Session;
import ru.rsmu.tempoLW.entities.auth.User;

import javax.inject.Inject;

/**
 * This component will trigger the following events on its container:
 * {@link UsersList#USER_SELECTED}(Long userId)
 *
 * @author leonid.
 */
@Events( {UsersList.USER_SELECTED} )  // just for documentation
public class UsersList {
    public static final String USER_SELECTED = "userSelected";

    @Parameter(required = true)
    @Property
    private Long userId;

    @Inject
    private Session session;

    @Property
    private GridDataSource usersDataSource = new HibernateGridDataSource( session, User.class );

    @Property
    private User user;


    public String getLinkCssClass() {
        if ( user != null && userId != null && user.getId() == userId ) {
            return "active";
        }
        return "";
    }
}
