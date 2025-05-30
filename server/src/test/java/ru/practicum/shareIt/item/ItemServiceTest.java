package ru.practicum.shareIt.item;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImplementation;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImplementation;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
@ContextConfiguration(classes = ShareItServer.class)
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImplementation itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserServiceImplementation userService;

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void createTest() {
        User user = getUser(1);

        ItemRequest itemRequest = getItemRequest(10);

        Item item = getItem(100L);
        item.setOwner(user);
        item.setItemRequest(itemRequest);

        when(userService.findUserOrNot(eq(user.getId()))).thenReturn(user);
        when(itemRequestService.getItemRequestOrNot(item.getItemRequest().getId())).thenReturn(itemRequest);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto inputDto = ItemMapper.INSTANCE.itemToItemDto(item);

        ItemDto resultDto = itemService.createItem(inputDto, user.getId());

        assertThat(resultDto.getId(), equalTo(item.getId()));
        assertThat(resultDto.getName(), equalTo(item.getName()));
        assertThat(resultDto.getDescription(), equalTo(item.getDescription()));
        assertThat(resultDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(resultDto.getRequestId(), equalTo(itemRequest.getId()));

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(userService, times(1)).findUserOrNot(eq(user.getId()));
        verify(itemRequestService, times(1)).getItemRequestOrNot(eq(item.getItemRequest().getId()));
        verifyNoMoreInteractions(userService, itemRepository, itemRequestService);
    }

    @Test
    void getByIdAsOwnerTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10);
        item.setOwner(owner);

        ItemRequest itemRequest = getItemRequest(10);
        item.setItemRequest(itemRequest);

        Booking lastBooking = getBooking(100, booker, item);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        Booking nextBooking = getBooking(101, booker, item);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        List<Booking> bookingList = Arrays.asList(
                lastBooking,
                nextBooking
        );

        Comment comment1 = getComment(1000);
        comment1.setAuthor(booker);

        Comment comment2 = getComment(1001);
        comment2.setAuthor(booker);

        List<Comment> commentList = Arrays.asList(
                comment1,
                comment2
        );

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemId(eq(item.getId()))).thenReturn(bookingList);
        when(commentRepository.findAllByItemId(eq(item.getId()))).thenReturn(commentList);
        when(itemRequestService.getItemRequestOrNot(item.getItemRequest().getId())).thenReturn(itemRequest);

        ItemDto resultDto = itemService.get(item.getId(), owner.getId());

        assertThat(resultDto.getId(), equalTo(item.getId()));
        assertThat(resultDto.getName(), equalTo(item.getName()));
        assertThat(resultDto.getDescription(), equalTo(item.getDescription()));
        assertThat(resultDto.getAvailable(), equalTo(item.getAvailable()));

        assertThat(resultDto.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(resultDto.getNextBooking().getBookerId(), equalTo(booker.getId()));
        assertThat(resultDto.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(resultDto.getLastBooking().getBookerId(), equalTo(booker.getId()));

        assertThat(resultDto.getComments().size(), equalTo(2));
        assertThat(resultDto.getComments().getFirst().getId(), equalTo(comment1.getId()));
        assertThat(resultDto.getComments().get(0).getText(), equalTo(comment1.getText()));
        assertThat(resultDto.getComments().get(0).getAuthorName(), equalTo(booker.getName()));
        assertThat(resultDto.getComments().get(1).getId(), equalTo(comment2.getId()));
        assertThat(resultDto.getComments().get(1).getText(), equalTo(comment2.getText()));
        assertThat(resultDto.getComments().get(1).getAuthorName(), equalTo(booker.getName()));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findAllByItemId(eq(item.getId()));
        verify(commentRepository, times(1)).findAllByItemId(eq(item.getId()));
        verify(itemRequestService, times(1)).getItemRequestOrNot(eq(item.getItemRequest().getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void getByIdAsNotOwnerTest() {
        User owner = getUser(1);
        User booker = getUser(2);
        User notOwner = getUser(3);

        Item item = getItem(10);
        item.setOwner(owner);

        Booking lastBooking = getBooking(100, booker, item);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        Booking nextBooking = getBooking(101, booker, item);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        Comment comment1 = getComment(1000);
        comment1.setAuthor(booker);

        Comment comment2 = getComment(1001);
        comment2.setAuthor(booker);

        List<Comment> commentList = Arrays.asList(
                comment1,
                comment2
        );

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(eq(item.getId()))).thenReturn(commentList);

        ItemDto resultDto = itemService.get(item.getId(), notOwner.getId());

        assertThat(resultDto.getId(), equalTo(item.getId()));
        assertThat(resultDto.getName(), equalTo(item.getName()));
        assertThat(resultDto.getDescription(), equalTo(item.getDescription()));
        assertThat(resultDto.getAvailable(), equalTo(item.getAvailable()));

        assertThat(resultDto.getNextBooking(), equalTo(null));
        assertThat(resultDto.getLastBooking(), equalTo(null));

        assertThat(resultDto.getComments().size(), equalTo(2));
        assertThat(resultDto.getComments().getFirst().getId(), equalTo(comment1.getId()));
        assertThat(resultDto.getComments().get(0).getText(), equalTo(comment1.getText()));
        assertThat(resultDto.getComments().get(0).getAuthorName(), equalTo(booker.getName()));
        assertThat(resultDto.getComments().get(1).getId(), equalTo(comment2.getId()));
        assertThat(resultDto.getComments().get(1).getText(), equalTo(comment2.getText()));
        assertThat(resultDto.getComments().get(1).getAuthorName(), equalTo(booker.getName()));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(commentRepository, times(1)).findAllByItemId(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void getAllBySearchTextTest() {
        Item item1 = getItem(1);
        Item item2 = getItem(2);

        List<Item> itemList = Arrays.asList(
                item1,
                item2
        );

        when(itemRepository.findBySearchText(eq("Item"))).thenReturn(itemList);

        List<ItemDto> resultDtoList = itemService.search("Item");

        assertThat(resultDtoList.size(), equalTo(2));

        assertThat(resultDtoList.getFirst().getId(), equalTo(item1.getId()));
        assertThat(resultDtoList.getFirst().getName(), equalTo(item1.getName()));
        assertThat(resultDtoList.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(resultDtoList.get(0).getAvailable(), equalTo(item1.getAvailable()));

        assertThat(resultDtoList.get(1).getId(), equalTo(item2.getId()));
        assertThat(resultDtoList.get(1).getName(), equalTo(item2.getName()));
        assertThat(resultDtoList.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(resultDtoList.get(1).getAvailable(), equalTo(item2.getAvailable()));

        verify(itemRepository, times(1)).findBySearchText(eq("Item"));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getAllBySearchTextTest_BlankQuery() {
        List<ItemDto> resultDtoList = itemService.search(" ");

        assertThat(resultDtoList.size(), equalTo(0));

        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void updateTest() {
        ItemDto inputDto = ItemDto.builder().build();

        User owner = getUser(1);

        Item item = getItem(10);
        item.setOwner(owner);

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto resultDto = itemService.updateItem(inputDto, item.getId(), owner.getId());

        assertThat(resultDto.getId(), equalTo(item.getId()));
        assertThat(resultDto.getName(), equalTo(item.getName()));
        assertThat(resultDto.getDescription(), equalTo(item.getDescription()));
        assertThat(resultDto.getAvailable(), equalTo(item.getAvailable()));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void getAllByOwnerIdTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item1 = getItem(10);
        item1.setOwner(owner);

        Item item2 = getItem(11);
        item2.setOwner(owner);

        List<Item> itemList = Arrays.asList(
                item1,
                item2
        );

        Booking item1lastBooking = getBooking(100, booker, item1);
        item1lastBooking.setStart(LocalDateTime.now().minusDays(2));
        item1lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        Booking item1nextBooking = getBooking(101, booker, item1);
        item1nextBooking.setStart(LocalDateTime.now().plusDays(1));
        item1nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        when(itemRepository.findAllByOwnerId(eq(owner.getId()))).thenReturn(itemList);

        List<ItemDto> resultDtoList = itemService.getAll(owner.getId());

        assertThat(resultDtoList.size(), equalTo(2));
        assertThat(resultDtoList.getFirst().getId(), equalTo(item1.getId()));
        assertThat(resultDtoList.getFirst().getName(), equalTo(item1.getName()));
        assertThat(resultDtoList.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(resultDtoList.get(0).getAvailable(), equalTo(item1.getAvailable()));

        assertThat(resultDtoList.get(1).getId(), equalTo(item2.getId()));
        assertThat(resultDtoList.get(1).getName(), equalTo(item2.getName()));
        assertThat(resultDtoList.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(resultDtoList.get(1).getAvailable(), equalTo(item2.getAvailable()));

        assertThat(resultDtoList.get(1).getNextBooking(), equalTo(null));
        assertThat(resultDtoList.get(1).getLastBooking(), equalTo(null));

        verify(itemRepository, times(1)).findAllByOwnerId(eq(owner.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void createCommentTest() {
        User user = getUser(1);
        Item item = getItem(10);

        Comment comment = getComment(100);
        comment.setItem(item);
        comment.setAuthor(user);

        CommentDto createDto = CommentDto.builder().build();

        log.info(createDto.toString());

        when(userService.findUserOrNot(eq(user.getId()))).thenReturn(user);
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        when(bookingRepository.findAllApprovedByItemIdAndBookerId(eq(item.getId()), eq(user.getId()), any(LocalDateTime.class))).thenReturn(Arrays.asList(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto resultDto = itemService.createComment(createDto, user.getId(), item.getId());

        assertThat(resultDto.getId(), equalTo(comment.getId()));
        assertThat(resultDto.getText(), equalTo(comment.getText()));
        assertThat(resultDto.getAuthorName(), equalTo(user.getName()));

        verify(userService, times(1)).findUserOrNot(eq(user.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findAllApprovedByItemIdAndBookerId(eq(item.getId()), eq(user.getId()), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(itemRepository, userService, bookingRepository, commentRepository);
    }

    @Test
    void tryToUpdateByNotOwnerTest() {
        ItemDto inputDto = ItemDto.builder().build();

        User owner = getUser(1);
        User notOwner = getUser(2);

        Item item = getItem(100L);
        item.setOwner(owner);

        assertThrows(ResponseStatusException.class,
                () -> itemService.updateItem(inputDto, item.getId(),
                        notOwner.getId()));
    }


    private User getUser(long id) {
        return User.builder()
                .id(id)
                .name("User " + id)
                .email("user" + id + "@user.com")
                .build();
    }

    private Item getItem(long id) {
        return Item.builder()
                .id(id)
                .name("Item " + id)
                .description("ItemDescription " + id)
                .available(true)
                .build();
    }

    private Comment getComment(long id) {
        return Comment.builder()
                .id(id)
                .text("Comment text " + id)
                .build();
    }

    private ItemRequest getItemRequest(int id) {
        return ItemRequest.builder()
                .id(id)
                .description("Request " + id)
                .build();
    }

    private Booking getBooking(long id, User booker, Item item) {
        return Booking.builder()
                .id(id)
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item)
                .build();
    }
}
