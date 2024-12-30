package zerobase.weather_mission.error;

public class InvalidDate extends RuntimeException {
    private static final String message = "범위를 초과한 과거 혹은 미래의 날짜입니다.";
    public InvalidDate() {
        super(message);
    }
}
