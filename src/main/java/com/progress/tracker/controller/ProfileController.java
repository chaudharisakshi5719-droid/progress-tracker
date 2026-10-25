package com.progress.tracker.controller;

import com.progress.tracker.dto.PasswordChangeDto;
import com.progress.tracker.dto.UserProfileDto;
import com.progress.tracker.entity.User;
import com.progress.tracker.security.CustomUserDetails;
import com.progress.tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profilePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        UserProfileDto profileDto = UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        model.addAttribute("profileDto", profileDto);
        model.addAttribute("passwordDto", new PasswordChangeDto());
        return "profile/profile";
    }

    @PostMapping
    public String updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("profileDto") UserProfileDto profileDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        User currentUser = userService.findById(userDetails.getId());
        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordDto", new PasswordChangeDto());
            return "profile/profile";
        }
        try {
            userService.updateProfile(currentUser, profileDto);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/profile";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("passwordDto", new PasswordChangeDto());
            return "profile/profile";
        }
    }

    @PostMapping("/password")
    public String changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute("passwordDto") PasswordChangeDto passwordDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        User currentUser = userService.findById(userDetails.getId());
        if (bindingResult.hasErrors()) {
            UserProfileDto profileDto = UserProfileDto.builder()
                    .id(currentUser.getId())
                    .username(currentUser.getUsername())
                    .email(currentUser.getEmail())
                    .firstName(currentUser.getFirstName())
                    .lastName(currentUser.getLastName())
                    .build();
            model.addAttribute("profileDto", profileDto);
            return "profile/profile";
        }

        try {
            userService.changePassword(currentUser, passwordDto);
            redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");
            return "redirect:/profile";
        } catch (Exception ex) {
            UserProfileDto profileDto = UserProfileDto.builder()
                    .id(currentUser.getId())
                    .username(currentUser.getUsername())
                    .email(currentUser.getEmail())
                    .firstName(currentUser.getFirstName())
                    .lastName(currentUser.getLastName())
                    .build();
            model.addAttribute("profileDto", profileDto);
            model.addAttribute("errorMessage", ex.getMessage());
            return "profile/profile";
        }
    }
}
