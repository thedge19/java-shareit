package ru.practicum.shareIt.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = ShareItServer.class)
@WebMvcTest(ItemRequestController.class)
public class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;
    private ItemRequestCreateDto itemRequestCreateDto;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto itemRequestDto2;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;

    @BeforeEach
    void setUp() {
        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("Нужна вещь");
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setRequestorId(1L);
        itemRequestDto.setId(1);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("Описание вещи");

        itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(1);
        itemRequestDto2.setRequestorId(1L);
        itemRequestDto2.setCreated(LocalDateTime.now());
        itemRequestDto2.setDescription("Описание второй вещи");

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Васян")
                .email("vasyan@mail,ru")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Вещь 1")
                .description("Описание вещи 1")
                .available(true)
                .requestId(1)
                .build();

        itemRequestDto.setItems(Collections.singletonList(itemDto));

        itemRequestWithItemsDto = new ItemRequestWithItemsDto();
        itemRequestWithItemsDto.setId(1);
        itemRequestWithItemsDto.setDescription("Описание");
        itemRequestWithItemsDto.setItems(Collections.singletonList(itemDto));
        itemRequestWithItemsDto.setCreated(LocalDateTime.now());
    }

    @Test
    @DisplayName("Создание запроса")
    void addRequest() throws Exception {
        when(itemRequestService.create(any(ItemRequestCreateDto.class), eq(1L))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.requestorId").value(itemRequestDto.getRequestorId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));

        verify(itemRequestService, times(1)).create(any(ItemRequestCreateDto.class), eq(1L));
    }

    @Test
    @DisplayName("Получение всех запросов")
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests()).thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].requestorId").value(itemRequestDto.getRequestorId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()));

        verify(itemRequestService, times(1)).getAllRequests();
    }

    @Test
    @DisplayName("Получение всех запросов пользователя")
    void getAllUserRequests() throws Exception {
        when(itemRequestService.getAllUserRequests(eq(1L))).thenReturn(List.of(itemRequestDto, itemRequestDto2));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id").value(itemRequestDto2.getId()))
                .andExpect(jsonPath("$[1].requestorId").value(itemRequestDto2.getRequestorId()))
                .andExpect(jsonPath("$[1].description").value(itemRequestDto2.getDescription()));
        verify(itemRequestService, times(1)).getAllUserRequests(eq(1L));
    }

    @Test
    @DisplayName("Получение всех вещи по ИД запроса")
    void getItemRequestById() throws Exception {
        when(itemRequestService.getByRequestId(eq(1))).thenReturn(itemRequestWithItemsDto);
        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestWithItemsDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestWithItemsDto.getDescription()))
                .andExpect(jsonPath("$.items.[0].name").value(itemRequestWithItemsDto.getItems().getFirst().getName()))
                .andExpect(jsonPath("$.items.[0].description").value(itemRequestDto.getItems().getFirst().getDescription()));
        verify(itemRequestService, times(1)).getByRequestId(1);
    }
}
