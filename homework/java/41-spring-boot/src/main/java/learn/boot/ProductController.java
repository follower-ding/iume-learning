package learn.boot;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
    public record ProductInput(@NotBlank @Size(max = 100) String name, @Positive int price) { }
    public record BatchInput(@NotBlank @Size(max = 100) String firstName,
                             @NotBlank @Size(max = 100) String secondName,
                             @Positive int price) { }

    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    @GetMapping("/{id}")
    public Product find(@PathVariable("id") long id) { return service.find(id); }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductInput input) {
        var product = service.create(input.name(), input.price());
        return ResponseEntity.created(URI.create("/products/" + product.getId())).body(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable("id") long id, @Valid @RequestBody ProductInput input) {
        return service.update(id, input.name(), input.price());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<Void> batch(@Valid @RequestBody BatchInput input) {
        service.createTwo(input.firstName(), input.secondName(), input.price());
        return ResponseEntity.noContent().build();
    }
}
