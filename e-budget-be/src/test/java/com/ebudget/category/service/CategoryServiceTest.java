package com.ebudget.category.service;

import com.ebudget.category.model.Category;
import com.ebudget.category.repository.CategoryRepository;
import com.ebudget.category.resource.request.NewCategoryDTO;
import com.ebudget.category.resource.request.UpdateCategoryDTO;
import com.ebudget.category.resource.response.CategoryDTO;
import com.ebudget.core.exceptions.EntityNotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@DisplayName("Category Service")
class CategoryServiceTest {
    @Inject
    CategoryService categoryService;
    @InjectMock
    CategoryRepository categoryRepository;

    private UUID sampleCategoryId;
    private Category sampleCategory;

    @BeforeEach
    void setup() {
        sampleCategoryId = UUID.randomUUID();
        sampleCategory = Category.builder()
                .categoryId(sampleCategoryId)
                .categoryName("categoryName")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should add a category")
    void shouldAddCategory() {
        // given
        NewCategoryDTO newCategoryDTO = new NewCategoryDTO("categoryName");

        doNothing().when(categoryRepository).persistAndFlush(any(Category.class));

        // when
        CategoryDTO category = categoryService.addCategory(newCategoryDTO);

        // then
        assertThat(category).isInstanceOf(CategoryDTO.class);
        assertThat(category.getCategoryName()).isEqualTo(newCategoryDTO.categoryName());

        verify(categoryRepository, times(1)).persistAndFlush(any(Category.class));
    }

    @Test
    @DisplayName("Should update a category")
    void shouldUpdateCategory() {
        // given
        UpdateCategoryDTO updateCategoryDTO = new UpdateCategoryDTO("updatedCategoryName");

        when(categoryRepository.findById(any(UUID.class))).thenReturn(sampleCategory);

        // when
        categoryService.updateCategory(sampleCategoryId, updateCategoryDTO);

        // then
        assertThat(sampleCategory.getCategoryId()).isEqualTo(sampleCategoryId);
        assertThat(sampleCategory.getCategoryName()).isEqualTo(updateCategoryDTO.categoryName());

        verify(categoryRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on update a non-existing category")
    void shouldThrowExceptionOnUpdateCategoryNonExistingCategory() {
        // given
        UpdateCategoryDTO updateCategoryDTO = new UpdateCategoryDTO("updatedCategoryName");

        when(categoryRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            categoryService.updateCategory(sampleCategoryId, updateCategoryDTO);
        });

        verify(categoryRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should get a category")
    void shouldGetCategory() {
        // given
        when(categoryRepository.findById(any(UUID.class))).thenReturn(sampleCategory);

        // when
        CategoryDTO category = categoryService.getCategory(sampleCategoryId);

        // then
        assertThat(category).isInstanceOf(CategoryDTO.class);
        assertThat(category.getCategoryId()).isEqualTo(sampleCategoryId);
        assertThat(category.getCategoryName()).isEqualTo(sampleCategory.getCategoryName());
        assertThat(category.getCreatedAt()).isEqualTo(sampleCategory.getCreatedAt());
        assertThat(category.getUpdatedAt()).isEqualTo(sampleCategory.getUpdatedAt());

        verify(categoryRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should throw exception on get a non-existing category")
    void shouldThrowExceptionOnGetCategoryNonExistingCategory() {
        // given
        when(categoryRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            categoryService.getCategory(sampleCategoryId);
        });

        verify(categoryRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should get all categories")
    void shouldGetCategories() {
        // given
        when(categoryRepository.listAll()).thenReturn(List.of(sampleCategory));

        // when
        List<CategoryDTO> categories = categoryService.getCategories();

        // then
        assertThat(categories).hasSize(1);

        verify(categoryRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Should delete a category")
    void shouldDeleteCategory() {
        // given
        when(categoryRepository.findById(any(UUID.class))).thenReturn(sampleCategory);
        doNothing().when(categoryRepository).delete(any(Category.class));

        // when / then
        assertThatNoException().isThrownBy(() -> {
            categoryService.deleteCategory(sampleCategoryId);
        });

        verify(categoryRepository, times(1)).findById(any(UUID.class));
        verify(categoryRepository, times(1)).delete(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception on delete a non-existing category")
    void shouldThrowExceptionOnDeleteCategoryNonExistingCategory() {
        // given
        when(categoryRepository.findById(any(UUID.class))).thenReturn(null);

        // when / then
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> {
            categoryService.deleteCategory(sampleCategoryId);
        });

        verify(categoryRepository, times(1)).findById(any(UUID.class));
    }
}