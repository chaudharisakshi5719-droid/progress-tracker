package com.progress.tracker.service;

import com.progress.tracker.dto.CategoryDto;
import com.progress.tracker.entity.Category;
import com.progress.tracker.exception.InvalidTaskDateException;
import com.progress.tracker.exception.ResourceNotFoundException;
import com.progress.tracker.repository.CategoryRepository;
import com.progress.tracker.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;

    public CategoryService(CategoryRepository categoryRepository, TaskRepository taskRepository) {
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToDto(category);
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new InvalidTaskDateException("Category with name '" + dto.getName().trim() + "' already exists");
        }

        Category category = Category.builder()
                .name(dto.getName().trim())
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .color(dto.getColor() != null ? dto.getColor() : "#0d6efd")
                .build();

        Category saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getName().equalsIgnoreCase(dto.getName().trim()) && 
            categoryRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new InvalidTaskDateException("Category with name '" + dto.getName().trim() + "' already exists");
        }

        category.setName(dto.getName().trim());
        category.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        category.setColor(dto.getColor() != null ? dto.getColor() : "#0d6efd");

        Category updated = categoryRepository.save(category);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        long taskCount = taskRepository.countByCategoryId(id);
        if (taskCount > 0) {
            throw new InvalidTaskDateException("Cannot delete category '" + category.getName() + "' because it is referenced by " + taskCount + " tasks");
        }

        categoryRepository.delete(category);
    }

    private CategoryDto mapToDto(Category category) {
        long taskCount = taskRepository.countByCategoryId(category.getId());
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .color(category.getColor())
                .taskCount(taskCount)
                .build();
    }
}
