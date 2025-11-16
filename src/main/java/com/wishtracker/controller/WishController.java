package com.wishtracker.controller;

import com.wishtracker.dto.CategoryDTO;
import com.wishtracker.dto.WishDTO;
import com.wishtracker.exception.InvalidStatusTransitionException;
import com.wishtracker.exception.WishNotFoundException;
import com.wishtracker.model.User;
import com.wishtracker.model.WishStatus;
import com.wishtracker.service.CategoryService;
import com.wishtracker.service.UserService;
import com.wishtracker.service.WishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for wish management.
 * ALL operations are user-scoped - users can only access their own wishes.
 */
@Controller
@RequestMapping("/wishes")
@RequiredArgsConstructor
@Slf4j
public class WishController {

    private final WishService wishService;
    private final CategoryService categoryService;
    private final UserService userService;

    /**
     * Display Kanban board with wishes grouped by status.
     * Optionally filter by category.
     *
     * @param categoryId optional category filter
     * @param model      the model
     * @return wishes list (Kanban board) template
     */
    @GetMapping
    public String listWishes(@RequestParam(required = false) Long categoryId,
                             Model model) {
        User currentUser = userService.getCurrentUser();
        List<CategoryDTO> categories = categoryService.findAllByUser(currentUser);

        // Get wishes (all or filtered by category)
        List<WishDTO> wishes;
        if (categoryId != null) {
            wishes = wishService.findByUserAndCategory(currentUser, categoryId);
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            wishes = wishService.findAllByUser(currentUser);
        }

        // Group wishes by status for Kanban board
        Map<WishStatus, List<WishDTO>> wishesByStatus = wishes.stream()
                .collect(Collectors.groupingBy(WishDTO::getStatus));

        model.addAttribute("wishWishes", wishesByStatus.getOrDefault(WishStatus.WISH, List.of()));
        model.addAttribute("inProgressWishes", wishesByStatus.getOrDefault(WishStatus.IN_PROGRESS, List.of()));
        model.addAttribute("achievedWishes", wishesByStatus.getOrDefault(WishStatus.ACHIEVED, List.of()));
        model.addAttribute("categories", categories);
        model.addAttribute("currentUser", currentUser);

        return "wishes/list";
    }

    /**
     * Show form to create a new wish.
     *
     * @param model the model
     * @return wish form template
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User currentUser = userService.getCurrentUser();
        List<CategoryDTO> categories = categoryService.findAllByUser(currentUser);

        WishDTO wish = new WishDTO();
        wish.setStatus(WishStatus.WISH); // Default status

        model.addAttribute("wish", wish);
        model.addAttribute("categories", categories);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isEdit", false);
        model.addAttribute("statuses", WishStatus.values());

        return "wishes/form";
    }

    /**
     * Create a new wish.
     *
     * @param wishDTO            the wish data
     * @param bindingResult      validation results
     * @param redirectAttributes flash messages
     * @param model              the model
     * @return redirect to wish list or form if errors
     */
    @PostMapping
    public String createWish(@Valid @ModelAttribute("wish") WishDTO wishDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User currentUser = userService.getCurrentUser();

        if (bindingResult.hasErrors()) {
            List<CategoryDTO> categories = categoryService.findAllByUser(currentUser);
            model.addAttribute("categories", categories);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", false);
            model.addAttribute("statuses", WishStatus.values());
            return "wishes/form";
        }

        try {
            wishService.create(wishDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Wish '" + wishDTO.getTitle() + "' created successfully!");
            return "redirect:/wishes";

        } catch (Exception e) {
            log.error("Error creating wish", e);
            List<CategoryDTO> categories = categoryService.findAllByUser(currentUser);
            model.addAttribute("errorMessage", "Error creating wish: " + e.getMessage());
            model.addAttribute("categories", categories);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", false);
            model.addAttribute("statuses", WishStatus.values());
            return "wishes/form";
        }
    }

    /**
     * View wish details.
     *
     * @param id                 the wish ID
     * @param model              the model
     * @param redirectAttributes flash messages
     * @return wish view template or redirect if not found
     */
    @GetMapping("/{id}")
    public String viewWish(@PathVariable Long id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        try {
            WishDTO wish = wishService.findByIdAndUser(id, currentUser);
            model.addAttribute("wish", wish);
            model.addAttribute("currentUser", currentUser);
            return "wishes/view";

        } catch (WishNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wish not found");
            return "redirect:/wishes";
        }
    }

    /**
     * Show form to edit an existing wish.
     *
     * @param id                 the wish ID
     * @param model              the model
     * @param redirectAttributes flash messages
     * @return wish form template or redirect if not found
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        try {
            WishDTO wish = wishService.findByIdAndUser(id, currentUser);
            List<CategoryDTO> categories = categoryService.findAllByUser(currentUser);

            model.addAttribute("wish", wish);
            model.addAttribute("categories", categories);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", true);
            model.addAttribute("statuses", WishStatus.values());

            return "wishes/form";

        } catch (WishNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wish not found");
            return "redirect:/wishes";
        }
    }

    /**
     * Update an existing wish.
     *
     * @param id                 the wish ID
     * @param wishDTO            the updated wish data
     * @param bindingResult      validation results
     * @param redirectAttributes flash messages
     * @param model              the model
     * @return redirect to wish list or form if errors
     */
    @PostMapping("/{id}")
    public String updateWish(@PathVariable Long id,
                             @Valid @ModelAttribute("wish") WishDTO wishDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User currentUser = userService.getCurrentUser();

        if (bindingResult.hasErrors()) {
            wishDTO.setId(id);
            List<CategoryDTO> categories = categoryService.findAllByUser(currentUser);
            model.addAttribute("categories", categories);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", true);
            model.addAttribute("statuses", WishStatus.values());
            return "wishes/form";
        }

        try {
            wishService.update(id, wishDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Wish '" + wishDTO.getTitle() + "' updated successfully!");
            return "redirect:/wishes";

        } catch (WishNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wish not found");
            return "redirect:/wishes";

        } catch (InvalidStatusTransitionException e) {
            wishDTO.setId(id);
            List<CategoryDTO> categories = categoryService.findAllByUser(currentUser);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categories);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", true);
            model.addAttribute("statuses", WishStatus.values());
            return "wishes/form";
        }
    }

    /**
     * Delete a wish.
     *
     * @param id                 the wish ID
     * @param redirectAttributes flash messages
     * @return redirect to wish list
     */
    @PostMapping("/{id}/delete")
    public String deleteWish(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        try {
            WishDTO wish = wishService.findByIdAndUser(id, currentUser);
            wishService.delete(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Wish '" + wish.getTitle() + "' deleted successfully!");

        } catch (WishNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wish not found");
        }

        return "redirect:/wishes";
    }

    /**
     * Change wish status (for status transition buttons).
     * Validates transition: WISH → IN_PROGRESS → ACHIEVED
     *
     * @param id                 the wish ID
     * @param status             the new status
     * @param redirectAttributes flash messages
     * @return redirect to wish list
     */
    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam WishStatus status,
                               RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        try {
            WishDTO updatedWish = wishService.changeStatus(id, status, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Wish '" + updatedWish.getTitle() + "' status changed to " + status.getDisplayName());

        } catch (WishNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wish not found");

        } catch (InvalidStatusTransitionException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/wishes";
    }
}
