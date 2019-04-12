package com.anjlab.tapestry5.services.liquibase;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.annotations.Advise;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leonid.
 *
 * This class based on code from anjlab-tapestry-liquibase project
 *
 * Our project does not use libraries and configurations required for original project.
 * So we create our own data source based on DB configuration from "hibernate.cfg.xml"
 *
 */
public class AutoConfigureLiquibaseDatasourceModule {
    // If JNDI name of the data source starts with "jdbc/" LiquibaseServletListener tries to do a
    // lookup from "java:comp/env" context. This context is read-only, so we cannot bind to it.
    // That's why we're binding directly to the scope of InitialContext.
    private static final String LIQUIBASE_DATASOURCE_JNDI_NAME = "liquibase-datasource";

    public static final String LIQUIBASE_CONFIG_FILE_NAME = "liquibase.hibernate.config-file-name";

    public static void contributeFactoryDefaults(
            MappedConfiguration<String, Object> configuration)
    {
        configuration.override( LiquibaseModule.LIQUIBASE_DATA_SOURCE, LIQUIBASE_DATASOURCE_JNDI_NAME);

        configuration.add(LIQUIBASE_CONFIG_FILE_NAME, "/hibernate.cfg.xml");
    }

    @Advise(serviceInterface = LiquibaseInitializer.class)
    public static void dataSourceForLiquibase(
            final MethodAdviceReceiver receiver,
            @Inject @Symbol(LIQUIBASE_CONFIG_FILE_NAME)
            final String hibernateConfigName,
            final Logger logger)
    {
        receiver.adviseAllMethods(new MethodAdvice()
        {
            @Override
            public void advise( MethodInvocation invocation)
            {
                InitialContext ic = null;
                DataSource dataSource = null;
                try
                {
                    // We're going to read from Hibernate configuration xml file to get
                    // information about connection settings

                    // Then we're creating JDBC datasource using apache-commons-dbcp

                    Map<String,String> props = parsePropertiesFromHibernate( hibernateConfigName );
                    dataSource = createDataSource( props );

                    ic = new InitialContext();

                    ic.bind(LIQUIBASE_DATASOURCE_JNDI_NAME, dataSource);

                    invocation.proceed();
                }
                catch (Throwable e)
                {
                    logger.error("Error binding liquibase datasource", e);

                    throw new RuntimeException(e);
                }
                finally
                {
                    if (ic != null)
                    {
                        try
                        {
                            ic.unbind(LIQUIBASE_DATASOURCE_JNDI_NAME);
                        }
                        catch (NamingException e)
                        {
                            logger.error("Error unbinding liquibase datasource", e);
                        }

                        try
                        {
                            ic.close();
                        }
                        catch (NamingException e)
                        {
                            logger.error("Error closing InitialContext", e);
                        }
                    }

                    if (dataSource instanceof Closeable)
                    {
                        try
                        {
                            ((Closeable) dataSource).close();
                        }
                        catch (IOException e)
                        {
                            logger.error("Error closing liquibase datasource", e);
                        }
                    }
                }
            }
        });
    }

    private static Map<String,String> parsePropertiesFromHibernate( String hibernateConfigName ) throws ParserConfigurationException, IOException, SAXException {
        Map<String,String> props = new HashMap<>();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse( AutoConfigureLiquibaseDatasourceModule.class.getResourceAsStream( hibernateConfigName ));

            NodeList nodes = doc.getElementsByTagName( "session-factory" ).item( 0 ).getChildNodes();
            int n = nodes.getLength();
            Node current;
            for (int i=0; i<n; i++) {
                current = nodes.item( i );
                if ( current.getNodeName().equalsIgnoreCase( "property" ) ) {
                    Node attr = current.getAttributes().item( 0 );
                    String attrName = attr.getNodeName();
                    String attrValue = attr.getNodeValue();
                    if ( attrName.equalsIgnoreCase( "name" ) ) {
                        props.put( attrValue, current.getTextContent() );
                    }
                }
            }
        return props;
    }

    private static DataSource createDataSource( Map<String,String> props ) {

        String connectorClass = props.get( "hibernate.connection.driver_class" );
        String connectionUrl = props.get( "hibernate.connection.url" );
        String schema = props.get( "hibernate.default_schema" );
        String username = props.get( "hibernate.connection.username" );
        String password = props.get( "hibernate.connection.password" );

        DataSource ds;
        if ( connectorClass.contains( "com.microsoft.sqlserver" ) ) {
            SQLServerDataSource msDs = new SQLServerConnectionPoolDataSource();
            msDs.setURL( connectionUrl );
            msDs.setUser( username );
            msDs.setPassword( password );

            ds = msDs;
        }
        else {
            BasicDataSource bds = new BasicDataSource();
            bds.setDriverClassName( connectorClass );
            bds.setUrl( connectionUrl );
            bds.setDefaultSchema( schema );
            bds.setUsername( username );
            bds.setPassword( password );
            ds = bds;
        }

        return ds;
    }
}
