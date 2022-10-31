package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.PostRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final static Logger LOGGER = LoggerFactory.getLogger(PostService.class);
    private static final int DEFAULT_IMAGE_SIZE_BYTES = 13000;

    public Boolean changeSaleToTrue(Integer id) {
        return postRepository.changeSaleToTrue(id) > 0;
    }

    public Optional<Post> getById(int id) {
        return postRepository.getById(id);
    }

    public byte[] getPhoto(int id) {
        var bytes = postRepository.getPhoto(id);
        if (bytes.isEmpty()) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(DEFAULT_IMAGE_SIZE_BYTES)) {
                BufferedImage bufferedImage = ImageIO.read(new File("./src/main/resources/img/defaultPostPhoto.png"));
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                LOGGER.error("Failed to get photo", e);
                throw new RuntimeException(e);
            }
        }
        return bytes.get();
    }

    public List<Post> getAll() {
        return postRepository.getAll();
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getAllFetchPriceHAndParticipates() {
        return postRepository.getAllFetchPriceHAndParticipates();
    }
}
