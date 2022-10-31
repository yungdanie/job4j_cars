package ru.job4j.cars;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
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
    public FilterRegistrationBean<AuthUserFilter> authUserFilterFilterRegistrationBean() {
        FilterRegistrationBean<AuthUserFilter> registrationBean = new FilterRegistrationBean<>();
        AuthUserFilter authUserFilter = new AuthUserFilter();
        registrationBean.setFilter(authUserFilter);
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<UserSessionFilter> userSessionFilterFilterRegistrationBean(UserService userService) {
        FilterRegistrationBean<UserSessionFilter> registrationBean = new FilterRegistrationBean<>();
        UserSessionFilter userSessionFilter = new UserSessionFilter(userService);
        registrationBean.setFilter(userSessionFilter);
        registrationBean.setOrder(0);
        return registrationBean;
    }
}
