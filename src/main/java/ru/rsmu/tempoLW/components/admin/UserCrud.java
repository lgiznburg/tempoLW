package ru.rsmu.tempoLW.components.admin;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import ru.rsmu.tempoLW.consumabales.CrudMode;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.dao.UserDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.auth.SubjectManager;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRole;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class UserCrud {
    public static final String CANCEL_CREATE = "cancelCreate";
    public static final String CREATED = "created";
    public static final String TO_UPDATE = "toUpdate";
    public static final String CANCEL_UPDATE = "cancelUpdate";
    public static final String UPDATED = "updated";
    public static final String DELETED = "deleted";

    // Parameters

    @Parameter(required = true)
    @Property
    private CrudMode mode;

    @Parameter(required = true)
    @Property
    private Long userId;

    // Screen fields

    @Property
    private User user;

    @Property
    private SubjectManager subjectManager;

    @Property
    private SelectModel rolesModel;

    @Property
    private SelectModel subjectsModel;

    @Property
    private String userPassword;

    @Property
    private String userPasswordConfirm;

    // ----
    @Inject
    private UserDao userDao;

    @Inject
    private QuestionDao questionDao;

    @InjectComponent
    private Form createForm;

    @InjectComponent
    private Form updateForm;

    @InjectComponent
    private Field password;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private SelectModelFactory modelFactory;

    private List<UserRole> roles;

    private List<ExamSubject> subjects;

    @Inject
    private Messages messages;


    public void setupRender() {
        if ( mode == CrudMode.REVIEW ) {
            user = userId == null ? null : userDao.find( User.class, userId );
            subjectManager = user == null ? null : userDao.findSubjectsForUser( user );
        }
    }

    //------ Create

    void onPrepareForRenderFromCreateForm() throws Exception {
        // If fresh start, make sure there's a User object available.
        if (createForm.isValid()) {
            prepare();
        }
    }

    void onPrepareForSubmitFromCreateForm() throws Exception {
        prepare();
    }

    boolean onValidateFromCreateForm() {
        if ( userPassword == null
                || userPassword.length() < 7
                || (!userPassword.matches( ".*\\W+.*" ) && !userPassword.matches( ".*\\w+.*" ) ) ) {
            createForm.recordError( password, messages.get("password-form-wrong"));
        }
        else if ( !userPassword.equals( userPasswordConfirm ) ) {
            createForm.recordError( password, messages.get("passwords-dont-match"));
        }
        else {
            user.setPassword( userDao.encrypt( userPassword ) );
        }

        return true;
    }

    boolean onSuccessFromCreateForm() {
        userDao.save( user );
        if ( user.isUserInRole( UserRoleName.subject_admin ) || user.isUserInRole( UserRoleName.teacher ) ) {
            subjectManager.setUser( user );
            userDao.save( subjectManager );
        }

        // We want to tell our containing page explicitly what person we've created, so we trigger a new event with a
        // parameter. It will bubble up because we don't have a handler method for it.
        componentResources.triggerEvent( CREATED, new Object[]{user.getId()}, null );

        // We don't want the original event to bubble up, so we return true to say we've handled it.
        return true;
    }


    //------ Review

    //------ Update

    void onPrepareForRenderFromUpdateForm() throws Exception {
        // If fresh start, make sure there's a User object available.
        if (updateForm.isValid()) {
            prepare();
        }
    }

    void onPrepareForSubmitFromUpdateForm() throws Exception {
        prepare();
    }

    boolean onValidateFromUpdateForm() {
        if ( userPassword != null && !userPassword.isEmpty() ) {
            if ( userPassword.length() < 7 || (!userPassword.matches( ".*\\W+.*" ) && !userPassword.matches( ".*\\w+.*" ) ) ) {
                updateForm.recordError( password, messages.get("password-form-wrong"));
            }
            else if ( ! userPassword.equals( userPasswordConfirm ) ) {
                updateForm.recordError( password, messages.get("passwords-dont-match"));
            }
            else {
                user.setPassword( userDao.encrypt( userPassword ) );
            }
        }

        return true;
    }

    boolean onSuccessFromUpdateForm() {
        userDao.save( user );
        if ( user.isUserInRole( UserRoleName.subject_admin ) || user.isUserInRole( UserRoleName.teacher ) ) {
            subjectManager.setUser( user );
            userDao.save( subjectManager );
        }

        // We want to tell our containing page explicitly what person we've created, so we trigger a new event with a
        // parameter. It will bubble up because we don't have a handler method for it.
        componentResources.triggerEvent( UPDATED, new Object[]{user.getId()}, null );

        // We don't want the original event to bubble up, so we return true to say we've handled it.
        return true;
    }


    //------

    public boolean isModeCreate() {
        return mode == CrudMode.CREATE;
    }

    public boolean isModeReview() {
        return mode == CrudMode.REVIEW;
    }

    public boolean isModeUpdate() {
        return mode == CrudMode.UPDATE;
    }

    private void prepare() {
        switch ( mode ) {
            case CREATE:
                user = new User();
                user.setRoles( new ArrayList<>() );
                subjectManager = new SubjectManager();
                subjectManager.setSubjects( new ArrayList<>() );
                break;
            case UPDATE:
                user = userDao.find( User.class, userId );
                if ( user != null ) {
                    subjectManager = userDao.findSubjectsForUser( user );
                    if ( subjectManager == null ) {
                        subjectManager = new SubjectManager();
                        subjectManager.setSubjects( new ArrayList<>() );
                    }
                }
                break;
        }
        roles = userDao.findAll( UserRole.class );
        rolesModel = modelFactory.create( roles, "roleName" );
        subjects = questionDao.findAll( ExamSubject.class );
        subjectsModel = modelFactory.create( subjects, "title" );
    }

    public ValueEncoder<UserRole> getRoleEncoder() {
        return new ValueEncoder<UserRole>() {
            @Override
            public String toClient( UserRole value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public UserRole toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
                for ( UserRole role :roles ) {
                    if ( role.getId() == id ) {
                        return role;
                    }
                }
                return null;
            }
        };
    }

    public ValueEncoder<ExamSubject> getSubjectEncoder() {
        return new ValueEncoder<ExamSubject>() {
            @Override
            public String toClient( ExamSubject value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public ExamSubject toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
                for ( ExamSubject subject : subjects ) {
                    if ( subject.getId() == id ) {
                        return subject;
                    }
                }
                return null;
            }
        };
    }

    public String getRoleNames() {
        if ( user == null ) return "";
        List<String> roleNames = user.getRoles().stream().map( role -> role.getRoleName().name() ).collect( Collectors.toList() );
        return StringUtils.join( roleNames, ", " );
    }

    public String getSubjectTitles() {
        if ( subjectManager == null ) return "";
        List<String> subjectTitles = subjectManager.getSubjects().stream().map( ExamSubject::getTitle ).collect( Collectors.toList() );
        return StringUtils.join( subjectTitles, ", " );
    }
}
