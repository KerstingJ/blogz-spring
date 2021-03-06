package org.launchcode.blogz.controllers;

import javax.servlet.http.HttpServletRequest;

import org.launchcode.blogz.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthenticationController extends AbstractController {
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signupForm() {
		return "signup";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(HttpServletRequest request, Model model) {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String verify = request.getParameter("verify");
		System.out.println("Got request headers");
		
		//look if username is already taken
		User prev_user = userDao.findByUsername(username);
		if (prev_user != null) {
			model.addAttribute("username_error", "Username is already taken");
			return "signup";
		}
		
		// if everything is valid send 'eem along
		if (User.isValidUsername(username) && User.isValidPassword(password) && password.equals(verify)) {

			User user = new User(username, password);
			userDao.save(user);
			this.setUserInSession(request.getSession(), user);
			return "redirect:/blog/newpost";	
		}
		
		
		//handle all the errors if didnt pass above
		if (!User.isValidPassword(password) || password == null) {
			//if password not valid return error
			model.addAttribute("password_error", "Password is invalid");
			System.out.println("Password failed");
		}
		
		if (!User.isValidUsername(username) || username == null) {
			//if username not valid return error
			model.addAttribute("username_error", "Username is Invalid");
			System.out.println("username failed");
		}
		
		if (!password.equals(verify)) {
			//if passwords dont match return error
			model.addAttribute("verify_error", "Passwords do not match");
			System.out.println("verify failed");
			
		}
		
		return "signup";

	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginForm() {
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, Model model) {

		// TODO - implement login
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		User user = userDao.findByUsername(username);
		
		//could we find the username?
		if (user == null) {
			model.addAttribute("error", "Username doesnt exist");
			return "login";
		}
		
		//if the password matches send 'eem along the way
		if (user.isMatchingPassword(password)) {
			this.setUserInSession(request.getSession(), user);
			return "redirect:blog/newpost";
		}
		
		//something didn't match up try again
		model.addAttribute("error", "password is incorrect");
		return "login";
		
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/";
	}
}
