package ru.cft.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.cft.dto.seller.SellerCreateDto;
import ru.cft.dto.seller.SellerDto;
import ru.cft.dto.seller.SellerPatchDto;
import ru.cft.dto.seller.SellerUpdateDto;
import ru.cft.exception.ResourceNotFoundException;
import ru.cft.service.SellerService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.cft.configuration.WebPath.API_VERSION_V1;
import static ru.cft.configuration.WebPath.PATH_SELLER;


@WebMvcTest(SellerController.class)
class SellerControllerTest {
    public static final String PATH = API_VERSION_V1 + PATH_SELLER;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SellerService sellerService;

    private static final LocalDateTime FIXED_DATE =
            LocalDateTime.of(2025, 8, 28, 15, 45, 23);

    @Test
    void testThat_findAll_shouldReturnOkWithList() throws Exception {
        var sellers = List.of(
                SellerDto.builder().id(1L).name("John Doe 1").contactInfo("john@doe1.com").registrationDate(FIXED_DATE).build(),
                SellerDto.builder().id(2L).name("John Doe 2").contactInfo("john@doe2.com").registrationDate(FIXED_DATE).build()
        );

        when(this.sellerService.findAll()).thenReturn(sellers);

        this.mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(this.objectMapper.writeValueAsString(sellers)));

