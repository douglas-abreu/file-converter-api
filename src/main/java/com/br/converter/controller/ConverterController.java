
package com.br.converter.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.converter.model.ApiResponse;
import com.br.converter.service.ConverterService;
import com.br.converter.util.FileExtension;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("converter")
public class ConverterController {
    private final ConverterService<List<List<String>>> converter;

    @GetMapping("/generic")
    public ResponseEntity<ApiResponse<String>> getConvertedValue(
            @RequestParam Map<String, String> value, @RequestParam FileExtension fileExtension) {
        List<List<String>> list = converter.mapToList(value, fileExtension);
        ApiResponse<String> response = converter.generateFile(list, fileExtension);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
