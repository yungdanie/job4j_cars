package ru.job4j.cars;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import ru.job4j.cars.filter.AuthUserFilter;
import ru.job4j.cars.filter.UserSessionFilter;
import ru.job4j.cars.service.UserService;

@SpringBootApplication
public class CarsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarsApplication.class);
    }

    @Bean
    public SessionFactory sf() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @Bean
    public FilterRegistrationBean<UserSessionFilter>
    userSessionFilterFilterRegistrationBean(UserService userService,
                                            @Value("SESSION_USER_NAME") String sessionUserName,
                                            @Value("COOKIE_UUID_USER_NAME") String cookieUuidUserName) {
        FilterRegistrationBean<UserSessionFilter> registrationBean = new FilterRegistrationBean<>();
        UserSessionFilter userSessionFilter = new UserSessionFilter(userService, sessionUserName, cookieUuidUserName);
        registrationBean.setFilter(userSessionFilter);
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AuthUserFilter>
    authUserFilterFilterRegistrationBean(@Value("SESSION_USER_NAME") String sessionUserName,
                                         @Value("NO_USER_REDIRECT_LINK") String noUserRedirectLink,
                                         @Value("AUTH_USER_REDIRECT_LINK") String authUserRedirectLink,
                                         @Value("NO_USER_ACCESS_RESTRICTION") String noUserAccessRestriction,
                                         @Value("AUTH_USER_ACCESS_RESTRICTION") String authUserAccessRestriction) {
        FilterRegistrationBean<AuthUserFilter> registrationBean = new FilterRegistrationBean<>();
        AuthUserFilter authUserFilter = new AuthUserFilter(
                sessionUserName,
                noUserRedirectLink,
                authUserRedirectLink,
                noUserAccessRestriction,
                authUserAccessRestriction
        );
        registrationBean.setFilter(authUserFilter);
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
