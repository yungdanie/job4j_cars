package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.hibernate.ObjectNotFoundException;
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
import ru.job4j.cars.exception.UndefinedPostException;
import ru.job4j.cars.exception.UndefinedUserException;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.PostService;
import ru.job4j.cars.util.AuthUserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    private final static Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @GetMapping("/addPost")
    public String addPostForm(Model model, HttpSession httpSession) {
        AuthUserUtil.addUserToModel(httpSession, model);
        return "post/addPostPage";
    }

    @GetMapping("/changeSalePost/{id}")
    public String changeSalePost(@PathVariable Integer id, Model model, HttpSession session) throws ObjectNotFoundException {
        postService.changeSaleToTrue(id);
        return "redirect:postPage/" + id;
    }

    @GetMapping("/postPage/{id}")
    public String getPostPage(@PathVariable Integer id, HttpSession session, Model model) throws UndefinedPostException {
        AuthUserUtil.addUserToModel(session, model);
        Post post = postService.getById(id);
        model.addAttribute("actualPost", post);
        AuthUserUtil.addUserToModel(session, model);
        return "post/postPage";
    }

    @PostMapping("/addPost")
    public String createPost(@ModelAttribute Post post, @ModelAttribute PriceHistory priceHistory,
                             @RequestParam("input_photo") MultipartFile file,
                             HttpSession session,
                             HttpServletRequest req,
                             RedirectAttributes redirectAttributes) throws UndefinedUserException {
        User user = AuthUserUtil.getUser(session);
        if (AuthUserUtil.isUserGuestOrNull(user)) {
            throw new UndefinedUserException("The user associated with the post is guest or null");
        }
        post.setUser(user);
        byte[] photo = null;
        try {
            photo = file.getBytes();
        } catch (IOException e) {
            LOGGER.info("Photo can not be loaded");
        }
        post.setPhoto(photo);
        postService.save(post);
        redirectAttributes.addFlashAttribute("post", post);
        redirectAttributes.addFlashAttribute("priceHistory", priceHistory);
        req.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return "redirect:/createPriceHistory";
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
