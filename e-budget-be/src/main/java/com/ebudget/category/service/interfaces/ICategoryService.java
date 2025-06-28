package com.ebudget.category.service.interfaces;

import com.ebudget.category.resource.request.NewCategoryDTO;
import com.ebudget.category.resource.request.UpdateCategoryDTO;
import com.ebudget.category.resource.response.CategoryDTO;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {
    CategoryDTO addCategory(NewCategoryDTO newCategoryDTO);
    void updateCategory(UUID categoryId, UpdateCategoryDTO updateCategoryDTO);
    CategoryDTO getCategory(UUID categoryId);
    List<CategoryDTO> getCategories();
    void deleteCategory(UUID categoryId);
}
