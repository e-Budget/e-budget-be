package com.ebudget.category.resource;

import com.ebudget.category.resource.request.NewCategoryDTO;
import com.ebudget.category.resource.response.CategoryDTO;
import com.ebudget.category.service.interfaces.ICategoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/category")
@RequiredArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {
    private final ICategoryService categoryService;

    @POST
    public RestResponse<CategoryDTO> addCategory(@Valid NewCategoryDTO newCategoryDTO) {
        CategoryDTO category = categoryService.addCategory(newCategoryDTO);

        return RestResponse.status(RestResponse.Status.CREATED, category);
    }

    @PUT
    @Path("{categoryId}")
    public RestResponse<Void> updateCategory(@PathParam("categoryId") UUID categoryId, NewCategoryDTO updateCategoryDTO) {
        categoryService.updateCategory(categoryId, updateCategoryDTO);

        return RestResponse.status(RestResponse.Status.NO_CONTENT);
    }

    @GET
    @Path("{categoryId}")
    public RestResponse<CategoryDTO> getCategory(@PathParam("categoryId") UUID categoryId) {
        CategoryDTO category = categoryService.getCategory(categoryId);

        return RestResponse.status(RestResponse.Status.OK, category);
    }

    @GET
    public RestResponse<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categories = categoryService.getCategories();

        return RestResponse.status(RestResponse.Status.OK, categories);
    }

    @DELETE
    @Path("{categoryId}")
    public RestResponse<Void> deleteCategory(@PathParam("categoryId") UUID categoryId) {
        categoryService.deleteCategory(categoryId);

        return RestResponse.status(RestResponse.Status.OK);
    }
}
