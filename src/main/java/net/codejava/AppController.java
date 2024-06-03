package net.codejava;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
@Controller
public class AppController {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
    private ArticleService articleService;

    @Autowired
    private FileStorageService fileStorageService;
	
	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}

	@GetMapping("/dashboard")
	public String viewDashboardPage() {
		return "dashboard";
	}

	@GetMapping("/signin")
	public String viewLoginPage() {
		return "signin2";
	}
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		
		return "signup2";
	}
	
	@PostMapping("/process_register")
	public String processRegister(
		User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		userRepo.save(user);
		
		return "signin2";
	}
	
	@GetMapping("/users")
	public String listUsers(Model model) {
		List<User> listUsers = userRepo.findAll();
		model.addAttribute("listUsers", listUsers);
		
		return "users";
	}


    @GetMapping("/articles")
    public String getAllArticles(Model model) {
        // Get logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;
        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else {
            userEmail = principal.toString();
        }

        List<Article> listArticles = articleService.getArticlesByUserEmail(userEmail);
        model.addAttribute("listArticles", listArticles);
        return "list_of_items"; 
    }

    @GetMapping("/articles/new")
    public String showAddArticleForm(Model model) {
        model.addAttribute("article", new Article());
        return "add_item";  
    }

    @PostMapping("/articles/save")
    public String addArticle(@RequestParam("name") String name,
                              @RequestParam("description") String description,
                              @RequestParam("category") String category,
                              @RequestParam("price") Double price,
                              @RequestParam("quantite") Integer quantite,
                              @RequestParam("image") MultipartFile image) throws IOException {
        String imagePath = fileStorageService.storeFile(image);

        // Get logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;
        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else {
            userEmail = principal.toString();
        }

        Article article = new Article();
        article.setName(name);
        article.setDescription(description);
        article.setCategory(category);
        article.setPrice(price);
        article.setQuantite(quantite);
        article.setImagePath(imagePath);
        System.out.println("acc0");
        articleService.saveArticle(article, userEmail);
		System.out.println("acc1");
        return "redirect:/articles";
    }
    @GetMapping("/change_password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "change_password_2";
    }

    @PostMapping("/process_change_password")
    public String processChangePassword(@RequestParam("oldPassword") String oldPassword,
                                        @RequestParam("newPassword") String newPassword,
                                        Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepo.findByEmail(currentUsername);

        if (user == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Invalid old password");
            return "change_password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        model.addAttribute("message", "Password changed successfully");
        return "signin2";
    }
}
