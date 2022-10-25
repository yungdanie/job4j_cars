package ru.job4j.cars.control;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.job4j.cars.service.PostService;

@Controller
@AllArgsConstructor
public class PostControl {

    private final PostService postService;

    @GetMapping("/addPost")
    public String addPost() {
        return "addPost";
    }

    @GetMapping("/allPosts")
    public String allPost() {
        return "allPosts";
    }

    @GetMapping("/getPhotoPost/{id}")
    public ResponseEntity<Resource> getPhotoPost(@PathVariable int id) {
        byte[] photo = postService.getPhoto(id);
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .contentLength(photo.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new ByteArrayResource(photo));
    }

}
