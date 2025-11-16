package com.wishtracker.controller;

import com.wishtracker.dto.CategoryDTO;
import com.wishtracker.exception.CategoryHasWishesException;
import com.wishtracker.exception.CategoryNotFoundException;
import com.wishtracker.exception.DuplicateCategoryNameException;
import com.wishtracker.model.User;
import com.wishtracker.service.CategoryService;
import com.wishtracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for category management.
 * ALL operations are user-scoped - users can only access their own categories.
 */
@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    /**
     * List all categories for the current user.
     *
     * @param model the model
     * @return categories list template
     */
    @GetMapping
    public String listCategories(Model model) {
        User currentUser = userService.getCurrentUser();
        List<CategoryDTO> categories = categoryService.findAllByUser(currentUser);

        model.addAttribute("categories", categories);
        model.addAttribute("currentUser", currentUser);

        return "categories/list";
    }

    /**
     * Show form to create a new category.
     *
     * @param model the model
     * @return category form template
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User currentUser = userService.getCurrentUser();

        model.addAttribute("category", new CategoryDTO());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isEdit", false);

        return "categories/form";
    }

    /**
     * Create a new category.
     *
     * @param categoryDTO        the category data
     * @param bindingResult      validation results
     * @param redirectAttributes flash messages
     * @param model              the model
     * @return redirect to category list or form if errors
     */
    @PostMapping
    public String createCategory(@Valid @ModelAttribute("category") CategoryDTO categoryDTO,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        User currentUser = userService.getCurrentUser();

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", false);
            return "categories/form";
        }

        try {
            categoryService.create(categoryDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Category '" + categoryDTO.getName() + "' created successfully!");
            return "redirect:/categories";

        } catch (DuplicateCategoryNameException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", false);
            return "categories/form";
        }
    }

    /**
     * Show form to edit an existing category.
     *
     * @param id                 the category ID
     * @param model              the model
     * @param redirectAttributes flash messages
     * @return category form template or redirect if not found
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        try {
            CategoryDTO category = categoryService.findByIdAndUser(id, currentUser);
            model.addAttribute("category", category);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", true);
            return "categories/form";

        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category not found");
            return "redirect:/categories";
        }
    }

    /**
     * Update an existing category.
     *
     * @param id                 the category ID
     * @param categoryDTO        the updated category data
     * @param bindingResult      validation results
     * @param redirectAttributes flash messages
     * @param model              the model
     * @return redirect to category list or form if errors
     */
    @PostMapping("/{id}")
    public String updateCategory(@PathVariable Long id,
                                  @Valid @ModelAttribute("category") CategoryDTO categoryDTO,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        User currentUser = userService.getCurrentUser();

        if (bindingResult.hasErrors()) {
            categoryDTO.setId(id);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", true);
            return "categories/form";
        }

        try {
            categoryService.update(id, categoryDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Category '" + categoryDTO.getName() + "' updated successfully!");
            return "redirect:/categories";

        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category not found");
            return "redirect:/categories";

        } catch (DuplicateCategoryNameException e) {
            categoryDTO.setId(id);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isEdit", true);
            return "categories/form";
        }
    }

    /**
     * Delete a category.
     *
     * @param id                 the category ID
     * @param redirectAttributes flash messages
     * @return redirect to category list
     */
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        User currentUser = userService.getCurrentUser();

        try {
            CategoryDTO category = categoryService.findByIdAndUser(id, currentUser);
            categoryService.delete(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Category '" + category.getName() + "' deleted successfully!");

        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category not found");

        } catch (CategoryHasWishesException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/categories";
    }
}
