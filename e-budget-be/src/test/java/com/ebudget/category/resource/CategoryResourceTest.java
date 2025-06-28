package com.ebudget.category.resource;

import com.ebudget.category.model.Category;
import com.ebudget.category.repository.CategoryRepository;
import com.ebudget.category.resource.request.NewCategoryDTO;
import com.ebudget.category.resource.response.CategoryDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("Category Resource")
@TestHTTPEndpoint(CategoryResource.class)
class CategoryResourceTest {
    @Inject
    CategoryRepository categoryRepository;

    private Category sampleCategory;

    @BeforeEach
    @Transactional
    void setup() {
        sampleCategory = Category.builder()
                .categoryName("categoryName")
                .build();

        categoryRepository.persistAndFlush(sampleCategory);
    }

    @AfterEach
    @Transactional
    void destroy() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Should add a category")
    void shouldAddCategory() {
        NewCategoryDTO newCategoryDTO = new NewCategoryDTO("categoryName");

        CategoryDTO response = given()
            .contentType(ContentType.JSON)
            .body(newCategoryDTO)
        .when()
            .post()
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<CategoryDTO>() {});

        assertThat(response.getCategoryId()).isNotNull();
        assertThat(response.getCategoryId()).isInstanceOf(UUID.class);
        assertThat(response.getCategoryName()).isEqualTo(newCategoryDTO.categoryName());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getCreatedAt()).isInstanceOf(LocalDateTime.class);
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isInstanceOf(LocalDateTime.class);

    }

    @Test
    @DisplayName("Should update a category")
    void shouldUpdateCategory() {
        NewCategoryDTO updateCategoryDTO = new NewCategoryDTO("updatedCategoryName");

        given()
            .contentType(ContentType.JSON)
            .body(updateCategoryDTO)
        .when()
            .put(String.valueOf(sampleCategory.getCategoryId()))
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should get a category")
    void shouldGetCategory() {
        CategoryDTO response = given()
            .contentType(ContentType.JSON)
        .when()
            .get(String.valueOf(sampleCategory.getCategoryId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<CategoryDTO>() {});

        assertThat(response.getCategoryId()).isEqualTo(sampleCategory.getCategoryId());
        assertThat(response.getCategoryName()).isEqualTo(sampleCategory.getCategoryName());
        assertThat(response.getCreatedAt()).isEqualTo(sampleCategory.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(sampleCategory.getUpdatedAt());
    }

    @Test
    @DisplayName("Should get all categories")
    void shouldGetCategories() {
        List<CategoryDTO> response = given()
            .contentType(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<List<CategoryDTO>>() {});

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getCategoryId()).isEqualTo(sampleCategory.getCategoryId());
        assertThat(response.getFirst().getCategoryName()).isEqualTo(sampleCategory.getCategoryName());
        assertThat(response.getFirst().getCreatedAt()).isEqualTo(sampleCategory.getCreatedAt());
        assertThat(response.getFirst().getUpdatedAt()).isEqualTo(sampleCategory.getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete a category")
    void shouldDeleteCategory() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete(String.valueOf(sampleCategory.getCategoryId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode());
    }
}