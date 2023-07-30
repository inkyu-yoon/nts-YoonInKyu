package com.nts.view;

import com.nts.domain.post.dto.PostDataGetResponse;
import com.nts.domain.post.dto.PostGetPageResponse;
import com.nts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class ViewController {
    private final PostService postService;
    @GetMapping("/")
    public String home(@RequestParam(required = false,defaultValue = "none") String searchCondition,
                       @RequestParam(required = false) String keyword,
                       @PageableDefault(size = 20, sort ="createdDate" , direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<PostGetPageResponse> posts = postService.getPostsBySearch(searchCondition, keyword, pageable);
        PostDataGetResponse postsData = postService.getTotalPostAndCommentCount();

        model.addAttribute("posts", posts);
        model.addAttribute("postsData", postsData);

        return "index";
    }

}
