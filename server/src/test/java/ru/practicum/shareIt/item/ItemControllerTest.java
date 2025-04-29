package ru.practicum.shareIt.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createTest() throws Exception {
        long userId = 1;

        ItemDto requestDto = getRequestDto();
        ItemDto responseDto = getItemResponseDto(10);

        when(itemService.createItem(any(ItemDto.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1)).createItem(any(ItemDto.class), eq(userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void createCommentTest() throws Exception {
        long userId = 1;
        long itemId = 10;

        CommentDto requestDto = CommentDto.builder()
                .text("Коммент на щетку")
                .build();

        CommentDto responseDto = CommentDto.builder()
                .id(100L)
                .build();

        when(itemService.createComment(any(CommentDto.class), eq(userId), eq(itemId))).thenReturn(responseDto);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1)).createComment(any(CommentDto.class), eq(userId), eq(itemId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getByIdTest() throws Exception {
        long userId = 1;

        ItemDto responseDto = getItemResponseDto(10);

        when(itemService.get(eq(userId), eq(responseDto.getId()))).thenReturn(responseDto);

        mockMvc.perform(get("/items/" + responseDto.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).get(eq(responseDto.getId()), eq(userId));
    }


    @Test
    void update() throws Exception {
        long userId = 1;
        long itemId = 10;

        ItemDto requestDto = getRequestDto();
        ItemDto responseDto = getItemResponseDto(10);

        when(itemService.updateItem(any(ItemDto.class), eq(itemId), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1)).updateItem(any(ItemDto.class), eq(itemId), eq(userId));
        verifyNoMoreInteractions(itemService);
    }

    private ItemDto getRequestDto() {
        return ItemDto.builder()
                .name("Зубная щетка")
                .description("Почти новая")
                .available(true)
                .build();
    }

    private ItemDto getItemResponseDto(long id) {
        return ItemDto.builder()
                .id(id)
                .build();
    }
}
