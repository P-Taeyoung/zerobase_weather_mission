package zerobase.weather_mission.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather_mission.domain.Diary;
import zerobase.weather_mission.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @ApiOperation(value = "날씨데이터와 일기 내용을 함께 DB에 저장합니다.", notes = "상세설명")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat
                             (iso = DateTimeFormat.ISO.DATE) LocalDate localDate
            , @RequestBody String text) {
        diaryService.createDiary(localDate, text);
    }

    @ApiOperation("선택한 날짜의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat
            (iso = DateTimeFormat.ISO.DATE)
                          @ApiParam(example = "2024-12-15")
                          LocalDate localDate) {
        return diaryService.readDiary(localDate);
    }

    @ApiOperation("선택한 기간의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat
                                    (iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value = "조회할 기간의 첫번째 날",
                                    example = "2024-12-15")
                            LocalDate startDate,
                            @RequestParam @DateTimeFormat
                                    (iso = DateTimeFormat.ISO.DATE)
                            @ApiParam(value = "조회할 기간의 마지막 날",
                                    example = "2024-12-15")
                            LocalDate endDate) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @ApiOperation(value = "선택한 일기 데이터를 수정합니다.", notes = "해당 날짜에 작성된 일기 중 가장 첫번째 일기 데이터를 가져옵니다.")
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat
                             (iso = DateTimeFormat.ISO.DATE)
                     @ApiParam(example = "2024-12-15")
                     LocalDate date,
                     @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }

    @ApiOperation("선택한 날짜의 모든 일기 데이터를 삭제합니다.")
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat
            (iso = DateTimeFormat.ISO.DATE)
                     @ApiParam(example = "2024-12-15")
                     LocalDate date) {
        diaryService.deleteDiary(date);
    }
}