        verify(this.sellerService, times(1)).findAll();
    }

    @Test
    void testThat_findById_shouldReturnOkWithSeller() throws Exception {
        long id = 42L;
        var expectedDto = SellerDto.builder().id(1L).name("John Doe").contactInfo("john@doe.com").registrationDate(FIXED_DATE).build();

        when(this.sellerService.findById(id)).thenReturn(expectedDto);

        this.mockMvc.perform(get(PATH + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.id()))
                .andExpect(jsonPath("$.name").value(expectedDto.name()))
                .andExpect(jsonPath("$.contactInfo").value(expectedDto.contactInfo()))
                .andExpect(jsonPath("$.registrationDate").value(expectedDto.registrationDate().toString()));

        verify(this.sellerService, times(1)).findById(id);
    }

    @Test
    void testThat_findById_shouldReturnNotFound_whenSellerDoesNotExist() throws Exception {
        long id = 42L;
        String errorMessage = "There is no seller with id " + id;
        when(this.sellerService.findById(id))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        this.mockMvc.perform(get(PATH + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.value()));

        verify(this.sellerService, times(1)).findById(id);
    }

    @Test
    void testThat_create_shouldReturnCreated_whenSellerIsValid() throws Exception {
        long id = 42;
        var expectedDto = SellerDto.builder()
                .id(id)
                .name("John Doe")
                .contactInfo("john@doe.com")
                .registrationDate(FIXED_DATE)
                .build();
        var requestDto = SellerCreateDto.builder()
                .name(expectedDto.name())
                .contactInfo(expectedDto.contactInfo())
                .build();

        when(this.sellerService.create(any(SellerCreateDto.class))).thenReturn(expectedDto);

        this.mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", PATH + "/" + id))
                .andExpect(jsonPath("$.name").value(expectedDto.name()))
                .andExpect(jsonPath("$.contactInfo").value(expectedDto.contactInfo()));

        verify(this.sellerService, times(1)).create(any(SellerCreateDto.class));
    }

    @Test
    void testThat_create_shouldReturnNotFound_whenSellerDoesNotContainName() throws Exception {
        var invalidRequestDto = SellerCreateDto.builder()
                .contactInfo("john@doe.com")
                .build();

        this.mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: field `name` must not be blank"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(this.sellerService, never()).create(any(SellerCreateDto.class));
    }

    @Test
    void testThat_create_shouldReturnNotFound_whenSellerContainsBlankName() throws Exception {
        var invalidRequestDto = SellerCreateDto.builder()
                .name("")
                .contactInfo("john@doe.com")
                .build();

        this.mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: field `name` must not be blank"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(this.sellerService, never()).create(any(SellerCreateDto.class));
    }

    @Test
    void testThat_create_shouldReturnNotFound_whenSellerDoesNotContainContactInfo() throws Exception {
        var invalidRequestDto = SellerCreateDto.builder()
                .name("John Doe")
                .build();

        this.mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: field `contactInfo` must not be blank"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(this.sellerService, never()).create(any(SellerCreateDto.class));
    }

    @Test
    void testThat_create_shouldReturnNotFound_whenSellerContainsBlankContactInfo() throws Exception {
        var invalidRequestDto = SellerCreateDto.builder()
                .name("John Doe")
                .contactInfo("")
                .build();

        this.mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: field `contactInfo` must not be blank"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(this.sellerService, never()).create(any(SellerCreateDto.class));
    }

    @Test
    void testThat_patch_shouldReturnOk_whenSellerIsValid() throws Exception {
        var patchDto = SellerPatchDto.builder()
                .id(1L)
                .name("John Doe")
                .build();

        var expectedDto = SellerDto.builder()
                .id(1L)
                .name("John Doe")
                .contactInfo("john@doe.com")
                .registrationDate(FIXED_DATE)
                .build();

        when(this.sellerService.patch(any(SellerPatchDto.class))).thenReturn(expectedDto);

        this.mockMvc.perform(patch(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.id()))
                .andExpect(jsonPath("$.name").value(expectedDto.name()))
                .andExpect(jsonPath("$.contactInfo").value(expectedDto.contactInfo()))
                .andExpect(jsonPath("$.registrationDate").value(expectedDto.registrationDate().toString()));

        verify(this.sellerService, times(1)).patch(any(SellerPatchDto.class));
    }

    @Test
    void testThat_patch_shouldReturnNotFound_whenSellerDoesNotExist() throws Exception {
        var patchDto = SellerPatchDto.builder()
                .id(42L)
                .name("John Doe")
                .build();

        String errorMessage = "There is no seller with id " + patchDto.id();
        when(this.sellerService.patch(any(SellerPatchDto.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        this.mockMvc.perform(patch(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.value()));

        verify(this.sellerService, times(1)).patch(any(SellerPatchDto.class));
    }

    @Test
    void testThat_patch_shouldReturnBadRequest_whenSellerDoesNotContainId() throws Exception {
        var invalidPatchDto = SellerPatchDto.builder().build();

        this.mockMvc.perform(patch(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(invalidPatchDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: field `id` must not be null"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(this.sellerService, never()).patch(any());
    }

    @Test
    void testThat_patch_shouldReturnBadRequest_whenSellerContainsBlankName() throws Exception {
        var invalidPatchDto = SellerPatchDto.builder()
                .id(42L)
                .name("")
                .build();

        this.mockMvc.perform(patch(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(invalidPatchDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: field `name` must not exist or be non-blank"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(this.sellerService, never()).patch(any());
    }

    @Test
    void testThat_put_shouldReturnOk_whenSellerIsValid() throws Exception {
        var putDto = SellerUpdateDto.builder()
                .id(42L)
                .name("John Doe")
                .contactInfo("john@doe.com")
                .build();

        var expectedDto = SellerDto.builder()
                .id(42L)
                .name("John Doe")
                .contactInfo("john@doe.com")
                .registrationDate(FIXED_DATE)
                .build();

        when(this.sellerService.put(any(SellerUpdateDto.class))).thenReturn(expectedDto);

        this.mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(putDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedDto.id()))
                .andExpect(jsonPath("$.name").value(expectedDto.name()))
                .andExpect(jsonPath("$.contactInfo").value(expectedDto.contactInfo()))
                .andExpect(jsonPath("$.registrationDate").value(expectedDto.registrationDate().toString()));

        verify(this.sellerService, times(1)).put(any(SellerUpdateDto.class));
    }

    @Test
    void testThat_put_shouldReturnNotFound_whenSellerDoesNotExist() throws Exception {
        var putDto = SellerPatchDto.builder()
                .id(42L)
                .name("John Doe")
                .contactInfo("john@doe.com")
                .build();

        String errorMessage = "There is no seller with id " + putDto.id();
        when(this.sellerService.put(any(SellerUpdateDto.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        this.mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(putDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.value()));

        verify(this.sellerService, times(1)).put(any(SellerUpdateDto.class));
    }

    @Test
    void testThat_put_shouldReturnBadRequest_whenSellerDoesNotContainId() throws Exception {
        var invalidPutDto = SellerUpdateDto.builder()
                .name("John Doe")
                .contactInfo("john@doe.com")
                .build();

        this.mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(invalidPutDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: field `id` must not be null"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(this.sellerService, never()).put(any());
    }

    @Test
    void testThat_put_shouldReturnBadRequest_whenSellerContainsBlankName() throws Exception {
        var invalidPutDto = SellerUpdateDto.builder()
                .id(42L)
                .name("")
                .contactInfo("john@doe.com")
                .build();

        this.mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(invalidPutDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: field `name` must not be blank"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(this.sellerService, never()).put(any());
    }

    @Test
    void testThat_put_shouldReturnBadRequest_whenSellerContainsBlankContactInfo() throws Exception {
        var invalidPutDto = SellerUpdateDto.builder()
                .id(42L)
                .name("John Doe")
                .contactInfo("")
                .build();

        this.mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(invalidPutDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: field `contactInfo` must not be blank"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(this.sellerService, never()).put(any());
    }


    @Test
    void testThat_delete_shouldReturnNoContent_whenSellerExists() throws Exception {
        long id = 42L;

        doNothing().when(this.sellerService).deleteById(id);

        this.mockMvc.perform(delete(PATH + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(this.sellerService, times(1)).deleteById(id);
    }

    @Test
    void testThat_delete_shouldReturnNotFound_whenSellerDoesNotExist() throws Exception {
        long id = 42L;
        String errorMessage = "There is no seller with id " + id;

        doThrow(new ResourceNotFoundException(errorMessage)).when(this.sellerService).deleteById(id);

        this.mockMvc.perform(delete(PATH + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.value()));

        verify(this.sellerService, times(1)).deleteById(id);
    }
}