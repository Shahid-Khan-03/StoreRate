package Backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DatabaseConnectionValidationConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionValidationConfig.class);

    @Bean
    static BeanFactoryPostProcessor datasourceConfigurationLogger(Environment environment) {
        return beanFactory -> logDatasourceTarget(environment);
    }

    @Bean
    ApplicationRunner hikariConnectionValidator(DataSource dataSource, Environment environment) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                log.info("HikariCP database connection validated successfully for user '{}' at '{}'.",
                        environment.getProperty("spring.datasource.username"),
                        connection.getMetaData().getURL());
            } catch (SQLException ex) {
                log.error("HikariCP database connection validation failed for user '{}' at '{}'.",
                        environment.getProperty("spring.datasource.username"),
                        environment.getProperty("spring.datasource.url"),
                        ex);
                throw ex;
            }
        };
    }

    private static void logDatasourceTarget(Environment environment) {
        JdbcTarget target = JdbcTarget.from(environment.getProperty("spring.datasource.url"));
        log.info("Database target: host='{}', port='{}', database='{}', username='{}'.",
                target.host(),
                target.port(),
                target.database(),
                environment.getProperty("spring.datasource.username"));
    }

    private record JdbcTarget(String host, String port, String database) {
        static JdbcTarget from(String jdbcUrl) {
            if (jdbcUrl == null || jdbcUrl.isBlank()) {
                return new JdbcTarget("unknown", "unknown", "unknown");
            }

            String uriValue = jdbcUrl;
            if (uriValue.startsWith("jdbc:")) {
                uriValue = uriValue.substring("jdbc:".length());
            }

            try {
                URI uri = URI.create(uriValue);
                String database = uri.getPath() == null || uri.getPath().isBlank()
                        ? "unknown"
                        : uri.getPath().replaceFirst("^/", "");
                String port = uri.getPort() == -1 ? defaultPort(uri.getScheme()) : String.valueOf(uri.getPort());
                return new JdbcTarget(nullToUnknown(uri.getHost()), port, database);
            } catch (IllegalArgumentException ex) {
                return new JdbcTarget("unparsed", "unparsed", jdbcUrl);
            }
        }

        private static String defaultPort(String scheme) {
            return "postgresql".equalsIgnoreCase(scheme) ? "5432" : "default";
        }

        private static String nullToUnknown(String value) {
            return value == null || value.isBlank() ? "unknown" : value;
        }
    }
}
