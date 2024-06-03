package net.codejava;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {


    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    public Article saveArticle(Article article, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        article.setUser(user);
        return articleRepository.save(article);
    }
    public List<Article> getArticlesByUserEmail(String email) {
        User user = userRepository.findByEmail(email);
        return articleRepository.findByUser(user);
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }
}
