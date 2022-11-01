package ru.job4j.cars.control;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cars.exceptions.UndefinedPostException;
import ru.job4j.cars.exceptions.UndefinedUserException;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.PostService;
import ru.job4j.cars.util.AuthUserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class PostControl {

    private final PostService postService;


    private final static Logger LOGGER = LoggerFactory.getLogger(PostControl.class);

    @GetMapping("/addPost")
    public String addPostForm(Model model, HttpSession httpSession) {
        AuthUserUtil.addUserToModel(httpSession, model);
        return "addPostPage";
    }

    @GetMapping("/changeSalePost/{id}")
    public String changeSalePost(@PathVariable Integer id, Model model, HttpSession session) {
        try {
            if (!postService.changeSaleToTrue(id)) {
                throw new UndefinedPostException("The post was not found");
            }
        } catch (UndefinedPostException e) {
            LOGGER.error("Error in changeSalePost method", e);
        }
        AuthUserUtil.addUserToModel(session, model);
        return "postPage/" + id;
    }

    @GetMapping("/postPage/{id}")
    public String getPostPage(@PathVariable Integer id, HttpSession session, Model model) {
        AuthUserUtil.addUserToModel(session, model);
        Optional<Post> post = postService.getById(id);
        if (post.isEmpty()) {
            model.addAttribute("errorMessage", "Пост не найден");
            return "redirect:/errorPage";
        }
        model.addAttribute("actualPost", post);
        AuthUserUtil.addUserToModel(session, model);
        return "postPage";
    }

    @PostMapping("/addPost")
    public String createPost(@ModelAttribute Post post, @ModelAttribute PriceHistory priceHistory,
                             @RequestParam("input_photo") MultipartFile file,
                             HttpSession session,
                             HttpServletRequest req,
                             RedirectAttributes redirectAttributes) {
        User user = AuthUserUtil.getUser(session);
        try {
            if (AuthUserUtil.isUserGuestOrNull(user)) {
                throw new UndefinedUserException("The user associated with the post is guest or null");
            } else {
                post.setUser(user);
            }
        } catch (UndefinedUserException e) {
            LOGGER.error("Error in createPost method", e);
        }
        byte[] photo = null;
        try {
            photo = file.getBytes();
        } catch (IOException e) {
            LOGGER.trace("Post is loaded without photo");
        }
        post.setPhoto(photo);
        postService.save(post);
        redirectAttributes.addAttribute("post", post);
        redirectAttributes.addAttribute("priceHistory", priceHistory);
        req.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return "redirect:/createPriceHistory";
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
