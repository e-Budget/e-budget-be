package com.ebudget.category.service;

import com.ebudget.category.model.Category;
import com.ebudget.category.repository.CategoryRepository;
import com.ebudget.category.resource.request.NewCategoryDTO;
import com.ebudget.category.resource.response.CategoryDTO;
import com.ebudget.category.service.interfaces.ICategoryService;
import com.ebudget.core.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDTO addCategory(NewCategoryDTO newCategoryDTO) {
        Category category = Category.builder()
                .categoryName(newCategoryDTO.categoryName())
                .build();

        categoryRepository.persistAndFlush(category);

        return new CategoryDTO(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void updateCategory(UUID categoryId, NewCategoryDTO updateCategoryDTO) {
        Category category = categoryRepository.findById(categoryId);

        if(category == null) {
            throw new EntityNotFoundException(Category.class, categoryId);
        }

        category.update(updateCategoryDTO);
    }

    @Override
    public CategoryDTO getCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId);

        if(category == null) {
            throw new EntityNotFoundException(Category.class, categoryId);
        }

        return new CategoryDTO(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    @Override
    public List<CategoryDTO> getCategories() {
        List<Category> categories = categoryRepository.listAll();

        return categories.stream()
                .map(category -> new CategoryDTO(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getCreatedAt(),
                        category.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId);

        if(category == null) {
            throw new EntityNotFoundException(Category.class, categoryId);
        }

        categoryRepository.delete(category);
    }
}
