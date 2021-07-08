package ch.admin.bag.covidcertificate.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    @GetMapping
    @PreAuthorize("hasRole('bag-cc-certificatecreator')")
    public ResponseEntity<HttpStatus> print() {
        log.info("Hello called");

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
