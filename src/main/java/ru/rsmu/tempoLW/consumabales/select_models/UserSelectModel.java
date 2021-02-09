package ru.rsmu.tempoLW.consumabales.select_models;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.entities.auth.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class UserSelectModel extends AbstractSelectModel {
    private List<User> users;

    public UserSelectModel( List<User> users ) {
        this.users = users;
    }

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        return users.stream().map( us -> {
            String name = us.getLastName() + " " +
                    (us.getFirstName() != null ? us.getFirstName().charAt( 0 ) + "." : "") +
                    (us.getMiddleName() != null ? us.getMiddleName().charAt( 0 ) + "." : "");
            return new OptionModelImpl( name, us );
        } ).collect( Collectors.toList());
    }
}
