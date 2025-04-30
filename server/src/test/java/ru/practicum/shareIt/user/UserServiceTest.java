package ru.practicum.shareIt.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImplementation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = ShareItServer.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImplementation userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void createTest() {
        UserDto inputDto = UserDto.builder().build();

        User user = getUser(1L);

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto resultDto = userService.createUser(inputDto);

        assertThat(resultDto.getId(), equalTo(user.getId()));
        assertThat(resultDto.getName(), equalTo(user.getName()));
        assertThat(resultDto.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getByIdTest() {
        User user = getUser(1);

        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        UserDto resultDto = userService.getUser(user.getId());

        assertThat(resultDto.getId(), equalTo(user.getId()));
        assertThat(resultDto.getName(), equalTo(user.getName()));
        assertThat(resultDto.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllTest() {
        User user1 = getUser(1);
        User user2 = getUser(2);

        List<User> userList = Arrays.asList(
                user1,
                user2
        );

        when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> resultDtoList = userService.getAll();

        assertThat(resultDtoList.getFirst().getId(), equalTo(user1.getId()));
        assertThat(resultDtoList.get(0).getName(), equalTo(user1.getName()));
        assertThat(resultDtoList.get(0).getEmail(), equalTo(user1.getEmail()));

        assertThat(resultDtoList.get(1).getId(), equalTo(user2.getId()));
        assertThat(resultDtoList.get(1).getName(), equalTo(user2.getName()));
        assertThat(resultDtoList.get(1).getEmail(), equalTo(user2.getEmail()));

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateTest() {
        UserDto inputDto = UserDto.builder().build();

        User user = getUser(1);

        System.out.println(user);
        System.out.println(userRepository.findById(user.getId()));

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto resultDto = userService.updateUser(inputDto, user.getId());

        assertThat(resultDto.getId(), equalTo(user.getId()));
        assertThat(resultDto.getName(), equalTo(user.getName()));
        assertThat(resultDto.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteTest() {
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(eq(1L));
    }

    private User getUser(long id) {
        return User.builder()
                .id(1L)
                .name("User " + id)
                .email("Email" + id + "@user.com")
                .build();
    }
}
