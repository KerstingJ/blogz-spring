package org.launchcode.blogz.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.launchcode.blogz.models.Post;
import org.launchcode.blogz.models.User;
import org.launchcode.blogz.models.dao.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PostController extends AbstractController {
	
	@Autowired
	PostDao postDao;
	
	@RequestMapping(value = "/blog/newpost", method = RequestMethod.GET)
	public String newPostForm() {
		return "newpost";
	}
	
	@RequestMapping(value = "/blog/newpost", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, Model model) {
		
		//get all the request info
		String title = request.getParameter("title");
		String body = request.getParameter("body");
		User user = this.getUserFromSession(request.getSession());
		
		
		//lets make sure the post has everything we need
		if (title == null || Post.isValidString(title)) {
			model.addAttribute("error", "please add a title");
			return "/blog/newpost";
		} else if (body == null || Post.isValidString(body)) {
			model.addAttribute("error", "please add a body to your post");
			return "/blog/newpost";
		}
		
		//add that mafk to the database if its got all the goods
		Post post = new Post(title, body, user);
		postDao.save(post);
		
		
		return String.format("redirect:/blog/%s/%s", user.getUsername(), post.getUid()); // TODO - this redirect should go to the new post's page  		
	}
	
	@RequestMapping(value = "/blog/{username}/{uid}", method = RequestMethod.GET)
	public String singlePost(@PathVariable String username, @PathVariable int uid, Model model) {
		
		Post post = postDao.findByUid(uid);
		
		model.addAttribute("post", post);
		return "post";
	}
	
	@RequestMapping(value = "/blog/{username}", method = RequestMethod.GET)
	public String userPosts(@PathVariable String username, Model model) {
		
		
		// TODO - implement userPosts
		User author = userDao.findByUsername(username);
		List<Post> posts = postDao.findAllByAuthor(author);
		
		model.addAttribute("posts", posts);
		return "blog";
	}
	
}
