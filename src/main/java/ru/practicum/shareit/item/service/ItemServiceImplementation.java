package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImplementation implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;
    private final CommentStorage commentStorage;
    private final BookingStorage bookingStorage;

    @Transactional
    @Override
    public ItemDto get(long id, long userId) {
        Item item = findItemOrNot(id);

        ItemDto itemDto = ItemMapper.INSTANCE.itemToItemDto(item);
        if ((item.getOwner().getId() == userId)) {
            addBookingInfo(itemDto);
        }
        addComments(itemDto);

        return itemDto;
    }

    @Transactional
    @Override
    public List<ItemDto> getAll(long userId) {
        List<Item> items = itemStorage.findAllByOwnerId(userId);
        return items.stream().map(ItemMapper.INSTANCE::itemToItemDto).toList();
    }

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        User owner = userService.findUserOrNot(userId);

        Item item = ItemMapper.INSTANCE.itemDtoToItem(itemDto);
        item.setOwner(owner);

        return ItemMapper.INSTANCE.itemToItemDto(itemStorage.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = findItemOrNot(itemId);

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("You are not allowed to update this item");
        }

        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.INSTANCE.itemToItemDto(itemStorage.save(item));
    }

    @Transactional
    @Override
    public List<ItemDto> search(String searchText) {
        if (searchText.isBlank()) {
            return Collections.emptyList();
        }

        searchText = searchText.toLowerCase();

        return itemStorage.findBySearchText(searchText)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper.INSTANCE::itemToItemDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto commentDto, long userId, long itemId) {

        User author = userService.findUserOrNot(userId);
        Item item = findItemOrNot(itemId);
        Comment comment = CommentMapper.INSTANCE.dtoToComment(commentDto, userId, itemId);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        if (bookingStorage.findAllApprovedByItemIdAndBookerId(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("Комментарии можно оставлять только к тем вещам, на которые было бронирование");
        }

        return CommentMapper.INSTANCE.commentToDto(commentStorage.save(comment));
    }

    @Transactional

    @Override
    public Item findItemOrNot(long id) {
        return itemStorage.findById(id).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private void addBookingInfo(ItemDto itemDto) {
        List<Booking> bookings = bookingStorage.findAllByItemId(itemDto.getId());

        Booking nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        Booking lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        itemDto.setNextBooking(nextBooking != null ? ItemDto.ItemBooking.builder()
                .id(nextBooking.getId())
                .bookerId(nextBooking.getBooker().getId())
                .build() : null);
        itemDto.setLastBooking(lastBooking != null ? ItemDto.ItemBooking.builder()
                .id(lastBooking.getId())
                .bookerId(lastBooking.getBooker().getId())
                .build() : null);

    }

    private void addComments(ItemDto itemDto) {
        itemDto.setComments(commentStorage.findAllByItemId(itemDto.getId())
                .stream()
                .map(CommentMapper.INSTANCE::commentToDto)
                .toList());
    }
}
