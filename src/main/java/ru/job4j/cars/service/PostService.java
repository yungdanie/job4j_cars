package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.PostRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final Logger logger;
    private static final int defaultImageSizeBytes = 13000;

    public byte[] getPhoto(int id) {
        var bytes = postRepository.getPhoto(id);
        if (bytes == null) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(defaultImageSizeBytes)) {
                BufferedImage bufferedImage = ImageIO.read(new File("./src/main/resources/img/defaultPostPhoto.png"));
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return postRepository.getPhoto(id);
    }

    public List<Post> getAll() {
        return postRepository.getAll();
    }

    public List<Post> getAllFetchingPriceHistory() {
        return postRepository.getAllFetchingPriceHistory();
    }
}
