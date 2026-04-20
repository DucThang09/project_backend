package com.luvina.la;
/**
 * Lớp chính khởi động ứng dụng Spring Boot.
 * Cấu hình và chạy ứng dụng Employee Management System.
 *
 * @author tdthang
 * @version 1.0
 * @since April 13, 2026
 */
import com.luvina.la.config.Constants;
import com.luvina.la.config.DefaultProfileUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Lớp chính của ứng dụng Spring Boot.
 * Khởi động ứng dụng và kiểm tra cấu hình profile.
 */
@SpringBootApplication
public class MainApplication implements InitializingBean {

    /** Logger để ghi log thông tin khởi động ứng dụng. */
    private static final Logger log = LoggerFactory.getLogger(MainApplication.class);

    /** Environment chứa các cấu hình của ứng dụng. */
    private final Environment env;

    /**
     * Constructor để inject Environment.
     *
     * @param env Environment của Spring
     */
    public MainApplication(Environment env) {
        this.env = env;
    }

    /**
     * Kiểm tra cấu hình profile sau khi properties được set.
     * Đảm bảo không chạy đồng thời cả dev và prod profile.
     *
     * @throws Exception nếu có lỗi trong quá trình kiểm tra
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(Constants.SPRING_PROFILE_DEVELOPMENT)
                && activeProfiles.contains(Constants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run "
                    + "with both the 'dev' and 'prod' profiles at the same time.");
        }
    }

    /**
     * Phương thức main khởi động ứng dụng.
     *
     * @param args Các tham số dòng lệnh
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MainApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    /**
     * Ghi log thông tin khởi động ứng dụng bao gồm URL truy cập.
     *
     * @param env Environment chứa cấu hình ứng dụng
     */
    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (!StringUtils.hasText(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        String[] profile = env.getActiveProfiles();
        if (profile.length == 0) {
            profile = env.getDefaultProfiles();
        }

        String textBlock = """
                
                ----------------------------------------------------------
                Application '{}' is running! Access URLs:
                Local: \t\t{}://localhost:{}{}
                External: \t{}://{}:{}{}
                Profile(s): {}
                ----------------------------------------------------------
                
                """;

        log.info(textBlock, env.getProperty("spring.application.name"),
                protocol, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath, profile);
    }
}
