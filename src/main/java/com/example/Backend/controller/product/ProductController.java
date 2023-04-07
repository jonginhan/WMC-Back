package com.example.Backend.controller.product;

import com.example.Backend.controller.product.form.ProductRegisterForm;
import com.example.Backend.entity.product.Category;
import com.example.Backend.entity.product.Product;
import com.example.Backend.service.category.CategoryService;
import com.example.Backend.service.product.ProductService;
import com.example.Backend.service.product.request.ProductRegisterRequest;
import com.example.Backend.service.product.response.ProductListResponse;
import com.example.Backend.service.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8080", allowedHeaders = "*")
public class ProductController {

    final private ProductService productService;
    final private CategoryService categoryService;

    @PostMapping(value = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public boolean productRegister(@ModelAttribute ProductRegisterForm form) throws IOException {
        log.info("productRegister(): " + form);
        log.info("Files received: " + form.getFileList().size());

        List<String> savedFiles = saveFiles(form.getFileList());
        Category category = categoryService.getCategoryById(form.getCategoryId());

        ProductRegisterRequest request = new ProductRegisterRequest(form.getName(), form.getDescription(), form.getStock(), form.getPrice(), category, savedFiles);

        return productService.register(request);
    }

    private List<String> saveFiles(List<MultipartFile> fileList) {
        List<String> savedFilePaths = new ArrayList<>();
        String basePath = "/Users/jonginhan/Documents/GitHub/finalProject/WMC-Front/src/assets/productImages/";

        for (MultipartFile multipartFile : fileList) {
            log.info("saveFiles() - filename: " + multipartFile.getOriginalFilename());
            log.info("saveFiles() - file size: " + multipartFile.getSize());

            String savedFileName = basePath + multipartFile.getOriginalFilename();
            savedFilePaths.add("assets/productImages/" + multipartFile.getOriginalFilename());

            try {
                FileOutputStream writer = new FileOutputStream(savedFileName);
                writer.write(multipartFile.getBytes());
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return savedFilePaths;
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody Long productId) {
        return productService.delete(productId);
    }

    @GetMapping("/list")
    public List<ProductListResponse> getAllProduct() {

        return productService.getAllProducts();
    }

    @GetMapping("/detail")
    public ProductResponse getProductDetail(@RequestParam Long productId) {
        return productService.getProductById(productId);
    }

    @GetMapping("/listByCategory")
    public List<ProductListResponse> getProductsByCategory(@RequestParam Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

}
