package com.progress.tracker.controller;

import com.progress.tracker.dto.DashboardStats;
import com.progress.tracker.entity.User;
import com.progress.tracker.security.CustomUserDetails;
import com.progress.tracker.service.DashboardService;
import com.progress.tracker.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        DashboardStats stats = dashboardService.getDashboardStats(user);
        model.addAttribute("user", user);
        model.addAttribute("stats", stats);
        return "dashboard/dashboard";
    }
}
