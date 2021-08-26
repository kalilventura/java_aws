package br.com.github.kalilventura.products.controllers;

import br.com.github.kalilventura.products.enums.EventType;
import br.com.github.kalilventura.products.models.Product;
import br.com.github.kalilventura.products.repository.ProductRepository;
import br.com.github.kalilventura.products.service.ProductPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductPublisher productPublisher;

    @Autowired
    public ProductController(ProductRepository productRepository, ProductPublisher productPublisher) {
        this.productRepository = productRepository;
        this.productPublisher = productPublisher;
    }

    @GetMapping
    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id) {
        Optional<Product> optProduct = productRepository.findById(id);
        return optProduct
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product productCreated = productRepository.save(product);

        productPublisher.publishProductEvent(productCreated, EventType.PRODUCT_CREATED, "John Doe");

        return new ResponseEntity<>(productCreated, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable("id") long id) {
        if (!productRepository.existsById(id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        product.setId(id);
        Product productUpdated = productRepository.save(product);

        productPublisher.publishProductEvent(productUpdated, EventType.PRODUCT_UPDATE, "John Doe");

        return new ResponseEntity<>(productUpdated, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
        Optional<Product> optProduct = productRepository.findById(id);
        if (!optProduct.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Product product = optProduct.get();
        productRepository.delete(product);

        productPublisher.publishProductEvent(product, EventType.PRODUCT_DELETED, "John Doe");

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping(path = "/bycode")
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        Optional<Product> optProduct = productRepository.findByCode(code);
        return optProduct.
                map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}

