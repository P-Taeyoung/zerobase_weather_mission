package zerobase.weather_mission.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather_mission.domain.Memo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {
        //given
        Memo memo = new Memo(10, "test");

        //when
        jpaMemoRepository.save(memo);
        List<Memo> memoList = jpaMemoRepository.findAll();

        //then
        assertFalse(memoList.isEmpty());
    }

    @Test
    void findByIdTest() {
        //given
        Memo newMemo = jpaMemoRepository.save(new Memo(10, "test"));
        //when
        Optional<Memo> result = jpaMemoRepository.findById(newMemo.getId());

        //then
        assertEquals("test",result.get().getText());
    }
}