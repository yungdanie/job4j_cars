package ru.job4j.cars;

import org.assertj.core.api.Assertions;
import org.hibernate.Session;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.MainRepository;
import ru.job4j.cars.repository.PostRepository;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

class PostHibernateTest {
    private final MainRepository mainRepository = new MainRepository(new MetadataSources(
            new StandardServiceRegistryBuilder()
            .configure().build())
            .buildMetadata()
            .buildSessionFactory());
    private final PostRepository postRepository = new PostRepository(mainRepository);

    @BeforeEach
    public void clearDataBase() {
        mainRepository.tx((Function<Session, Object>) session -> session.createQuery("delete from Post").executeUpdate());
    }

    @Test
    public void getAllPostsWithNotNullPhoto() {
        Post post = new Post();
        Post post2 = new Post();
        Post noPhotoPost = new Post();
        byte[] photo = new byte[] {1, 1, 1 ,1};
        byte[] photo2 = new byte[] {1, 0, 0, 0};
        post2.setPhoto(photo);
        post.setPhoto(photo2);
        post.setText("text");
        post2.setText("text2");
        noPhotoPost.setText("no photo");
        postRepository.save(post);
        postRepository.save(post2);
        postRepository.save(noPhotoPost);
        List<Post> resultList = postRepository.getAllWithNoEmptyPhoto();
        Assertions.assertThat(resultList).isEqualTo(List.of(post, post2));
    }

    @Test
    public void whenSavePost() {
        Post post = new Post();
        post.setText("post_text");
        postRepository.save(post);
        Post savedPost = postRepository.getById(post.getId()).orElse(new Post());
        Assertions.assertThat(savedPost.getText()).isEqualTo(post.getText());
    }

    @Test
    public void whenGetPostWithTextLikeKey() {
        Post firstPost = new Post();
        Post secondPost = new Post();
        String key = "key";
        firstPost.setText("key_of_post");
        secondPost.setText("post_of_key");
        postRepository.save(firstPost);
        postRepository.save(secondPost);
        Assertions.assertThat(postRepository.getByTextContains(key)).isEqualTo(List.of(firstPost, secondPost));
    }

    @Test
    public void whenGetLastDayPost() {
        Post oldPost = new Post();
        Post newPost = new Post();
        LocalDateTime oldDateTime = LocalDateTime.of(2000, 1, 10, 14, 15);
        LocalDateTime newDateTime = LocalDateTime.of(2010, 1, 10, 14, 15);
        oldPost.setCreated(oldDateTime);
        newPost.setCreated(newDateTime);
        postRepository.save(oldPost);
        postRepository.save(newPost);
        Post post = postRepository.getLastDay().get(0);
        Assertions.assertThat(postRepository.getLastDay().get(0)).isEqualTo(newPost);
    }

    @Test
    public void whenTruncDate() {
        Post lastPost = new Post();
        Post anotherLastPost = new Post();
        Post oldPost = new Post();
        Post anotherOldPost = new Post();
        LocalDateTime dateTime = LocalDateTime.of(2022, 10, 10, 10, 10);
        lastPost.setText("testing1");
        lastPost.setCreated(dateTime);
        oldPost.setText("testing2");
        oldPost.setCreated(dateTime.minusDays(1));
        anotherOldPost.setText("testing3");
        oldPost.setCreated(dateTime.minusMonths(1));
        anotherLastPost.setText("testing4");
        anotherLastPost.setCreated(dateTime.minusHours(1));
        postRepository.save(lastPost);
        postRepository.save(oldPost);
        postRepository.save(anotherOldPost);
        postRepository.save(anotherLastPost);
        List<Post> list = mainRepository.tx(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Post> listQuery = cb.createQuery(Post.class);
            Root<Post> root = listQuery.from(Post.class);
            listQuery.select(root);
            Subquery<LocalDate> sub = listQuery.subquery(LocalDate.class);
            Path<LocalDate> selection = sub.from(Post.class).get("created");
            sub.select(cb.greatest(selection).as(LocalDate.class));
            listQuery.where(cb.equal(sub, root.get("created").as(LocalDate.class)));
            return session.createQuery(listQuery).list();
        });
        Assertions.assertThat(list).isEqualTo(List.of(lastPost, anotherLastPost));
    }
}