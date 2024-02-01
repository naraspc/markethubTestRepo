package org.hanghae.markethub.domain.picture.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.picture.service.PictureService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/pictures")
@RequiredArgsConstructor
public class PictureController {

	private final PictureService pictureService;

	@PostMapping("/{itemId}")
	public void createPicture(@PathVariable Long itemId,
							  @RequestPart("files") List<MultipartFile> file) throws IOException {
		pictureService.createPicture(itemId, file);
	}

	@DeleteMapping("/{itemId}")
	public void deletePicture(@PathVariable Long itemId) {
		pictureService.deletePicture(itemId);
	}
}
