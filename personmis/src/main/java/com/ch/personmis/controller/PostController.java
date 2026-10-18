package com.ch.personmis.controller;

import com.ch.personmis.entity.Post;
import com.ch.personmis.entity.PostByCon;

import com.ch.personmis.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class PostController {
    @Resource
    private PostService postService;
    @GetMapping("/getPostByPage")
    public Map<String, Object> selectPostByPage(PostByCon postByCon) {
        return postService.selectPostByPage(postByCon);
    }
    @PostMapping("/addPost")
    public  String addPost(@RequestBody Post post){
        return  postService.addPost(post);
    }
    @PostMapping("/selectPostByCon")
    public Map<String, Object> selectPostByCon(@RequestBody PostByCon postByCon) {
        return postService.selectPostByCon(postByCon);
    }
    @GetMapping("/getPostDetail")
    public Post getPostDetail(int id) {
        return postService.getPostDetail(id);
    }
    @PostMapping("/updatePost")
    public  String updatePost(@RequestBody Post post){
        return  postService.updatePost(post);
    }
    @GetMapping("/getPost")
    public List<Post> getPost() {
        return postService.getPost();
    }
    @PostMapping("/deletePost")
    public  String deletePost(int id){
        return postService.deletePost(id);
    }
}
