package com.booking.modules.hotel.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {

    // SVG Hotel Icon
    private static final String FAVICON_SVG = """
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100">
            <rect width="100" height="100" rx="20" fill="#2563eb"/>
            <path d="M50 20 L80 45 L80 80 L20 80 L20 45 Z" fill="#ffffff"/>
            <rect x="42" y="55" width="16" height="25" fill="#2563eb"/>
            <rect x="30" y="48" width="10" height="10" fill="#2563eb"/>
            <rect x="60" y="48" width="10" height="10" fill="#2563eb"/>
        </svg>
        """;

    @GetMapping(value = "/favicon.ico", produces = "image/svg+xml")
    @ResponseBody
    public ResponseEntity<String> getFavicon() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("image/svg+xml"));
        headers.setCacheControl("max-age=86400, public");
        return new ResponseEntity<>(FAVICON_SVG, headers, HttpStatus.OK);
    }
}
