package com.habitame.api.propertyImage.service;

import com.habitame.api.media.service.ImageStorageService;
import com.habitame.api.property.entity.PropertyEntity;
import com.habitame.api.property.entity.PropertyStatus;
import com.habitame.api.property.service.PropertySecurityService;
import com.habitame.api.property.service.PropertyService;
import com.habitame.api.propertyImage.dto.PropertyImageRequest;
import com.habitame.api.propertyImage.dto.PropertyImageResponse;
import com.habitame.api.propertyImage.entity.PropertyImageEntity;
import com.habitame.api.propertyImage.repository.PropertyImageRepository;
import com.habitame.api.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyImageServiceTest {

    @Mock
    private PropertyImageRepository propertyImageRepository;
    @Mock
    private ImageStorageService imageStorageService;
    @Mock
    private PropertyService propertyService;
    @Mock
    private PropertySecurityService propertySecurityService;

    @InjectMocks
    private PropertyImageService propertyImageService;

    // ------------------- upload -------------------

    @Test
    void upload_ShouldStoreAndPersistImage() throws IOException {
        PropertyEntity property = buildProperty();
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
        PropertyImageRequest request = new PropertyImageRequest(file, false);
        PropertyImageEntity saved = PropertyImageEntity.builder()
                .id(1).property(property).imageUrl("https://example.com/photo.jpg").isMain(false).build();

        when(propertyService.findEntityById(1)).thenReturn(property);
        when(propertyImageRepository.countMainImages(1)).thenReturn(1);
        when(imageStorageService.store(eq(file), eq("properties"))).thenReturn("https://example.com/photo.jpg");
        when(propertyImageRepository.save(any())).thenReturn(saved);

        PropertyImageResponse result = propertyImageService.upload(1, request);

        assertThat(result.imageUrl()).isEqualTo("https://example.com/photo.jpg");
        verify(imageStorageService).store(file, "properties");
        verify(propertyImageRepository).save(any());
    }

    @Test
    void upload_WhenNoMainExists_ShouldSetAsMain() throws IOException {
        PropertyEntity property = buildProperty();
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
        PropertyImageRequest request = new PropertyImageRequest(file, false);
        PropertyImageEntity saved = PropertyImageEntity.builder()
                .id(1).property(property).imageUrl("https://example.com/photo.jpg").isMain(true).build();

        when(propertyService.findEntityById(1)).thenReturn(property);
        when(propertyImageRepository.countMainImages(1)).thenReturn(0);
        when(imageStorageService.store(any(), any())).thenReturn("https://example.com/photo.jpg");
        when(propertyImageRepository.save(any())).thenReturn(saved);

        PropertyImageResponse result = propertyImageService.upload(1, request);

        verify(propertyImageRepository).resetMainImage(1);
        assertThat(result.isMain()).isTrue();
    }

    @Test
    void upload_WhenEmptyFile_ShouldThrow() {
        PropertyEntity property = buildProperty();
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[0]);
        PropertyImageRequest request = new PropertyImageRequest(file, false);

        when(propertyService.findEntityById(1)).thenReturn(property);
        when(propertyImageRepository.countMainImages(1)).thenReturn(0);

        assertThatThrownBy(() -> propertyImageService.upload(1, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Archivo inválido");
    }

    @Test
    void upload_WhenNotImageFile_ShouldThrow() {
        PropertyEntity property = buildProperty();
        MockMultipartFile file = new MockMultipartFile("file", "document.pdf", "application/pdf", new byte[]{1, 2, 3});
        PropertyImageRequest request = new PropertyImageRequest(file, false);

        when(propertyService.findEntityById(1)).thenReturn(property);
        when(propertyImageRepository.countMainImages(1)).thenReturn(1);

        assertThatThrownBy(() -> propertyImageService.upload(1, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Archivo inválido");
    }

    // ------------------- delete -------------------

    @Test
    void delete_ShouldDeleteImageAndStorage() throws IOException {
        PropertyEntity property = buildProperty();
        PropertyImageEntity image = PropertyImageEntity.builder()
                .id(1).property(property).imageUrl("https://example.com/photo.jpg").isMain(false).build();

        when(propertyImageRepository.findById(1)).thenReturn(Optional.of(image));

        propertyImageService.delete(1);

        verify(imageStorageService).delete("https://example.com/photo.jpg");
        verify(propertyImageRepository).deleteById(1);
    }

    @Test
    void delete_WhenNotFound_ShouldThrow() {
        when(propertyImageRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyImageService.delete(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ------------------- findByPropertyId -------------------

    @Test
    void findByPropertyId_ShouldReturnMappedList() {
        PropertyEntity property = buildProperty();
        PropertyImageEntity image = PropertyImageEntity.builder()
                .id(1).property(property).imageUrl("https://example.com/photo.jpg").isMain(true).build();

        when(propertyImageRepository.findAllByPropertyId(1)).thenReturn(List.of(image));

        List<PropertyImageResponse> result = propertyImageService.findByPropertyId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).imageUrl()).isEqualTo("https://example.com/photo.jpg");
    }

    // ------------------- helpers -------------------

    private PropertyEntity buildProperty() {
        return PropertyEntity.builder()
                .id(1)
                .title("Piso").description("Desc").address("Calle")
                .status(PropertyStatus.IN_REVIEW)
                .images(new ArrayList<>()).propertyAmenities(new ArrayList<>())
                .reviews(new ArrayList<>()).rooms(new ArrayList<>())
                .build();
    }
}
