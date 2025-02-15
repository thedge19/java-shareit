package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@SpringBootTest
class ShareItTests {

	private UserStorage userStorage;
	private ItemStorage itemStorage;

	private UserDto userDto1;
	private UserDto userDto2;
	private UserDto userDto3;
	private ItemDto itemDto1;
	private ItemDto itemDto2;
	private ItemDto itemDto3;
    @Autowired
    private InMemoryItemStorage inMemoryItemStorage;

	@BeforeEach
	void contextLoads() {
		userStorage = new InMemoryUserStorage();
		itemStorage = new InMemoryItemStorage();
		userDto1 = makeUser();
		userDto2 = makeAnotherUser();
		userDto3 = makeThirdUser();
		itemDto1 = makeItem();
		itemDto2 = makeAnotherItem();
		itemDto3 = makeThirdItem();
	}

	@Test
	void shouldAddUser() {
		assertThat(userStorage.getAllUsers().size()).isEqualTo(0);
		userStorage.create(userDto1);
		assertThat(userStorage.getAllUsers().size()).isEqualTo(1);
	}

	@Test
	void shouldGetUser() {
		assertThat(userStorage.getAllUsers().size()).isEqualTo(0);
		userStorage.create(userDto1);
		userStorage.create(userDto2);
		assertThat(userStorage.get(2).getName()).isEqualTo("Mike");
	}

	@Test
	void shouldGetAllUsers() {
		assertThat(userStorage.getAllUsers().size()).isEqualTo(0);
		userStorage.create(userDto1);
		userStorage.create(userDto2);
		userStorage.create(userDto3);
		assertThat(userStorage.getAllUsers().size()).isEqualTo(3);
	}

	@Test
	void shouldUpdateUser() {
		assertThat(userStorage.getAllUsers().size()).isEqualTo(0);
		userStorage.create(userDto1);
		assertThat(userStorage.get(1).getName()).isEqualTo("John");
		userStorage.update(userDto2, 1);
		assertThat(userStorage.get(1).getName()).isEqualTo("Mike");
	}

	@Test
	void shouldDeleteUser() {
		assertThat(userStorage.getAllUsers().size()).isEqualTo(0);
		userStorage.create(userDto1);
		userStorage.create(userDto2);
		userStorage.create(userDto3);
		assertThat(userStorage.get(3).getName()).isEqualTo("Kevin");
		userStorage.delete(3);
		assertThat(userStorage.get(3)).isNull();
	}

	@Test
	void shouldAddItem() {
		assertThat(itemStorage.getAllItems().size()).isEqualTo(0);
		userStorage.create(userDto1);
		itemStorage.addItem(itemDto1, userStorage.get(1));
		assertThat(itemStorage.getAllItems().size()).isEqualTo(1);
		assertThat(itemStorage.get(1).getName()).isEqualTo("stuffed woodcock");
	}

	@Test
	void shouldGetAll() {
		assertThat(itemStorage.getAllItems().size()).isEqualTo(0);
		userStorage.create(userDto1);
		itemStorage.addItem(itemDto1, userStorage.get(1));
		userStorage.create(userDto2);
		itemStorage.addItem(itemDto2, userStorage.get(2));
		assertThat(itemStorage.getAll(1).size()).isEqualTo(1);
		assertThat(itemStorage.getAll(2).size()).isEqualTo(1);
	}

	@Test
	void shouldSearch() {
		assertThat(itemStorage.getAllItems().size()).isEqualTo(0);
		userStorage.create(userDto1);
		itemStorage.addItem(itemDto1, userStorage.get(1));
		itemStorage.addItem(itemDto2, userStorage.get(1));
		itemStorage.addItem(itemDto3, userStorage.get(1));
		assertThat(itemStorage.getAllItems().size()).isEqualTo(3);
		List<Item> foundItems1 = itemStorage.search("this");
		assertThat(foundItems1.size()).isEqualTo(3);
		List<Item> foundItems2 = itemStorage.search("woodpecker");
		assertThat(foundItems2.size()).isEqualTo(1);
	}

	private UserDto makeUser()	{
		User user = new User();
		user.setId(null);
		user.setName("John");
		user.setEmail("john@mail.ru");
		return UserMapper.toUserDto(user);
	}

	private UserDto makeAnotherUser() {
		User user = new User();
		user.setId(null);
		user.setName("Mike");
		user.setEmail("mike@mail.ru");
		return UserMapper.toUserDto(user);
	}

	private UserDto makeThirdUser() {
		User user = new User();
		user.setId(null);
		user.setName("Kevin");
		user.setEmail("kevin@mail.ru");
		return UserMapper.toUserDto(user);
	}

	private ItemDto makeItem() {
		Item item = new Item();
		item.setId(null);
		item.setName("stuffed woodcock");
		item.setDescription("This is a stuffed woodcock");
		item.setAvailable(true);
		return ItemMapper.toItemDto(item);
	}

	private ItemDto makeAnotherItem() {
		Item item = new Item();
		item.setId(null);
		item.setName("stuffed scarecrow");
		item.setDescription("This is a stuffed scarecrow");
		item.setAvailable(true);
		return ItemMapper.toItemDto(item);
	}

	private ItemDto makeThirdItem() {
		Item item = new Item();
		item.setId(null);
		item.setName("stuffed woodpecker");
		item.setDescription("This is a stuffed woodpecker");
		item.setAvailable(true);
		return ItemMapper.toItemDto(item);
	}
}
