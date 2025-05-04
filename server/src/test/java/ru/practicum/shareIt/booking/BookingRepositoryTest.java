package ru.practicum.shareIt.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ContextConfiguration(classes = ShareItServer.class)
@DataJpaTest
@Transactional
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void findAllApprovedByItemIdAndUserIdTest() {
        LocalDateTime timestamp = LocalDateTime.now();

        User owner = testEntityManager.persist(User.builder()
                .name("Owner")
                .email("owner@user.com")
                .build());

        User booker1 = testEntityManager.persist(User.builder()
                .name("Booker1")
                .email("booker1@user.com")
                .build());

        User booker2 = testEntityManager.persist(User.builder()
                .name("Booker2")
                .email("booker2@user.com")
                .build());

        Item item1 = testEntityManager.persist(Item.builder()
                .name("Item1")
                .description("Item1")
                .available(true)
                .owner(owner)
                .build());

        Item item2 = testEntityManager.persist(Item.builder()
                .name("Item2")
                .description("Item2")
                .available(true)
                .owner(owner)
                .build());

        testEntityManager.persist(Booking.builder()
                .start(timestamp.minusDays(1))
                .end(timestamp.minusHours(1))
                .item(item1)
                .booker(booker1)
                .status(BookingStatus.APPROVED)
                .build());

        assertThat(bookingRepository.findAllApprovedByItemIdAndBookerId(item1.getId(), booker1.getId(), timestamp)).size().isEqualTo(1);
        assertThat(bookingRepository.findAllApprovedByItemIdAndBookerId(item1.getId(), booker2.getId(), timestamp)).size().isEqualTo(0);

        testEntityManager.persist(Booking.builder()
                .start(timestamp.minusDays(1))
                .end(timestamp.minusHours(1))
                .item(item2)
                .booker(booker1)
                .status(BookingStatus.APPROVED)
                .build());

        assertThat(bookingRepository.findAllApprovedByItemIdAndBookerId(item1.getId(), booker1.getId(), timestamp)).size().isEqualTo(1);

        testEntityManager.persist(Booking.builder()
                .start(timestamp.minusDays(1))
                .end(timestamp.minusHours(1))
                .item(item1)
                .booker(booker1)
                .status(BookingStatus.REJECTED)
                .build());

        assertThat(bookingRepository.findAllApprovedByItemIdAndBookerId(item1.getId(), booker1.getId(), timestamp)).size().isEqualTo(1);

        testEntityManager.persist(Booking.builder()
                .start(timestamp.minusDays(2))
                .end(timestamp.minusHours(2))
                .item(item1)
                .booker(booker1)
                .status(BookingStatus.APPROVED)
                .build());

        assertThat(bookingRepository.findAllApprovedByItemIdAndBookerId(item1.getId(), booker1.getId(), timestamp)).size().isEqualTo(2);

        testEntityManager.persist(Booking.builder()
                .start(timestamp.minusDays(2))
                .end(timestamp.plusHours(2))
                .item(item1)
                .booker(booker1)
                .status(BookingStatus.APPROVED)
                .build());

        assertThat(bookingRepository.findAllApprovedByItemIdAndBookerId(item1.getId(), booker1.getId(), timestamp)).size().isEqualTo(2);
    }
}
