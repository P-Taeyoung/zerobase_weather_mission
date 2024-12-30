package zerobase.weather_mission.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather_mission.WeatherMissionApplication;
import zerobase.weather_mission.domain.DateWeather;
import zerobase.weather_mission.domain.Diary;
import zerobase.weather_mission.repository.DateWeatherRepository;
import zerobase.weather_mission.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;

    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherMissionApplication.class);

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate localDate, String text) {
        logger.info("createDiary");
        diaryRepository.save(Diary.setDiary(getDateWeather(localDate), text));
        logger.info("createDiary finished");
    }

    private DateWeather getDateWeather(LocalDate localDate) {
        List<DateWeather> dateWeathers =
                dateWeatherRepository.findAllByDate(localDate);
        //예외
        if (dateWeathers.isEmpty()) {
            return getWeatherFromApi();
        }

        //정상적으로 처리
        return dateWeathers.get(0);
    }

    private String getWeatherString() {
        String apiUrl =
                "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid="
                        + apiKey;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            BufferedReader br;

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            return response.toString();

        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject main = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", main.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        logger.info("saveWeatherDate");
        dateWeatherRepository.save(getWeatherFromApi());
    }

    public DateWeather getWeatherFromApi () {
        //open weather map에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();

        //받아온 날씨 json파싱하기
        Map<String, Object> parsedWeather =  parseWeather(weatherData);

        DateWeather nowWeather = new DateWeather();
        nowWeather.setDate(LocalDate.now());
        nowWeather.setWeather(parsedWeather.get("main").toString());
        nowWeather.setIcon(parsedWeather.get("icon").toString());
        nowWeather.setTemperature((Double) parsedWeather.get("temp"));
        return nowWeather;
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate localDate) {
        logger.debug("readDiary");
        return diaryRepository.findAllByDate(localDate);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }
}
