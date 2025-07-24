package com.example.ocrapp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class OcrController {

    private final OcrService ocrService;
    private final OcrResultRepository ocrResultRepository;

    public OcrController(OcrService ocrService, OcrResultRepository ocrResultRepository) {
        this.ocrService = ocrService;
        this.ocrResultRepository = ocrResultRepository;
    }

    @GetMapping("/")
    public String index(Model model, Pageable pageable) {
        Page<OcrResult> page = ocrResultRepository.findAll(pageable);
        model.addAttribute("page", page);
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            String text = ocrService.recognizeText(file);
            String fileType = file.getContentType();
            OcrResult ocrResult = new OcrResult(file.getOriginalFilename(), file.getBytes(), text, fileType);
            ocrResultRepository.save(ocrResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        OcrResult ocrResult = ocrResultRepository.findById(id).orElse(null);
        if (ocrResult != null && ocrResult.getImage() != null) {
            MediaType mediaType = MediaType.parseMediaType(ocrResult.getFileType());
            return ResponseEntity.ok().contentType(mediaType).body(ocrResult.getImage());
        }
        return ResponseEntity.notFound().build();
    }
}

