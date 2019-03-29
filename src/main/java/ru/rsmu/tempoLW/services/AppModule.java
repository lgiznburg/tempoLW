package ru.rsmu.tempoLW.services;

import com.anjlab.tapestry5.services.liquibase.LiquibaseModule;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.impl.SecurityFilterChain;
import ru.rsmu.tempoLW.dao.HibernateModule;
import com.anjlab.tapestry5.services.liquibase.AutoConfigureLiquibaseDatasourceModule;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author leonid.
 */
@ImportModule( {
        LiquibaseModule.class,
        AutoConfigureLiquibaseDatasourceModule.class,
        HibernateModule.class
} )
public class AppModule {

    @ApplicationDefaults
    @Contribute(SymbolProvider.class)
    public static void configureTapestryHotelBooking(
            MappedConfiguration<String, String> configuration)
    {

        configuration.add( SymbolConstants.SUPPORTED_LOCALES, "en,ru");
        configuration.add(SymbolConstants.APPLICATION_VERSION, "1.0-SNAPSHOT");
        configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
        configuration.add(SymbolConstants.RESTRICTIVE_ENVIRONMENT, "true");

        // Generate a random HMAC key for form signing (not cluster safe).
        // Normally it would be better to use a fixed password-like string, but
        // we can't because this file is published as open source software.
        configuration.add(SymbolConstants.HMAC_PASSPHRASE,
                new BigInteger(130, new SecureRandom()).toString(32));

        // use jquery instead of prototype as foundation JS library
        configuration.add(SymbolConstants.JAVASCRIPT_INFRASTRUCTURE_PROVIDER, "jquery");

        // false turns off switching between HTTP and HTTPS (ignoring @Secure
        // annotations), so if app is served under HTTP it will stay that way,
        // and if served under HTTPS it will also stay that way, for all pages
        configuration.add(SymbolConstants.SECURE_ENABLED, "false");

        configuration.add(SymbolConstants.ENABLE_PAGELOADING_MASK, "false");

        configuration.add(SecuritySymbols.LOGIN_URL, "/login");
        configuration.add( SecuritySymbols.SUCCESS_URL, "/index");
        //create rememberMe cipher key, 16 bytes long
        byte[] cipherKeySource = {10, 33, 28, 77, 48, 115, 3, 47, 109, 75, 55, 55, 68, 121, 19, 63};
        configuration.add( SecuritySymbols.REMEMBERME_CIPHERKERY, Base64.encodeToString( cipherKeySource ) );

        // provide liquibase integration with master changelog file
        configuration.add( LiquibaseModule.LIQUIBASE_CHANGELOG, "db_migrations/change_log.xml");
    }

    /*
        public HttpServletRequestFilter buildUtf8Filter()
    {
        return new HttpServletRequestFilter()
        {
            public boolean service(HttpServletRequest request, HttpServletResponse response,
                    HttpServletRequestHandler handler) throws IOException
            {
                request.setCharacterEncoding("UTF-8");
                return handler.service(request, response);
            }
        };
    }

    public void contributeHttpServletRequestHandler(
            OrderedConfiguration<HttpServletRequestFilter> configuration,
            @InjectService("Utf8Filter")
            HttpServletRequestFilter utf8Filter)
    {
        configuration.add("Utf8Filter", utf8Filter, "before:MultipartFilter");
    }
    * */

    public static void bind(ServiceBinder binder) {
        binder.bind(AuthorizingRealm.class, UserDetailsRealm.class).withId(UserDetailsRealm.class.getSimpleName());

        binder.bind( SecurityUserHelper.class );

    }

    public static void contributeSecurityConfiguration(OrderedConfiguration<SecurityFilterChain> configuration,
                                                       SecurityFilterChainFactory factory) {
        configuration.add( "admin-users", factory.createChain( "/admin/users*/**" ).add( factory.roles(), "admin" ).build() );
        configuration.add("admin-admin", factory.createChain("/admin/**").add( factory.user() ).build());
        configuration.add("loginform-anon",
                factory.createChain("/login.loginform.tynamologinform").add(factory.anon()).build());
        configuration.add("anon", factory.createChain("/**").add(factory.anon()).build());
    }

    public static void contributeWebSecurityManager(Configuration<Realm> configuration, @InjectService("UserDetailsRealm") AuthorizingRealm userRealm) {
        configuration.add(userRealm);
    }

}
