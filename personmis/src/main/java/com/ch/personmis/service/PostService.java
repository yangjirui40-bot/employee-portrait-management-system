package com.ch.personmis.service;

import com.ch.personmis.entity.Depart;
import com.ch.personmis.entity.DepartByCon;
import com.ch.personmis.entity.Post;
import com.ch.personmis.entity.PostByCon;

import java.util.List;
import java.util.Map;

/**
 * 岗位（Post）业务服务接口
 *
 * 功能说明：
 * 定义岗位管理相关能力增删改查。
 */
public interface PostService {
    String addPost(Post post);
    Map<String, Object> selectPostByPage(PostByCon postByCon);
    Map<String, Object> selectPostByCon(PostByCon postByCon);
    Post getPostDetail(int id);
    String updatePost(Post post);
    List<Post> getPost();
    String deletePost(int id);
}
