package ru.rsmu.tempoLW.rest;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext;
import org.jboss.resteasy.util.Base64;
import ru.rsmu.tempoLW.dao.UserDao;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRole;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This interceptor verify the access permissions for a user
 * based on username and password provided in request
 * It uses user credential to grant access
 * @author leonid.
 */
@Provider
public class SecurityInterceptor implements ContainerRequestFilter
{
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401, new Headers<Object>());;
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", 403, new Headers<Object>());;
    private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", 500, new Headers<Object>());;

    @Inject
    private UserDao userDao;

    private boolean isUserAllowed(final String username, final String password, final Set<String> rolesSet)
    {
        boolean isAllowed = false;

        //Step 1. Fetch password from database and match with password in argument
        //If both match then get the defined role for user from database and continue; else return isAllowed [false]

        User user = userDao.findByUsername( username );
        if ( !password.equalsIgnoreCase( user.getPassword() ) ) {
            return false;
        }

        //Step 2. Verify user role
        for ( UserRole role : user.getRoles() ) {
            if ( rolesSet.contains(role.getAuthority()) )
            {
                isAllowed = true;
                break;
            }
        }
        return isAllowed;
    }

    @Override
    public void filter( ContainerRequestContext context ) throws IOException {
        PostMatchContainerRequestContext pmContext = (PostMatchContainerRequestContext) context;

        Method method = pmContext.getResourceMethod().getMethod();
        //Access allowed for all
        if( ! method.isAnnotationPresent(PermitAll.class))
        {
            //Access denied for all
            if(method.isAnnotationPresent(DenyAll.class))
            {
                context.abortWith(ACCESS_FORBIDDEN);
                return;
            }

            //Get request headers
            final MultivaluedMap<String, String> headers = context.getHeaders();

            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

            //If no authorization information present; block access
            if(authorization == null || authorization.isEmpty())
            {
                context.abortWith(ACCESS_DENIED);
                return;
            }

            //Get encoded username and password
            final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

            //Decode username and password
            String usernameAndPassword = null;
            try {
                usernameAndPassword = new String(Base64.decode(encodedUserPassword));
            } catch (IOException e) {
                context.abortWith(SERVER_ERROR);
                return;
            }

            //Split username and password tokens
            final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
            final String username = tokenizer.nextToken();
            final String password = tokenizer.nextToken();

            //Verifying Username and password
            //System.out.println(username);
            //System.out.println(password);

            //Verify user access
            if(method.isAnnotationPresent(RolesAllowed.class))
            {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));

                //Is user valid?
                if( ! isUserAllowed(username, password, rolesSet))
                {
                    context.abortWith(ACCESS_DENIED);
                    return;
                }
            }
        }

    }
}